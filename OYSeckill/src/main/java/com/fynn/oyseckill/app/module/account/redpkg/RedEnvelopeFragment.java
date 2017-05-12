package com.fynn.oyseckill.app.module.account.redpkg;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.model.entity.RedEnvelope;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.ViewUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;

/**
 * Created by Fynn on 2016/6/29.
 */
public class RedEnvelopeFragment extends BaseFragment {

    private static final int LIMIT = 10;
    private int type = RedPkgType.AVAILABLE.getValue();
    private List<RedEnvelope> redEnvelopes;
    private RedElpAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ListView lvRedPkg;
    private RedEnvelopeActivity activity;
    private Date currentDate;
    private int page = 0;
    private boolean isFinish;
    private boolean isLoading;

    private boolean isFirst = true;

    @Override
    public void handleIntent() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt("type", RedPkgType.AVAILABLE.getValue());
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_red_envelope;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        redEnvelopes = new ArrayList<RedEnvelope>();
        adapter = new RedElpAdapter();

        lvRedPkg = $(R.id.lv_red_pkg);
        swipeRefresh = $(R.id.swipe_refresh);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        lvRedPkg.setAdapter(adapter);
        lvRedPkg.setEmptyView($(R.id.ll_empty));

        lvRedPkg.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        boolean toBottom = view.getLastVisiblePosition() == view.getCount() - 1;
                        if (toBottom && !isLoading && !isFinish) {
                            update();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        swipeRefresh.setColorSchemeColors(Color.parseColor("#f85757"));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    isFinish = false;
                    page = 0;
                    update();

                } else {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void update() {
        if (isFinish || isLoading) {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }
            return;
        }

        isLoading = true;
        Bmob.getServerTime(activity, new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                currentDate = new Date(time * 1000L);
                LogU.e("current server date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(currentDate));
                queryRedEnvelope(currentDate);
            }

            @Override
            public void onFailure(int code, String msg) {
                isLoading = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }

                switch (code) {
                    case 9010:
                    case 9016:
                        showShortToast("网络不给力啊~");
                        break;
                }
            }
        });
    }

    private void queryRedEnvelope(Date currentDate) {
        BmobQuery<RedEnvelope> query = new BmobQuery<RedEnvelope>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setLimit(LIMIT);
        query.setSkip(page * LIMIT);

        if (type == RedPkgType.AVAILABLE.getValue()) {
            query.addWhereEqualTo("available", true);
            query.addWhereGreaterThanOrEqualTo("expiryDate", new BmobDate(currentDate));

        } else {
            BmobQuery<RedEnvelope> or1 = new BmobQuery<RedEnvelope>();
            or1.addWhereEqualTo("available", true);
            or1.addWhereLessThan("expiryDate", new BmobDate(currentDate));

            BmobQuery<RedEnvelope> or2 = new BmobQuery<RedEnvelope>();
            or2.addWhereEqualTo("available", false);

            List<BmobQuery<RedEnvelope>> queries = new ArrayList<BmobQuery<RedEnvelope>>();
            queries.add(or1);
            queries.add(or2);

            query.or(queries);
        }

        query.findObjects(activity, new FindListener<RedEnvelope>() {
            @Override
            public void onSuccess(List<RedEnvelope> list) {
                isLoading = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }

                if (list != null && !list.isEmpty()) {
                    if (page == 0) {
                        redEnvelopes.clear();
                    }
                    redEnvelopes.addAll(list);
                    adapter.notifyDataSetChanged();
                    page++;

                } else {
                    isFinish = true;
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }

                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("网络不给力啊~");
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (RedEnvelopeActivity) getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isFirst && !isLoading) {
            isFirst = false;
            update();
        }
        Log.e("isVisibleToUser", isVisibleToUser + "");
    }

    class RedElpAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return redEnvelopes.size();
        }

        @Override
        public Object getItem(int position) {
            return redEnvelopes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.layout_red_envelope_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RedEnvelope re = redEnvelopes.get(position);

            Double amount = re.getAmount();
            String amountStr = "0";
            if (amount != null) {
                amountStr = new DecimalFormat("#.##").format(amount);
            }
            holder.tvAmount.setText(amountStr + "元");
            holder.tvName.setText(String.valueOf(re.getName()));
            holder.tvDesc.setText(re.getDesc());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            String effDate = sdf.format(DateTimeUtils.parseDate(re.getEffectiveDate().getDate()));
            String expDate = sdf.format(DateTimeUtils.parseDate(re.getExpiryDate().getDate()));

            holder.tvDeadline.setText("有效期：" + effDate + "-" + expDate);

            if (re.getAvailable()) {
                BmobDate bd = re.getExpiryDate();
                Date exp = DateTimeUtils.parseDate(bd.getDate());
                if (currentDate == null) {
                    holder.ivMark.setVisibility(View.VISIBLE);
                    holder.ivMark.setImageResource(R.drawable.icon_red_envelope_expiry);

                } else {
                    if (exp.before(currentDate)) {
                        holder.ivMark.setVisibility(View.VISIBLE);
                        holder.ivMark.setImageResource(R.drawable.icon_red_envelope_expiry);
                    } else {
                        holder.ivMark.setVisibility(View.GONE);
                    }
                }

            } else {
                holder.ivMark.setVisibility(View.VISIBLE);
                holder.ivMark.setImageResource(R.drawable.icon_red_envelope_used);
            }

            return convertView;
        }

        class ViewHolder {
            private TextView tvAmount;
            private TextView tvName;
            private TextView tvDeadline;
            private TextView tvDesc;
            private ImageView ivMark;

            public ViewHolder(View convertView) {
                tvAmount = ViewUtils.findViewById(convertView, R.id.tv_amount);
                tvName = ViewUtils.findViewById(convertView, R.id.tv_name);
                tvDeadline = ViewUtils.findViewById(convertView, R.id.tv_deadline);
                tvDesc = ViewUtils.findViewById(convertView, R.id.tv_desc);
                ivMark = ViewUtils.findViewById(convertView, R.id.iv_mark);
            }
        }
    }
}
