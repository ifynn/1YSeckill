package com.fynn.oyseckill.app.module.account.user;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.app.module.home.detail.ProductDetailActivity;
import com.fynn.oyseckill.app.module.home.util.ProductUtils;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.model.SeckillVo;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.UserPersonTimes;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.ParamMap;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/7/20.
 */
public class MySeckillFragment extends BaseFragment {

    private static final int LIMIT = 20;
    private SwipeRefreshLayout refreshView;
    private ListView lvMySeckill;
    private TextView tvFooter;
    private List<SeckillVo> issues;
    private PTAdapter ptAdapter;
    private int pageCount = 0;
    private int position;
    private boolean isFinish;
    private boolean isLoading;
    private boolean isLoaded;

    @Override
    public void handleIntent() {
        Bundle params = getArguments();
        if (params != null) {
            position = params.getInt("position", 0);
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_my_seckill;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvMySeckill = $(R.id.lv_my_seckill);
        refreshView = $(R.id.refresh_view);
        tvFooter = new TextView(activity);

        issues = new ArrayList<SeckillVo>();
        ptAdapter = new PTAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        int dp = DensityUtils.dip2px(10);
        tvFooter.setText("");
        tvFooter.setTextColor(0xFFA9A9A9);
        tvFooter.setTextSize(13f);
        tvFooter.setPadding(dp, dp, dp, 0);
        tvFooter.setGravity(Gravity.CENTER);
        lvMySeckill.addFooterView(tvFooter, null, false);
        lvMySeckill.setAdapter(ptAdapter);

        refreshView.setColorSchemeColors(0xFFF85757, 0xFFFF7F24);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                refresh();
            }
        });

        lvMySeckill.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        boolean toBottom = view.getLastVisiblePosition() == view.getCount() - 1;
                        if (!isLoading && !isFinish && toBottom) {
                            query();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoading && !isFinish && !isLoaded) {
            query(true);
        }
    }

    private void refresh() {
        isFinish = false;
        pageCount = 0;
        query();
    }

    private void query() {
        query(false);
    }

    private void query(boolean showDialog) {
        if (showDialog) {
            showProgress();
        }
        isLoading = true;
        BmobQuery<Issue> query = new BmobQuery<Issue>();
        query.addWhereRelatedTo("issues", new BmobPointer(UserHelper.getUser()));
        switch (position) {
            case 0:
            default:
                break;

            case 1:
                query.addWhereContainedIn("announceState", Arrays.asList(0, 1));
                break;

            case 2:
                query.addWhereEqualTo("announceState", 2);
                break;
        }
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * pageCount);
        query.order("-updatedAt");
        query.include("product,succeedSeckill,succeedSeckill.user,product.currentIssue");
        query.findObjects(activity, new FindListener<Issue>() {
            @Override
            public void onSuccess(List<Issue> list) {
                if (list != null && !list.isEmpty()) {
                    if (list.size() < LIMIT) {
                        isFinish = true;
                    }
                    queryPt(list);

                } else {
                    if (pageCount == 0) {
                        issues.clear();
                    }
                    ptAdapter.notifyDataSetChanged();
                    if (issues.isEmpty()) {
                        int dp = DensityUtils.dip2px(10);
                        tvFooter.setPadding(dp, dp, dp, 0);
                        tvFooter.setText("无参与记录");

                    } else {
                        int dp = DensityUtils.dip2px(10);
                        tvFooter.setPadding(dp, 0, dp, 0);
                        tvFooter.setText("无更多参与记录");
                    }

                    isFinish = true;
                    isLoading = false;
                    if (refreshView.isRefreshing()) {
                        refreshView.setRefreshing(false);
                    }
                    hideProgress();
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
                hideProgress();

                LogU.w("我的秒杀获取失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private void queryPt(final List<Issue> list) {
        BmobQuery<UserPersonTimes> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.include("user,issue");

        final int size = list.size();
        List<String> issueIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            issueIds.add(list.get(i).getObjectId());
        }

        query.addWhereContainedIn("issueId", issueIds);
        query.findObjects(activity, new FindListener<UserPersonTimes>() {
            @Override
            public void onSuccess(List<UserPersonTimes> upts) {
                isLoading = false;
                if (!isLoaded) {
                    isLoaded = true;
                }
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
                hideProgress();

                if (pageCount == 0) {
                    issues.clear();
                }
                pageCount++;

                int uSize = upts.size();
                for (int i = 0; i < size; i++) {
                    SeckillVo seckillVo = new SeckillVo();
                    seckillVo.setIssue(list.get(i));

                    for (int j = 0; j < uSize; j++) {
                        if (list.get(i).getObjectId().equals(upts.get(j).getIssueId())) {
                            seckillVo.setUserPersonTimes(upts.get(j));
                            break;
                        }
                    }
                    issues.add(seckillVo);
                }
                ptAdapter.notifyDataSetChanged();

                if (issues.isEmpty()) {
                    int dp = DensityUtils.dip2px(10);
                    tvFooter.setPadding(dp, dp, dp, 0);
                    tvFooter.setText("无参与记录");

                } else {
                    int dp = DensityUtils.dip2px(10);
                    tvFooter.setPadding(dp, 0, dp, 0);
                    if (isFinish) {
                        tvFooter.setText("无更多参与记录");
                    } else {
                        tvFooter.setText("上拉查看更多");
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
                hideProgress();

                LogU.w("我的秒杀 - 参与人次获取失败", "code:" + i, "msg:" + s);
            }
        });
    }

    class PTAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return issues.size();
        }

        @Override
        public Object getItem(int position) {
            return issues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.layout_my_seckill_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Issue issue = issues.get(position).getIssue();
            final Product product = issue.getProduct();

            String imageUrl = product.getImage();
            if (!TextUtils.isEmpty(imageUrl)) {
                ImageUtils.getInstance().display(imageUrl, holder.ivPic, R.drawable.pic_default_square_white);
            } else {
                holder.ivPic.setImageResource(R.drawable.pic_default_square_white);
            }

            holder.tvProductName.setText(product.getName());
            holder.tvIssueNo.setText("期号：" + issue.getIssueNumber());

            int state = issue.getAnnounceState() == null ? 0 : issue.getAnnounceState();
            double price = product.getPrice() == null ? 0d : product.getPrice();
            long pt = issue.getPersonTimes() == null ? 0l : issue.getPersonTimes();
            switch (state) {
                case 0:
                default:
                    holder.pbProgress.setVisibility(View.VISIBLE);
                    holder.tvLuckUser.setVisibility(View.GONE);
                    holder.pbProgress.setProgress(ProductUtils.getProgressInHundred(price, pt));
                    break;

                case 1:
                    holder.pbProgress.setVisibility(View.GONE);
                    holder.tvLuckUser.setVisibility(View.VISIBLE);
                    holder.tvLuckUser.setText("即将揭晓...");
                    break;

                case 2:
                    OysUser user = issue.getSucceedSeckill() == null ? null : issue.getSucceedSeckill().getUser();
                    holder.pbProgress.setVisibility(View.GONE);
                    holder.tvLuckUser.setVisibility(View.VISIBLE);
                    holder.tvLuckUser.setText("获得者：" + UserHelper.getNickname(user));
                    break;
            }

            UserPersonTimes upt = issues.get(position).getUserPersonTimes();
            final String ptStr = (upt == null ? 0 : upt.getPersonTimes()) + "";
            holder.tvMyPt.setText(new SpannableStringBuilder("我已参与：")
                    .append(TextStyleUtils.genColorText(
                            ptStr, getResources().getColor(R.color.colorPrimary)))
                    .append("人次"));

            switch (state) {
                case 0:
                default:
                    holder.tvBuyAgain.setText("继续秒杀");
                    holder.tvBuyAgain.setTextColor(
                            getResources().getColor(R.color.colorPrimary));
                    break;

                case 1:
                case 2:
                    holder.tvBuyAgain.setText("再次购买");
                    holder.tvBuyAgain.setTextColor(
                            getResources().getColor(R.color.black_4F4F4F));
                    break;
            }

            holder.tvBuyAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueNo", product.getCurrentIssue().getIssueNumber());
                    startActivity(ProductDetailActivity.class, params);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueNo", issue.getIssueNumber());
                    startActivity(ProductDetailActivity.class, params);
                }
            });

            holder.tvMyPtDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueObjId", issue.getObjectId());
                    params.put("productName", product.getName());
                    params.put("issueNo", issue.getIssueNumber());
                    params.put("pts", ptStr);
                    startActivity(SeckillNoDetailActivity.class, params);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private ImageView ivPic;
            private TextView tvProductName;
            private TextView tvIssueNo;
            private TextView tvLuckUser;
            private TextView tvMyPt;
            private TextView tvMyPtDetail;
            private TextView tvBuyAgain;
            private ProgressBar pbProgress;

            public ViewHolder(View convertView) {
                ivPic = ViewUtils.findViewById(convertView, R.id.iv_pic);
                tvProductName = ViewUtils.findViewById(convertView, R.id.tv_product_name);
                tvIssueNo = ViewUtils.findViewById(convertView, R.id.tv_issue_no);
                tvLuckUser = ViewUtils.findViewById(convertView, R.id.tv_luck_user);
                tvMyPt = ViewUtils.findViewById(convertView, R.id.tv_pt);
                tvBuyAgain = ViewUtils.findViewById(convertView, R.id.tv_buy_again);
                pbProgress = ViewUtils.findViewById(convertView, R.id.pb_progress);
                tvMyPtDetail = ViewUtils.findViewById(convertView, R.id.tv_pt_detail);
            }
        }
    }
}
