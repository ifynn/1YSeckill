package com.fynn.oyseckill.app.module.account.pay;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.config.AssistanceDetailActivity;
import com.fynn.oyseckill.app.module.home.util.Manager;
import com.fynn.oyseckill.model.entity.Asset;
import com.fynn.oyseckill.model.entity.Recharge;
import com.fynn.oyseckill.util.FileUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.pay.Pay;
import com.fynn.oyseckill.util.pay.PayListener;
import com.fynn.oyseckill.util.pay.PayMethod;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Prompter;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.NetUtils;
import org.appu.common.utils.TextUtils;

import java.util.List;

import c.b.BP;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fynn on 16/6/25.
 */
public class RechargeActivity extends BaseActivity {

    private TextView tv20;
    private TextView tv50;
    private TextView tv100;
    private TextView tv200;
    private TextView tv500;
    private TextView tv1000;
    private EditText etOther;
    private TextView tvRecharge;
    private TextView tvPayRqa;

    private Pay.Build payBuild;

    private boolean isLoading;

    @Override
    public int getContentResId() {
        return R.layout.activity_recharge;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tv20 = $(R.id.tv_20);
        tv50 = $(R.id.tv_50);
        tv100 = $(R.id.tv_100);
        tv200 = $(R.id.tv_200);
        tv500 = $(R.id.tv_500);
        tv1000 = $(R.id.tv_1000);
        etOther = $(R.id.et_other);
        tvRecharge = $(R.id.tv_recharge);
        tvPayRqa = $(R.id.tv_pay_rqa);

        payBuild = new Pay.Build(this);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tv20, tv50, tv100, tv200, tv500, tv1000, tvRecharge, tvPayRqa);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_20:
            case R.id.tv_50:
            case R.id.tv_100:
            case R.id.tv_200:
            case R.id.tv_500:
            case R.id.tv_1000:
                if (!NetUtils.isNetworkAvailable()) {
                    showShortToast("无网络连接");
                    return;
                }
                if (isLoading) {
                    showShortToast("正在支付...");
                    return;
                }
                TextView view = (TextView) v;
                double amount = Double.valueOf(view.getText().toString());
                pay(amount);
                break;

            case R.id.tv_recharge:
                if (!NetUtils.isNetworkAvailable()) {
                    showShortToast("无网络连接");
                    return;
                }
                String text = etOther.getText().toString().trim();
                double otherAmount = -1;
                try {
                    otherAmount = Double.valueOf(text);
                } catch (Exception e) {
                    LogU.w(e);
                }

                if (otherAmount > 0) {
                    if (isLoading) {
                        showShortToast("正在支付...");
                        return;
                    }
                    pay(otherAmount);
                }
                break;

            case R.id.tv_pay_rqa:
                ParamMap<String, Object> params = new ParamMap<>();
                params.put("typeId", 1); //1表示支付问题
                startActivity(AssistanceDetailActivity.class, params);
                break;
        }
    }

    private void pay(final double amount) {
        isLoading = true;
        showProgress("请耐心等待");

        double a = amount;
        if (Manager.contains(UserHelper.getObjectId())) {
            a = 0.01d;
        }

        payBuild.setAmount(a)
                .setTitle("1元秒充值")
                .setDesc("1元秒秒币充值")
                .setPayMethod(PayMethod.WECHAT)
                .pay(new PayListener() {
                    @Override
                    public void onSuccess(String orderId) {
                        isLoading = false;
                        hideProgress();
                        saveRecharge(orderId, amount, Pay.STATE_SUCCESS);
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("amount", amount);
                        startActivity(RechargeResultActivity.class, params);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String msg, String orderId) {
                        isLoading = false;
                        hideProgress();
                        switch (code) {
                            case -3:
                                showShortToast("未安装安全支付插件，请安装后再试");
                                FileUtils.installBmobPayPlugin(me);
                                break;

                            case 7777:
                                showShortToast("未安装微信客户端");
                                break;

                            case 8888:
                                showShortToast("微信版本过低或被加了应用锁");
                                break;

                            case 9010:
                            case 9016:
                                showShortToast("网络异常");
                                break;

                            case 10777:
                                BP.ForceFree();
                                showShortToast("支付失败，请重试");
                                break;

                            case Pay.RESULT_UNKNOWN:
                            case Pay.RESULT_NOT_PAY:
                                saveRecharge(orderId, amount, code == Pay.RESULT_NOT_PAY ?
                                        Pay.STATE_NOT_PAY : Pay.STATE_UNKNOWN);
                                showTipsDialog(orderId, amount, code);
                                break;

                            default:
                                if (TextUtils.isEmpty(orderId)) {
                                    showShortToast("充值失败");
                                    return;
                                }
                                saveRecharge(orderId, amount, Pay.STATE_UNKNOWN);
                                showTipsDialog(orderId, amount, code);
                                break;
                        }
                    }
                });
    }

    private void showTipsDialog(final String orderId, final double amount, final int code) {
        new Prompter.Builder(this)
                .setTitle("支付结果确认")
                .setTitleSize(17)
                .setPositiveButton("支付成功", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                        ParamMap<String, Object> unParams = new ParamMap<String, Object>();
                        unParams.put("amount", amount);
                        unParams.put("orderId", orderId);
                        unParams.put("code", code);
                        startActivity(RechargeResultActivity.class, unParams);
                        finish();
                    }
                })
                .setNegativeButton("支付遇到问题", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButtonTextColor(0xfff85757)
                .show();
    }

    private void saveRecharge(String orderCode, final Double amount, final String state) {
        Recharge recharge = new Recharge();
        recharge.setUser(UserHelper.getUser());
        recharge.setOrderCode(orderCode);
        recharge.setDesc("1元秒秒币充值");
        recharge.setAmount(amount);
        recharge.setMethod(PayMethod.WECHAT.getValue());
        recharge.setName("1元秒充值");
        recharge.setState(state);
        recharge.save(me, new SaveListener() {
            @Override
            public void onSuccess() {
                if (Pay.STATE_SUCCESS.equals(state)) {
                    queryAsset(amount);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogU.e("保存充值数据失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private void queryAsset(final Double amount) {
        BmobQuery<Asset> query = new BmobQuery<Asset>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(me, new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                if (list != null && !list.isEmpty()) {
                    //数据同步 increment
                    Asset asset = new Asset();
                    asset.setObjectId(list.get(0).getObjectId());
                    asset.increment("oysCoin", amount);
                    asset.update(me);

                } else {
                    Asset asset = new Asset();
                    asset.setUser(UserHelper.getUser());
                    asset.setOysCoin(amount);
                    asset.save(me);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("查询资产信息失败", "code:" + i, "msg:" + s);
            }
        });
    }
}
