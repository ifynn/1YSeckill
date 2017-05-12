package com.fynn.oyseckill.app.module.home.detail;

import android.os.Bundle;
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
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.ParamMap;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fynn on 16/7/16.
 * 往期揭晓页
 */
public class ForwardAnnActivity extends BaseActivity {

    private static final int LIMIT = 10;
    private ListView lvForwardAnn;
    private TextView tvEmpty;
    private TextView footView;
    private boolean isLoading;
    private boolean isFinish;
    private int page = 0;

    private List<Issue> issues;
    private ForwardAnnAdapter annAdapter;
    private String productObjectId;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            productObjectId = (String) params.get("productObjectId");
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_forward_announced;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvForwardAnn = $(R.id.lv_forward_ann);
        tvEmpty = $(R.id.tv_empty);

        issues = new ArrayList<Issue>();
        annAdapter = new ForwardAnnAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        int dp = DensityUtils.dip2px(5);

        footView = new TextView(me);
        footView.setText("上拉加载更多");
        footView.setTextSize(13);
        footView.setTextColor(0xFFA9A9A9);
        footView.setPadding(dp, dp, dp, dp);
        footView.setGravity(Gravity.CENTER);

        lvForwardAnn.addFooterView(footView, null, false);
        lvForwardAnn.setEmptyView(tvEmpty);
        lvForwardAnn.setAdapter(annAdapter);

        lvForwardAnn.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        boolean toBottom = view.getLastVisiblePosition() == view.getCount() - 1;
                        if (!isFinish && !isLoading && toBottom) {
                            query();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        query();
    }

    private void query() {
        isLoading = true;

        Product product = new Product();
        product.setObjectId(productObjectId);

        BmobQuery<Issue> query = new BmobQuery<Issue>();
        query.addWhereEqualTo("product", new BmobPointer(product));
        query.addWhereNotEqualTo("announceState", SeckillState.SECKILLING.getValue());
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * page);
        query.include("succeedSeckill,succeedSeckill.user,luckUserPt");
        query.findObjects(me, new FindListener<Issue>() {
            @Override
            public void onSuccess(List<Issue> list) {
                if (list != null && !list.isEmpty()) {
                    issues.addAll(list);
                    annAdapter.notifyDataSetChanged();

                    if (list.size() < LIMIT) {
                        isFinish = true;
                    } else {
                        page++;
                    }
                } else {
                    isFinish = true;
                }

                if (issues.size() <= 0) {
                    footView.setText("无往期揭晓记录");
                } else {
                    if (isFinish) {
                        footView.setText("没有更多了~");
                    } else {
                        footView.setText("继续上拉加载更多");
                    }
                }

                isLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                LogU.e("获取往期揭晓记录失败", "code:" + i, "msg:" + s);
            }
        });

    }

    class ForwardAnnAdapter extends BaseAdapter {

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
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(me).inflate(R.layout.layout_forward_ann_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Issue issue = issues.get(position);

            holder.tvIssueNo.setText("期号 " + issue.getIssueNumber());

            Integer state = issue.getAnnounceState();
            if (state == SeckillState.ANNOUNCING.getValue()) {
                holder.llUserInfo.setVisibility(View.GONE);
                holder.llDivider.setVisibility(View.GONE);
                holder.tvAnnTime.setText("即将揭晓...");
            } else {
                OysUser user = issue.getSucceedSeckill().getUser();

                holder.llUserInfo.setVisibility(View.VISIBLE);
                holder.llDivider.setVisibility(View.VISIBLE);
                holder.tvAnnTime.setText(issue.getAnnouncedAt().getDate());

                holder.tvUsername.setText("幸运用户：" + UserHelper.getNickname(user));
                holder.tvUserId.setText("用户ID：" + user.getUserId());
                holder.tvLuckNo.setText("幸运号码：" + issue.getSucceedSeckillNo());
                holder.tvIP.setText("IP：" + issue.getSucceedSeckill().getIp());
                holder.tvPt.setText("本期参与：" + issue.getLuckUserPt().getPersonTimes() + "人次");
            }

            return convertView;
        }

        class ViewHolder {

            private TextView tvIssueNo;
            private TextView tvAnnTime;
            private TextView tvUsername;
            private TextView tvUserId;
            private TextView tvLuckNo;
            private TextView tvIP;
            private TextView tvPt;
            private LinearLayout llDivider;
            private LinearLayout llUserInfo;

            public ViewHolder(View view) {
                tvIssueNo = ViewUtils.findViewById(view, R.id.tv_issue_no);
                tvAnnTime = ViewUtils.findViewById(view, R.id.tv_ann_time);
                tvUsername = ViewUtils.findViewById(view, R.id.tv_username);
                tvUserId = ViewUtils.findViewById(view, R.id.tv_user_id);
                tvLuckNo = ViewUtils.findViewById(view, R.id.tv_luck_no);
                tvIP = ViewUtils.findViewById(view, R.id.tv_ip);
                tvPt = ViewUtils.findViewById(view, R.id.tv_pt);
                llDivider = ViewUtils.findViewById(view, R.id.divider);
                llUserInfo = ViewUtils.findViewById(view, R.id.ll_user_info);
            }
        }
    }
}
