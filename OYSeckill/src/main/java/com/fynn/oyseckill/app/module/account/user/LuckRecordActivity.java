package com.fynn.oyseckill.app.module.account.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.db.UserDb;
import com.fynn.oyseckill.model.OrderState;
import com.fynn.oyseckill.model.ProductType;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.LuckOrder;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.UserPersonTimes;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.common.ParamMap;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/7/26.
 */
public class LuckRecordActivity extends BaseActivity {

    private static final int LIMIT = 5;
    private ListView lvLuck;
    private SwipeRefreshLayout refreshLayout;
    private TextView footView;
    private TextView tvEmpty;
    private List<LuckOrder> luckOrders;
    private LuckAdapter luckAdapter;
    private boolean isLoading;
    private boolean isFinish;
    private int page = 0;

    @Override
    public int getContentResId() {
        return R.layout.activity_luck_list;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvLuck = $(R.id.lv_luck_record);
        refreshLayout = $(R.id.refresh_view);
        footView = new TextView(me);
        tvEmpty = $(R.id.tv_empty);

        luckOrders = new ArrayList<>();
        luckAdapter = new LuckAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        int dp = DensityUtils.dip2px(10);
        footView.setText("上拉加载更多");
        footView.setTextColor(0xFFA9A9A9);
        footView.setTextSize(13f);
        footView.setPadding(dp, 0, dp, 0);
        footView.setGravity(Gravity.CENTER);
        lvLuck.addFooterView(footView, null, false);
        lvLuck.setAdapter(luckAdapter);

        refreshLayout.setColorSchemeColors(0xFFF85757, 0xFFFF7F24);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                refresh();
            }
        });

        lvLuck.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        boolean toBottom = view.getLastVisiblePosition() == view.getCount() - 1;
                        if (!isFinish && !isLoading && toBottom) {
                            fetchData();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        updateEmptyView();
        fetchData();

        register(Event.EVENT_ORDER_SHARED, Event.EVENT_CONFIRM_ADDRESS);
    }

    private void refresh() {
        isFinish = false;
        page = 0;
        fetchData();
    }

    private void fetchData() {
        isLoading = true;

        BmobQuery<LuckOrder> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * page);
        query.include("issue,issue.product,issue.luckUserPt");
        query.findObjects(me, new FindListener<LuckOrder>() {
            @Override
            public void onSuccess(List<LuckOrder> list) {
                isLoading = false;
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (page == 0) {
                    luckOrders.clear();
                }

                if (list != null && !list.isEmpty()) {
                    luckOrders.addAll(list);
                    if (list.size() < LIMIT) {
                        isFinish = true;
                    } else {
                        page++;
                    }
                } else {
                    isFinish = true;
                }

                luckAdapter.notifyDataSetChanged();
                updateFootView();
                updateEmptyView();
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateFootView() {
        if (isFinish) {
            footView.setText("多多参与，让更多幸运找上门~");
        } else {
            footView.setText("上拉加载更多");
        }
    }

    private void updateEmptyView() {
        if (lvLuck.getAdapter().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        if (UserDb.isReward()) {
            UserDb.putReward(false);
        }
        super.onResume();
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (Event.EVENT_ORDER_SHARED.equals(action) || Event.EVENT_CONFIRM_ADDRESS.equals(action)) {
            refresh();
        }
    }

    class LuckAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return luckOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return luckOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_luck_record_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LuckOrder luckOrder = (LuckOrder) getItem(position);
            final Issue issue = luckOrder.getIssue();
            OysUser user = luckOrder.getUser();
            if (issue == null || user == null) {
                holder.ivPic.setImageBitmap(null);
                holder.tvProName.setText("");
                holder.tvIssueNo.setText("");
                holder.tvLuckNo.setText("");
                holder.tvTotalPt.setText("");
                holder.tvMyPt.setText("");
                holder.tvFinishTime.setText("");
                holder.tvDesc.setText("");
                holder.tvState.setVisibility(View.GONE);
                return convertView;
            }

            final Product product = issue.getProduct();

            //商品图片
            final String imageUrl = product.getImage();
            if (!TextUtils.isEmpty(imageUrl)) {
                ImageUtils.getInstance().display(imageUrl, holder.ivPic, R.drawable.pic_default_square_white);
            } else {
                holder.ivPic.setImageResource(R.drawable.pic_default_square_white);
            }

            //商品名称
            holder.tvProName.setText(product.getName());

            //期号
            holder.tvIssueNo.setText("期号：" + issue.getIssueNumber());

            //幸运号码
            holder.tvLuckNo.setText(new SpannableStringBuilder("幸运号码：").append(
                    TextStyleUtils.genColorText(issue.getSucceedSeckillNo() + "", 0xFFF85757)));

            //总需人次
            holder.tvTotalPt.setText("总需：" +
                    (issue.getTotalPersonTimes() == null ? 0 : issue.getTotalPersonTimes()) + "人次");

            //我已参与人次
            UserPersonTimes upt = issue.getLuckUserPt();
            holder.tvMyPt.setText("本期参与：" + (upt == null ? 0 : upt.getPersonTimes()) + "人次");

            //揭晓时间
            holder.tvFinishTime.setText("揭晓时间：" +
                    (issue.getAnnouncedAt() == null ? "null" : issue.getAnnouncedAt().getDate()));

            final int state = luckOrder.getState() == null ? 0 : luckOrder.getState();
            final String type = TextUtils.isEmpty(product.getType()) ?
                    ProductType.TYPE_ENTITY_PRODUCT : product.getType();

            if (type.equalsIgnoreCase(ProductType.TYPE_VIRTUAL_PRODUCT)) {
                handleVirtual(holder, state);
            } else {
                handleEntity(holder, state);
            }

            holder.tvState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (state) {
                        case OrderState.STATE_RECEIVED:
                            if (type.equalsIgnoreCase(ProductType.TYPE_VIRTUAL_PRODUCT)) {
                                gotoOrderStatus(luckOrder, holder);
                            } else {
                                ParamMap<String, Object> params = new ParamMap<String, Object>();
                                params.put("picUrl", imageUrl);
                                params.put("productName", product.getName());
                                params.put("issueNo", issue.getIssueNumber());
                                boolean isv = ProductType.TYPE_VIRTUAL_PRODUCT.equalsIgnoreCase(product.getType());
                                params.put("isVirtual", isv);
                                params.put("issueObjId", issue.getObjectId());
                                params.put("productObjId", product.getObjectId());
                                params.put("orderObjId", luckOrder.getObjectId());
                                startActivity(OrderShareEditActivity.class, params);
                            }
                            break;

                        default:
                            gotoOrderStatus(luckOrder, holder);
                            break;
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoOrderStatus(luckOrder, holder);
                }
            });

            return convertView;
        }

        private void gotoOrderStatus(LuckOrder order, ViewHolder holder) {
            ParamMap<String, Object> params = new ParamMap<String, Object>();
            params.put("picUrl", order.getIssue().getProduct().getImage());
            params.put("productName", holder.tvProName.getText().toString());
            params.put("issueNo", holder.tvIssueNo.getText().toString());
            params.put("luckNo", holder.tvLuckNo.getText().toString());
            params.put("totalPt", holder.tvTotalPt.getText().toString());
            params.put("myPt", holder.tvMyPt.getText().toString());
            params.put("annTime", holder.tvFinishTime.getText().toString());
            params.put("orderId", order.getObjectId());
            boolean isv = ProductType.TYPE_VIRTUAL_PRODUCT.equalsIgnoreCase(
                    order.getIssue().getProduct().getType());
            params.put("isVirtual", isv);
            params.put("issueObjId", order.getIssue().getObjectId());
            params.put("productObjId", order.getIssue().getProduct().getObjectId());
            startActivity(OrderStatusActivity.class, params);
        }

        private void handleVirtual(ViewHolder holder, int state) {
            switch (state) {
                case OrderState.STATE_GAIN_PRODUCT:
                default:
                    holder.tvDesc.setText("已获得商品，正在审核...");
                    holder.tvState.setVisibility(View.GONE);
                    break;

                case OrderState.STATE_WAIT_FOR_DELIVER:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("待发放");
                    holder.tvState.setTextColor(0xFFFF7F24);
                    holder.tvState.setBackgroundColor(Color.WHITE);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_DELIVERED:
                case OrderState.STATE_RECEIVED:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("查看卡密");
                    holder.tvState.setTextColor(Color.WHITE);
                    holder.tvState.setBackgroundResource(R.drawable.btn_corner_red_selector);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_SHARED:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("已完成");
                    holder.tvState.setTextColor(0xFF00CD00);
                    holder.tvState.setBackgroundColor(Color.WHITE);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private void handleEntity(ViewHolder holder, int state) {
            switch (state) {
                case OrderState.STATE_GAIN_PRODUCT:
                default:
                    holder.tvDesc.setText("已获得商品，正在审核...");
                    holder.tvState.setVisibility(View.GONE);
                    break;

                case OrderState.STATE_WAIT_FOR_CONFIRM_ADDRESS:
                    holder.tvDesc.setText("7日内未确认收货地址，视为自动放弃");
                    holder.tvState.setText("确认收货地址");
                    holder.tvState.setTextColor(Color.WHITE);
                    holder.tvState.setBackgroundResource(R.drawable.btn_corner_red_selector);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_CONFIRM_ADDRESS_LIMIT:
                    holder.tvDesc.setText("未在7日内确认收货地址，已放弃");
                    holder.tvState.setVisibility(View.GONE);
                    break;

                case OrderState.STATE_WAIT_FOR_DELIVER:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("待发货");
                    holder.tvState.setTextColor(0xFFFF7F24);
                    holder.tvState.setBackgroundColor(Color.WHITE);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_DELIVERED:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("待签收");
                    holder.tvState.setTextColor(0xFFFF7F24);
                    holder.tvState.setBackgroundColor(Color.WHITE);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_RECEIVED:
                    holder.tvDesc.setText("已签收，赶快去晒单吧");
                    holder.tvState.setText("晒单评价");
                    holder.tvState.setTextColor(Color.WHITE);
                    holder.tvState.setBackgroundResource(R.drawable.btn_corner_red_selector);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;

                case OrderState.STATE_SHARED:
                    holder.tvDesc.setText("");
                    holder.tvState.setText("已完成");
                    holder.tvState.setTextColor(0xFF00CD00);
                    holder.tvState.setBackgroundColor(Color.WHITE);
                    holder.tvState.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class ViewHolder {
        private ImageView ivPic;
        private TextView tvProName;
        private TextView tvIssueNo;
        private TextView tvLuckNo;
        private TextView tvTotalPt;
        private TextView tvMyPt;
        private TextView tvFinishTime;
        private TextView tvDesc;
        private TextView tvState;

        public ViewHolder(View v) {
            ivPic = ViewUtils.findViewById(v, R.id.iv_pic);
            tvProName = ViewUtils.findViewById(v, R.id.tv_product_name);
            tvIssueNo = ViewUtils.findViewById(v, R.id.tv_issue_no);
            tvLuckNo = ViewUtils.findViewById(v, R.id.tv_luck_no);
            tvTotalPt = ViewUtils.findViewById(v, R.id.tv_total_pt);
            tvMyPt = ViewUtils.findViewById(v, R.id.tv_my_pt);
            tvFinishTime = ViewUtils.findViewById(v, R.id.tv_finish_time);
            tvDesc = ViewUtils.findViewById(v, R.id.tv_desc);
            tvState = ViewUtils.findViewById(v, R.id.tv_state);
        }
    }
}
