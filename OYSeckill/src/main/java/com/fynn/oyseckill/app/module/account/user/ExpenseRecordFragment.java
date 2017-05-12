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
import com.fynn.oyseckill.model.entity.Expense;
import com.fynn.oyseckill.model.entity.Recharge;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.pay.Pay;
import com.fynn.oyseckill.util.pay.PayMethod;
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
public class ExpenseRecordFragment extends BaseFragment {

    private static final int LIMIT = 20;
    private SwipeRefreshLayout refreshView;
    private ListView lvExpenseRecord;
    private View emptyHeader;
    private TextView tvFooter;
    private List<Expense> expenses;
    private ERAdapter erAdapter;
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
        lvExpenseRecord = $(R.id.lv_account_record);
        refreshView = $(R.id.refresh_view);
        emptyHeader = LayoutInflater.from(activity).inflate(
                R.layout.layout_my_seckill_list_header, null);
        tvFooter = ViewUtils.findViewById(emptyHeader, R.id.tv_footer);

        expenses = new ArrayList<Expense>();
        erAdapter = new ERAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        tvFooter.setText("无消费记录");
        lvExpenseRecord.addFooterView(emptyHeader, null, false);
        lvExpenseRecord.setAdapter(erAdapter);

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

        lvExpenseRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        BmobQuery<Expense> query = new BmobQuery<Expense>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setLimit(LIMIT);
        query.setSkip(LIMIT * pageCount);
        //query.include("issue");
        query.order("-createdAt");
        query.findObjects(activity, new FindListener<Expense>() {
            @Override
            public void onSuccess(List<Expense> list) {
                isLoading = false;
                if (!isLoaded) {
                    isLoaded = true;
                }
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }

                if (pageCount <= 0) {
                    expenses.clear();
                }

                if (list != null && !list.isEmpty()) {
                    expenses.addAll(list);

                    if (list.size() < LIMIT) {
                        isFinish = true;
                    } else {
                        pageCount++;
                    }
                } else {
                    isFinish = true;
                }

                erAdapter.notifyDataSetChanged();

                if (expenses.isEmpty()) {
                    tvFooter.setText("无消费记录");

                } else {
                    if (isFinish) {
                        tvFooter.setText("无更多消费记录");
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

    private void saveExpense(final Expense expense, final String state) {
        Expense expUpdate = new Expense();
        expUpdate.setState(state);
        expUpdate.update(activity, expense.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                if (Pay.STATE_SUCCESS.equals(state)) {
                    queryAsset(expense);
                } else {
                    hideProgress();
                    PrompterUtils.showCaution(activity,
                            "查询成功！该笔账单支付失败。", "确定",
                            new IPrompter.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                    expense.setState(Pay.STATE_NOT_PAY);
                                    erAdapter.notifyDataSetChanged();
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

    private void queryAsset(final Expense expense) {
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
                    asset.increment("oysCoin", expense.getAmount());
                    asset.update(activity);

                } else {
                    Asset asset = new Asset();
                    asset.setUser(UserHelper.getUser());
                    asset.setOysCoin(expense.getAmount());
                    asset.save(activity);
                }

                recordRecharge(expense.getAmount());

                PrompterUtils.showCaution(activity,
                        "查询成功！该笔账单已支付成功，将以秒币方式返还至您的账户。", "确定",
                        new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                                expense.setState(Pay.STATE_SUCCESS);
                                erAdapter.notifyDataSetChanged();
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

    private void recordRecharge(final double amount) {
        Recharge recharge = new Recharge();
        recharge.setState(Pay.STATE_SUCCESS);
        recharge.setUser(UserHelper.getUser());
        recharge.setName("退还");
        recharge.setMethod(PayMethod.RETURN.getValue());
        recharge.setDesc("退还参与商品失败所支付金额");
        recharge.setAmount(amount);
        recharge.save(activity);
    }

    class ERAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return expenses.size();
        }

        @Override
        public Object getItem(int position) {
            return expenses.get(position);
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

            final Expense expense = (Expense) getItem(position);

            String method = expense.getMethod();
            String methodDesc = "";
            String amountDesc = "";
            if (PayMethod.REST_COIN.getValue().equalsIgnoreCase(method)) {
                amountDesc = new DecimalFormat("0.0#").format(expense.getAmount()) + "秒币";
                methodDesc = "秒币支付";
            } else if (PayMethod.WECHAT.getValue().equalsIgnoreCase(method)) {
                amountDesc = new DecimalFormat("0.0#").format(expense.getAmount()) + "元";
                methodDesc = "微信支付";
            } else {
                amountDesc = new DecimalFormat("0.0#").format(expense.getAmount()) + "秒币";
                methodDesc = "秒币支付";
            }
            holder.tvAmount.setText(amountDesc);

            String state = "未知";
            if (Pay.STATE_SUCCESS.equals(expense.getState())) {
                state = "支付成功";
                holder.tvState.setTextColor(getResources().getColor(R.color.green_32CD32));
                holder.tvQuery.setVisibility(View.GONE);
            } else if (Pay.STATE_NOT_PAY.equals(expense.getState())) {
                state = "支付失败";
                holder.tvState.setTextColor(getResources().getColor(R.color.red_F85757));
                holder.tvQuery.setVisibility(View.GONE);
            } else if (Pay.STATE_UNKNOWN.equals(expense.getState())) {
                state = "未知";
                holder.tvState.setTextColor(getResources().getColor(R.color.yellow_FF7F24));
                holder.tvQuery.setVisibility(View.VISIBLE);
            }
            holder.tvState.setText(state);
            holder.tvDesc.setText(expense.getDesc() + "  " + methodDesc);
            holder.tvTime.setText(expense.getCreatedAt());

            holder.tvQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    BP.query(expense.getOrderCode(), new QListener() {
                        @Override
                        public void succeed(String s) {
                            saveExpense(expense, s);
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
