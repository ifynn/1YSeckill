package com.fynn.oyseckill.app.module.account.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.user.MySeckillActivity;
import com.fynn.oyseckill.model.entity.Asset;
import com.fynn.oyseckill.model.entity.Recharge;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.pay.Pay;
import com.fynn.oyseckill.util.pay.PayMethod;

import org.appu.common.ParamMap;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/8/29.
 */
public class PayResultActivity extends BaseActivity {

    private ImageView ivState;
    private TextView tvDesc;
    private TextView tvDetail;
    private TextView tvContinue;
    private TextView tvRecord;

    private String payMethod;
    private double amount;      //实付人次
    private double realAmount;  //应付人次
    private boolean success;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            Object pm = params.get("payMethod");
            Object a = params.get("amount");
            Object ra = params.get("realAmount");
            Object s = params.get("success");

            if (pm != null) {
                payMethod = String.valueOf(pm);
            }

            if (a != null) {
                amount = Double.valueOf(String.valueOf(a));
            }

            if (ra != null) {
                realAmount = Double.valueOf(String.valueOf(ra));
            }

            if (s != null) {
                success = Boolean.parseBoolean(String.valueOf(s));
            }
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_pay_result;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ivState = $(R.id.iv_state);
        tvDesc = $(R.id.tv_desc);
        tvDetail = $(R.id.tv_detail);
        tvContinue = $(R.id.tv_continue);
        tvRecord = $(R.id.tv_record);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvContinue, tvRecord);

        if (success) {
            double returnCoin = amount - realAmount;
            if (PayMethod.REST_COIN.getValue().equals(payMethod)) {
                if (returnCoin > 0) {
                    String detail = "该期商品剩余人次不足" +
                            new DecimalFormat("#.#").format(amount) + "人次，共参与" +
                            new DecimalFormat("#.#").format(realAmount) + "人次，实付款" +
                            new DecimalFormat("#.##").format(realAmount) + "秒币";
                    tvDetail.setText(detail);
                    tvDetail.setTextColor(0xFFF85757);
                }

            } else {
                if (returnCoin > 0) {
                    String detail = "该期商品剩余人次不足" +
                            new DecimalFormat("#.#").format(amount) + "人次，共参与" +
                            new DecimalFormat("#.#").format(realAmount) + "人次，实付款" +
                            new DecimalFormat("#.##").format(amount) + "元；\n" +
                            "多支付部分将以秒币方式返回至用户账户。";
                    tvDetail.setText(detail);
                    tvDetail.setTextColor(0xFFF85757);

                    returnCoin(returnCoin);
                }
            }

        } else {
            ivState.setImageResource(R.drawable.icon_close_fill);
            tvDesc.setTextColor(0xfff85757);
            tvDesc.setText("抱歉，参与失败！");
            tvRecord.setVisibility(View.GONE);

            if (PayMethod.REST_COIN.getValue().equals(payMethod)) {
                tvDetail.setText("参与失败");

            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("如有疑问请尝试以下操作：\n");
                sb.append("1.前往 “账户明细” 查看该笔消费记录；\n");
                sb.append("2.进入 “设置 -> 支付帮助” 查看帮助；\n");
                sb.append("3.联系客服微信（1元秒服务）。");
                tvDetail.setText(sb);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue:
                finish();
                break;

            case R.id.tv_record:
                startActivity(MySeckillActivity.class);
                finish();
                break;
        }
    }

    private void returnCoin(final double amount) {
        BmobQuery<Asset> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(me, new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                if (list != null && !list.isEmpty()) {
                    Asset asset = new Asset();
                    asset.setObjectId(list.get(0).getObjectId());
                    asset.increment("oysCoin", amount);
                    asset.update(me, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Recharge recharge = new Recharge();
                            recharge.setState(Pay.STATE_SUCCESS);
                            recharge.setUser(UserHelper.getUser());
                            recharge.setName("退还");
                            recharge.setMethod(PayMethod.RETURN.getValue());
                            recharge.setDesc("退还商品参与多支付部分");
                            recharge.setAmount(amount);
                            recharge.save(me);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
