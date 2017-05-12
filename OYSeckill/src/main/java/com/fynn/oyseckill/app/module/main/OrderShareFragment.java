package com.fynn.oyseckill.app.module.main;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.app.module.find.OrderShareDetailActivity;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OrderShare;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.PauseOnScrollListener;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.view.SheetUtils;
import com.fynn.oyseckill.widget.CircleImageView;
import com.fynn.oyseckill.widget.RectImageView;
import com.fynn.oyseckill.widget.dialog.IPrompter;

import org.appu.common.ParamMap;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;
import org.appu.model.Result;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by fynn on 16/7/17.
 */
public class OrderShareFragment extends BaseFragment {

    private static final int LIMIT = 10;
    private List<OrderShare> orderShares;
    private ShareAdapter adapter;
    private ListView lvShare;
    private TextView footView;
    private SwipeRefreshLayout refreshView;
    private LinearLayout llEmpty;
    private boolean isLoading;
    private boolean isFinish;
    private int page = 0;

    private boolean showTitlebar = true;
    private String productObjId;
    private String userObjId;
    private boolean lazyLoad;
    private String from;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            productObjId = (String) params.get("productObjId");
            userObjId = (String) params.get("userObjId");

            Object st = params.get("showTitlebar");
            if (st != null) {
                showTitlebar = (boolean) st;
            }

            Object ll = params.get("lazyLoad");
            if (ll != null) {
                lazyLoad = (boolean) ll;
            }
        }

        Bundle argue = getArguments();
        if (argue != null) {
            lazyLoad = argue.getBoolean("lazyLoad", false);
            from = argue.getString("from");
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_order_share;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        orderShares = new ArrayList<OrderShare>();
        adapter = new ShareAdapter();

        lvShare = $(R.id.lv_order_share);
        refreshView = $(R.id.refresh_view);
        llEmpty = $(R.id.ll_empty);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        if (!showTitlebar) {
            titlebar.setVisibility(View.GONE);
        }

        int dp = DensityUtils.dip2px(5);
        footView = new TextView(activity);

        footView.setText("上拉加载更多");
        footView.setTextColor(0xFFA9A9A9);
        footView.setTextSize(13f);
        footView.setPadding(dp, dp, dp, dp);
        footView.setGravity(Gravity.CENTER);

        lvShare.addFooterView(footView, null, false);
        lvShare.setAdapter(adapter);
        updateEmptyDataState();

        lvShare.setOnScrollListener(new PauseOnScrollListener(new AbsListView.OnScrollListener() {
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
        }));

        refreshView.setColorSchemeResources(R.color.colorPrimary);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }

                refresh();
            }
        });

        if (!lazyLoad) {
            refresh();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        isLoading = false;
        if (refreshView.isRefreshing()) {
            refreshView.setRefreshing(false);
        }

        if (!isVisible()) {
            return;
        }

        Result<List<OrderShare>> result = (Result<List<OrderShare>>) msg.obj;
        if (result != null && result.isOk()) {
            if (result.hasData()) {
                List<OrderShare> oss = result.getData();
                if (page == 0) {
                    orderShares.clear();
                }

                if (!oss.isEmpty()) {
                    if (oss.size() < LIMIT) {
                        isFinish = true;
                        footView.setText("看完了，有没有心动~");

                    } else {
                        page++;
                        footView.setText("上拉加载更多");
                    }

                    orderShares.addAll(oss);

                } else {
                    isFinish = true;
                    footView.setText("看完了~");
                }

                adapter.notifyDataSetChanged();
                updateEmptyDataState();
            }

        } else {
            LogU.e("晒单获取失败",
                    "code:" + result.getCode(),
                    "msg:" + result.getMessage());
        }

    }

    private void refresh() {
        isFinish = false;
        page = 0;
        fetchData();
    }

    private void fetchData() {
        isLoading = true;

        BmobQuery<OrderShare> query = new BmobQuery<OrderShare>();
        query.order("-createdAt");
        query.include("issue,issue.product,user");
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * page);

        if (!"my.order.share".equals(from)) {
            if ("mng.order.share.access".equalsIgnoreCase(from)) {
                query.addWhereEqualTo("access", -1); //待审核的，用于运营
            } else {
                query.addWhereEqualTo("access", 1); //审核通过的
            }
        }

        //通过商品id查询
        if (!TextUtils.isEmpty(productObjId)) {
            Product product = new Product();
            product.setObjectId(productObjId);
            query.addWhereEqualTo("product", new BmobPointer(product));
        }

        //通过用户查询
        if (!TextUtils.isEmpty(userObjId)) {
            OysUser user = new OysUser();
            user.setObjectId(userObjId);
            query.addWhereEqualTo("user", new BmobPointer(user));
        }

        query.findObjects(activity, new FindListener<OrderShare>() {
            @Override
            public void onSuccess(List<OrderShare> list) {
                Result<List<OrderShare>> result = new Result<List<OrderShare>>();
                result.setCode(Result.RESULT_OK);
                result.setData(list);
                Message msg = handler.obtainMessage();
                msg.obj = result;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {
                Result<List<OrderShare>> result = new Result<List<OrderShare>>();
                result.setCode(String.valueOf(i));
                result.setMessage(s);
                Message msg = handler.obtainMessage();
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isLoading) {
            refresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoading) {
            refresh();
        }
    }

    private void updateEmptyDataState() {
        if (adapter.isEmpty()) {
            refreshView.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            refreshView.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    class ShareAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ShareAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return orderShares.size();
        }

        @Override
        public Object getItem(int position) {
            return orderShares.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mInflater.inflate(R.layout.layout_order_share_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OrderShare os = orderShares.get(position);
            OysUser user = os.getUser();
            Issue issue = os.getIssue();
            String desc = os.getDesc();
            List<String> picUrls = os.getPictures();
            String time = os.getCreatedAt();
            BmobFile profile = user.getProfile();
            Product product = issue.getProduct();

            if (profile == null || TextUtils.isEmpty(profile.getFileUrl(activity))) {
                holder.civUserProfile.setImageResource(R.drawable.icon_user_profile_normal);
            } else {
                ImageUtils.getInstance().display(profile.getFileUrl(activity), holder.civUserProfile,
                        R.drawable.icon_user_profile_normal);
            }

            holder.tvUsername.setText(UserHelper.getNickname(user));
            holder.tvTime.setText(time);
            holder.tvIssueNo.setText("第" + issue.getIssueNumber() + "期");
            holder.tvProductName.setText(product.getName());
            holder.tvCommentDesc.setText(desc);

            ViewGroup prt = (ViewGroup) holder.rivLeftImage.getParent();
            if (picUrls != null && !picUrls.isEmpty()) {
                prt.setVisibility(View.VISIBLE);
                if (picUrls.size() == 1) {
                    holder.rivLeftImage.setVisibility(View.VISIBLE);
                    holder.rivMiddleImage.setVisibility(View.INVISIBLE);
                    holder.rivRightImage.setVisibility(View.INVISIBLE);
                    ImageUtils.getInstance().display(picUrls.get(0), holder.rivLeftImage,
                            R.drawable.pic_default_square_gray);

                } else if (picUrls.size() == 2) {
                    holder.rivLeftImage.setVisibility(View.VISIBLE);
                    holder.rivMiddleImage.setVisibility(View.VISIBLE);
                    holder.rivRightImage.setVisibility(View.INVISIBLE);
                    ImageUtils.getInstance().display(picUrls.get(0), holder.rivLeftImage,
                            R.drawable.pic_default_square_gray);
                    ImageUtils.getInstance().display(picUrls.get(1), holder.rivMiddleImage,
                            R.drawable.pic_default_square_gray);

                } else if (picUrls.size() >= 3) {
                    holder.rivLeftImage.setVisibility(View.VISIBLE);
                    holder.rivMiddleImage.setVisibility(View.VISIBLE);
                    holder.rivRightImage.setVisibility(View.VISIBLE);
                    ImageUtils.getInstance().display(picUrls.get(0), holder.rivLeftImage,
                            R.drawable.pic_default_square_gray);
                    ImageUtils.getInstance().display(picUrls.get(1), holder.rivMiddleImage,
                            R.drawable.pic_default_square_gray);
                    ImageUtils.getInstance().display(picUrls.get(2), holder.rivRightImage,
                            R.drawable.pic_default_square_gray);
                }
            } else {
                prt.setVisibility(View.GONE);
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("objId", os.getObjectId());
                    startActivity(OrderShareDetailActivity.class, params);
                }
            });

            if ("mng.order.share.access".equalsIgnoreCase(from)) {
                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SheetUtils.showItemSheet(activity, new IPrompter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Dialog dialog, View view, int position) {
                                dialog.dismiss();
                                OrderShare osTmp = new OrderShare();
                                osTmp.setObjectId(os.getObjectId());
                                osTmp.setAccess(position);
                                osTmp.update(activity, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        showShortToast("操作成功");
                                        refresh();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showShortToast("操作失败");
                                    }
                                });

                            }
                        }, null, "不通过审核", "通过审核");
                        return true;
                    }
                });
            }

            return convertView;
        }
    }

    class ViewHolder {

        private CircleImageView civUserProfile;
        private TextView tvUsername;
        private TextView tvTime;
        private TextView tvIssueNo;
        private TextView tvProductName;
        private TextView tvCommentDesc;
        private RectImageView rivLeftImage;
        private RectImageView rivMiddleImage;
        private RectImageView rivRightImage;
        private CardView cardView;

        public ViewHolder(View view) {
            civUserProfile = ViewUtils.findViewById(view, R.id.civ_user_profile);
            tvUsername = ViewUtils.findViewById(view, R.id.tv_username);
            tvTime = ViewUtils.findViewById(view, R.id.tv_time);
            tvIssueNo = ViewUtils.findViewById(view, R.id.tv_issue_no);
            tvProductName = ViewUtils.findViewById(view, R.id.tv_product_name);
            tvCommentDesc = ViewUtils.findViewById(view, R.id.tv_comment_desc);
            rivLeftImage = ViewUtils.findViewById(view, R.id.riv_left_image);
            rivMiddleImage = ViewUtils.findViewById(view, R.id.riv_middle_image);
            rivRightImage = ViewUtils.findViewById(view, R.id.riv_right_image);
            cardView = ViewUtils.findViewById(view, R.id.card_view);
        }
    }
}
