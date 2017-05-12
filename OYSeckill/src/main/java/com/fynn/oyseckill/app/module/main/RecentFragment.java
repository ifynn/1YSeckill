package com.fynn.oyseckill.app.module.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.app.module.home.detail.ProductDetailActivity;
import com.fynn.oyseckill.app.module.home.detail.SeckillState;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.Seckill;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.Titlebar;

import org.appu.common.ParamMap;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;

/**
 * Created by fynn on 16/4/24.
 */
public class RecentFragment extends BaseFragment {

    private Titlebar titlebar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llEmpty;

    private List<Issue> issues;
    private AnnAdapter annAdapter;

    private int page = 0;
    private int LIMIT = 10;

    private boolean isLoading;
    private boolean isFinish;

    @Override
    public int getContentResId() {
        return R.layout.fragment_recent;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titlebar = $(R.id.titlebar);
        recyclerView = $(R.id.rv_issue);
        swipeRefresh = $(R.id.swipe_refresh);
        llEmpty = $(R.id.ll_empty);

        issues = new ArrayList<Issue>();
        annAdapter = new AnnAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(annAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                        int totalItemCount = lm.getItemCount();
                        int[] lastVisibleItems = null;
                        lastVisibleItems = lm.findLastCompletelyVisibleItemPositions(lastVisibleItems);

                        boolean toBottom = contain(lastVisibleItems, totalItemCount - 1);
                        if (toBottom && !isLoading && !isFinish) {
                            fetchData(false);
                        }

                        ImageUtils.getInstance().getImageLoader().resume();
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        ImageUtils.getInstance().getImageLoader().pause();
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                        ImageUtils.getInstance().getImageLoader().pause();
                        break;
                }
            }
        });

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                refresh();
            }
        });

        updateEmptyState();
    }

    private void refresh() {
        page = 0;
        isFinish = false;
        fetchData(false);
    }

    private void fetchData(boolean showDialog) {
        if (showDialog) {
            showProgress();
        }
        isLoading = true;
        Bmob.getServerTime(activity, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {
                queryRecent(new Date(l * 1000));
            }

            @Override
            public void onFailure(int i, String s) {
                isLoading = false;
                hideProgress();
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                LogU.e("获取最近揭晓 - 查询服务器时间失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private void updateEmptyState() {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) {
            llEmpty.setVisibility(View.VISIBLE);
            swipeRefresh.setVisibility(View.GONE);
        } else {
            llEmpty.setVisibility(View.GONE);
            swipeRefresh.setVisibility(View.VISIBLE);
        }
    }

    private boolean contain(int[] ints, int value) {
        int length = ints.length;
        for (int i = 0; i < length; i++) {
            if (ints[i] == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isLoading) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        if (isVisible()) {
            onHiddenChanged(false);
        }
        super.onResume();
    }

    private void queryRecent(Date date) {
        BmobQuery<Issue> q1 = new BmobQuery<Issue>();
        q1.addWhereEqualTo("announceState", 1); //即将揭晓

        BmobQuery<Issue> q2 = new BmobQuery<Issue>();
        q2.addWhereEqualTo("announceState", 2); //已揭晓
        Date addDate = DateTimeUtils.addDate(date, Calendar.DAY_OF_MONTH, -2);
        q2.addWhereGreaterThanOrEqualTo("announcedAt", new BmobDate(addDate));

        List<BmobQuery<Issue>> qList = new ArrayList<BmobQuery<Issue>>();
        qList.add(q1);
        qList.add(q2);

        BmobQuery<Issue> query = new BmobQuery<Issue>();
        query.setSkip(LIMIT * page);
        query.setLimit(LIMIT);
        query.include("product,succeedSeckill,succeedSeckill.user");
        query.or(qList);
        query.order("-updatedAt");
        query.findObjects(getActivity(), new FindListener<Issue>() {
            @Override
            public void onSuccess(List<Issue> list) {
                isLoading = false;
                hideProgress();
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }

                if (!isVisible()) {
                    return;
                }

                if (page == 0) {
                    issues.clear();
                }

                if (list != null && !list.isEmpty()) {
                    issues.addAll(list);
                    annAdapter.notifyDataSetChanged();

                    page++;
                    if (list.size() < LIMIT) {
                        isFinish = true;
                    }

                } else {
                    isFinish = true;
                }

                updateEmptyState();
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                hideProgress();
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                LogU.e("获取最近揭晓失败", "code:" + i, "msg:" + s);
            }
        });
    }

    class AnnAdapter extends RecyclerView.Adapter<RecentViewHolder> {
        private LayoutInflater mInflater;

        public AnnAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = mInflater.inflate(R.layout.layout_recent_prize_item, parent, false);
            RecentViewHolder holder = new RecentViewHolder(convertView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecentViewHolder holder, int position) {
            if (issues.get(position) == null || issues.get(position).getProduct() == null) {
                return;
            }

            final Issue issue = issues.get(position);
            final Product product = issue.getProduct();

            //商品图片
            String url = product.getImage();
            if (!TextUtils.isEmpty(url)) {
                ImageUtils.getInstance().display(url, holder.ivPic, R.drawable.pic_default_square_white);
            } else {
                holder.ivPic.setImageResource(R.drawable.pic_default_square_white);
            }

            //商品名称
            holder.tvName.setText(product.getName());

            //期号
            holder.tvIssueNo.setText("期号：" + String.valueOf(issue.getIssueNumber()));

            //Item点击事件
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueNo", issue.getIssueNumber());
                    params.put("objectId", product.getObjectId());
                    startActivity(ProductDetailActivity.class, params);
                }
            });

            //揭晓文描
            int isAnn = issue.getAnnounceState() == null ?
                    SeckillState.ANNOUNCING.getValue() : issue.getAnnounceState();
            switch (isAnn) {
                case 1: //即将揭晓
                default:
                    holder.tvAnnDesc.setVisibility(View.VISIBLE);
                    holder.tvUsername.setVisibility(View.GONE);
                    holder.tvUserId.setVisibility(View.GONE);
                    holder.tvLuckNo.setVisibility(View.GONE);
                    break;

                case 2: //已揭晓
                    holder.tvAnnDesc.setVisibility(View.GONE);
                    holder.tvUsername.setVisibility(View.VISIBLE);
                    holder.tvUserId.setVisibility(View.VISIBLE);
                    holder.tvLuckNo.setVisibility(View.VISIBLE);

                    Seckill seckill = issue.getSucceedSeckill();
                    if (seckill == null || issue.getSucceedSeckillNo() == null) {
                        holder.tvUsername.setText("获得者：");
                        holder.tvUserId.setText("用户ID：");
                        holder.tvLuckNo.setText("幸运号码：");
                        return;
                    }

                    OysUser user = seckill.getUser();
                    if (user == null) {
                        holder.tvUsername.setText("获得者：");
                        holder.tvUserId.setText("用户ID：");
                        holder.tvLuckNo.setText("幸运号码：");
                        return;
                    }

                    //获得者
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
                    SpannableString ss = TextStyleUtils.genColorText(
                            UserHelper.getNickname(user),
                            Color.parseColor("#4876FF"));
                    ssBuilder.append("获奖者：").append(ss);
                    holder.tvUsername.setText(ssBuilder);

                    //用户ID
                    holder.tvUserId.setText("用户ID：" + user.getUserId() + "(唯一不变标识)");

                    //幸运号码
                    ss = TextStyleUtils.genColorText(
                            String.valueOf(issue.getSucceedSeckillNo()),
                            Color.parseColor("#f85757"));
                    ssBuilder.clear();
                    ssBuilder.clearSpans();
                    ssBuilder.append("幸运号码：").append(ss);
                    holder.tvLuckNo.setText(ssBuilder);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return issues.size();
        }
    }

    class RecentViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPic;
        private TextView tvName;
        private TextView tvAnnDesc;
        private TextView tvIssueNo;
        private TextView tvUsername;
        private TextView tvUserId;
        private TextView tvLuckNo;
        private LinearLayout llItem;

        public RecentViewHolder(View view) {
            super(view);
            ivPic = ViewUtils.findViewById(view, R.id.iv_pic);
            tvName = ViewUtils.findViewById(view, R.id.tv_name);
            tvAnnDesc = ViewUtils.findViewById(view, R.id.tv_announce_desc);
            tvIssueNo = ViewUtils.findViewById(view, R.id.tv_issue_no);
            tvUsername = ViewUtils.findViewById(view, R.id.tv_user_name);
            tvUserId = ViewUtils.findViewById(view, R.id.tv_user_id);
            tvLuckNo = ViewUtils.findViewById(view, R.id.tv_luck_seckill_no);
            llItem = ViewUtils.findViewById(view, R.id.ll_item);
        }
    }
}
