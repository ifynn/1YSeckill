package com.fynn.oyseckill.app.module.account.user;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.model.entity.Asset;
import com.fynn.oyseckill.model.entity.Recharge;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.pay.Pay;
import com.fynn.oyseckill.util.view.PrompterUtils;
import com.fynn.oyseckill.widget.dialog.IPrompter;

import org.appu.common.utils.LogU;
import org.appu.common.utils.ViewUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import c.b.QListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/8/8.
 */
public class RechargeRecordFragment extends BaseFragment {

    private static final int LIMIT = 20;
    private SwipeRefreshLayout refreshView;
    private ListView lvRechargeRecord;
    private View emptyHeader;
    private TextView tvFooter;
    private List<Recharge> recharges;
    private RRAdapter rrAdapter;
    private int pageCount = 0;
    private boolean isFinish;
    private boolean isLoading;
    private boolean isLoaded;

    @Override
    public int getContentResId() {
        return R.layout.fragment_account_record;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvRechargeRecord = $(R.id.lv_account_record);
        refreshView = $(R.id.refresh_view);
        emptyHeader = LayoutInflater.from(activity).inflate(
                R.layout.layout_my_seckill_list_header, null);
        tvFooter = ViewUtils.findViewById(emptyHeader, R.id.tv_footer);

        recharges = new ArrayList<Recharge>();
        rrAdapter = new RRAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        tvFooter.setText("无充值记录");
        lvRechargeRecord.addFooterView(emptyHeader, null, false);
        lvRechargeRecord.setAdapter(rrAdapter);

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

        lvRechargeRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
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

    private void query() {
        isLoading = true;
        BmobQuery<Recharge> query = new BmobQuery<Recharge>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * pageCount);
        query.order("-createdAt");
        query.findObjects(activity, new FindListener<Recharge>() {
            @Override
            public void onSuccess(List<Recharge> list) {
                isLoading = false;
                if (!isLoaded) {
                    isLoaded = true;
                }
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }

                if (pageCount <= 0) {
                    recharges.clear();
                }

                if (list != null && !list.isEmpty()) {
                    recharges.addAll(list);

                    if (list.size() < LIMIT) {
                        isFinish = true;
                    } else {
                        pageCount++;
                    }
                } else {
                    isFinish = true;
                }

                rrAdapter.notifyDataSetChanged();

                if (recharges.isEmpty()) {
                    tvFooter.setText("无充值记录");

                } else {
                    if (isFinish) {
                        tvFooter.setText("无更多充值记录");
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
                LogU.e("查询充值记录失败", "code:" + i, "msg:" + s);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoading && !isFinish && !isLoaded) {
            query();
        }
    }

    private void refresh() {
        isFinish = false;
        pageCount = 0;
        query();
    }

    private void saveRecharge(final Recharge rcg, final String state) {
        Recharge rcgUpdate = new Recharge();
        rcgUpdate.setState(state);
        rcgUpdate.update(activity, rcg.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                if (Pay.STATE_SUCCESS.equals(state)) {
                    queryAsset(rcg);
                } else {
                    hideProgress();
                    PrompterUtils.showCaution(activity,
                            "查询成功！该笔账单支付失败。", "确定",
                            new IPrompter.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                    rcg.setState(Pay.STATE_NOT_PAY);
                                    rrAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                showShortToast("查询失败");
            }
        });
    }

    private void queryAsset(final Recharge recharge) {
        BmobQuery<Asset> query = new BmobQuery<Asset>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(activity, new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                hideProgress();
                if (list != null && !list.isEmpty()) {
                    //数据同步 increment
                    Asset asset = new Asset();
                    asset.setObjectId(list.get(0).getObjectId());
                    asset.increment("oysCoin", recharge.getAmount());
                    asset.update(activity);

                } else {
                    Asset asset = new Asset();
                    asset.setUser(UserHelper.getUser());
                    asset.setOysCoin(recharge.getAmount());
                    asset.save(activity);
                }

                PrompterUtils.showCaution(activity,
                        "查询成功！该笔账单已支付成功，将以秒币方式返还至您的账户。", "确定",
                        new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                                recharge.setState(Pay.STATE_SUCCESS);
                                rrAdapter.notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("查询资产信息失败", "code:" + i, "msg:" + s);
                hideProgress();
                showShortToast("查询失败");
            }
        });
    }

    class RRAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recharges.size();
        }

        @Override
        public Object getItem(int position) {
            return recharges.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(
                        R.layout.layout_recharge_record_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Recharge recharge = (Recharge) getItem(position);

            holder.tvAmount.setText(new DecimalFormat("0.0#").format(recharge.getAmount()) + "元");

            String state = "未知";
            if (Pay.STATE_SUCCESS.equals(recharge.getState())) {
                state = "充值成功";
                holder.tvState.setTextColor(getResources().getColor(R.color.green_32CD32));
                holder.tvQuery.setVisibility(View.GONE);
            } else if (Pay.STATE_NOT_PAY.equals(recharge.getState())) {
                state = "充值失败";
                holder.tvState.setTextColor(getResources().getColor(R.color.red_F85757));
                holder.tvQuery.setVisibility(View.GONE);
            } else if (Pay.STATE_UNKNOWN.equals(recharge.getState())) {
                state = "未知";
                holder.tvState.setTextColor(getResources().getColor(R.color.yellow_FF7F24));
                holder.tvQuery.setVisibility(View.VISIBLE);
            }

            holder.tvState.setText(state);
            holder.tvDesc.setText(recharge.getName());
            holder.tvTime.setText(recharge.getCreatedAt());

            holder.tvQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    BP.query(recharge.getOrderCode(), new QListener() {
                        @Override
                        public void succeed(String s) {
                            saveRecharge(recharge, s);
                        }

                        @Override
                        public void fail(int i, String s) {
                            showShortToast("查询失败");
                            hideProgress();
                            LogU.e("查询失败", "code:" + i, "msg:" + s);
                        }
                    });
                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView tvAmount;
            private TextView tvState;
            private TextView tvDesc;
            private TextView tvTime;
            private TextView tvQuery;

            public ViewHolder(View v) {
                tvAmount = ViewUtils.findViewById(v, R.id.tv_amount);
                tvState = ViewUtils.findViewById(v, R.id.tv_state);
                tvDesc = ViewUtils.findViewById(v, R.id.tv_desc);
                tvTime = ViewUtils.findViewById(v, R.id.tv_time);
                tvQuery = ViewUtils.findViewById(v, R.id.tv_query);
            }
        }
    }
}
