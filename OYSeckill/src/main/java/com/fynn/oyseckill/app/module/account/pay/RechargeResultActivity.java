package com.fynn.oyseckill.app.module.account.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.user.AccountDetailActivity;
import com.fynn.oyseckill.model.entity.Recharge;
import com.fynn.oyseckill.util.pay.Pay;

import org.appu.common.ParamMap;
import org.appu.common.utils.TextUtils;

import java.text.DecimalFormat;
import java.util.List;

import c.b.BP;
import c.b.QListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/6/28.
 */
public class RechargeResultActivity extends BaseActivity {

    private TextView tvGainedCoin;
    private TextView tvGoback;
    private TextView tvRecord;
    private TextView tvDesc;
    private ImageView ivState;
    private LinearLayout llContent;
    private ProgressBar progressBar;

    private Double amount = 0d;
    private String orderId;
    private int code = -1;

    @Override
    public int getContentResId() {
        return R.layout.activity_recharge_result;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void handleIntent() {
        ParamMap<String, Object> param = getParams();
        if (param != null) {
            orderId = (String) param.get("orderId");

            Object a = param.get("amount");
            if (a != null) {
                amount = Double.parseDouble(String.valueOf(a));
            }

            Object codeTmp = param.get("code");
            if (codeTmp != null) {
                code = Integer.valueOf(String.valueOf(codeTmp));
            }
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvGainedCoin = $(R.id.tv_gain_coin);
        tvGoback = $(R.id.tv_goback);
        tvRecord = $(R.id.tv_record);
        tvDesc = $(R.id.tv_desc);
        ivState = $(R.id.iv_state);
        llContent = $(R.id.ll_content);
        progressBar = $(R.id.progress_bar);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvGoback, tvRecord);
        setEmpty(true);

        if (code == -1 && TextUtils.isEmpty(orderId)) {
            String format = getResources().getString(R.string.recharge_success_gained_coin);
            String amountStr = new DecimalFormat("#.##").format(amount);
            tvGainedCoin.setText(String.format(format, amountStr));
            setEmpty(false);

        } else {
            BP.ForceFree();
            BP.query(orderId, new QListener() {
                @Override
                public void succeed(String s) {
                    if (Pay.STATE_SUCCESS.equals(s)) {
                        String format = getResources().getString(R.string.recharge_success_gained_coin);
                        String amountStr = new DecimalFormat("#.#").format(amount);
                        tvGainedCoin.setText(String.format(format, amountStr));
                        setEmpty(false);
                        successPay();

                    } else {
                        ivState.setImageResource(R.drawable.icon_close_fill);
                        tvDesc.setTextColor(0xfff85757);
                        tvDesc.setText("抱歉，充值异常！");

                        StringBuilder sb = new StringBuilder();
                        sb.append("如有疑问请尝试以下操作：\n");
                        sb.append("1.前往 “账户明细” 查看该笔充值记录；\n");
                        sb.append("2.进入 “设置 -> 支付帮助” 查看帮助；\n");
                        sb.append("3.联系客服微信（1元秒服务）。");
                        tvGainedCoin.setText(sb);

                        setEmpty(false);
                    }
                }

                @Override
                public void fail(int i, String s) {
                    ivState.setImageResource(R.drawable.icon_close_fill);
                    tvDesc.setTextColor(0xfff85757);
                    tvDesc.setText("支付结果未知");

                    StringBuilder sb = new StringBuilder();
                    sb.append("如有疑问请执行以下操作：\n");
                    sb.append("1.前往 “账户明细” 查看该笔充值记录；\n");
                    sb.append("2.进入 “设置 -> 支付帮助” 查看帮助；\n");
                    sb.append("3.联系客服微信（1元秒服务）。");
                    tvGainedCoin.setText(sb);

                    setEmpty(false);
                }
            });
        }
    }

    private void successPay() {
        BmobQuery<Recharge> query = new BmobQuery<>();
        query.addWhereEqualTo("orderCode", orderId);
        query.findObjects(me, new FindListener<Recharge>() {
            @Override
            public void onSuccess(List<Recharge> list) {
                if (list != null || !list.isEmpty()) {
                    Recharge recharge = list.get(0);
                    Recharge updateRecharge = new Recharge();
                    updateRecharge.setState(Pay.STATE_SUCCESS);
                    updateRecharge.setObjectId(recharge.getObjectId());
                    updateRecharge.update(me);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_goback:
                finish();
                break;

            case R.id.tv_record:
                startActivity(AccountDetailActivity.class);
                finish();
                break;
        }
    }

    private void setEmpty(boolean isEmpty) {
        if (isEmpty) {
            llContent.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            llContent.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
