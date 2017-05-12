package com.fynn.oyseckill.util.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.fynn.oyseckill.model.entity.Order;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.AppU;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Fynn on 2016/6/27.
 */
public class Pay {

    public static final int RESULT_UNKNOWN = 20002;
    public static final int RESULT_NOT_PAY = 20003;
    public static final String STATE_SUCCESS = "SUCCESS";
    public static final String STATE_NOT_PAY = "NOTPAY";
    public static final String STATE_UNKNOWN = "UNKNOWN";
    private Context context;
    private double amount;
    private String title;
    private String desc;
    private PayMethod payMethod;
    private PayListener payListener;
    private boolean isPaying;
    private String orderId;
    private Timer timer = new Timer(true);
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (timer != null) {
                timer.purge();
            }
            query();
        }
    };

    public Pay(Context context) {
        this.context = context;
    }

    public Pay(Context context, double amount) {
        this.amount = amount;
        this.context = context;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }

    public PayListener getPayListener() {
        return payListener;
    }

    public synchronized void pay(final PayListener payListener) {
        this.payListener = payListener;
        if (amount <= 0 || TextUtils.isEmpty(title) || payMethod == null || isPaying) {
            return;
        }
        boolean aliOrWechat = payMethod == PayMethod.ALIPAY ? true : false;
        isPaying = true;

        //打开一个自动关闭的activity，解决小米、努比亚手机无网络bug
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");
            intent.setComponent(cn);
            context.startActivity(intent);
        } catch (Exception e) {
            LogU.e(e);
        }

        BP.ForceFree();
        BP.pay(title, desc, amount, aliOrWechat, new PListener() {
            @Override
            public void orderId(String s) {
                Pay.this.orderId = s;
                Order order = new Order();
                order.setOrderCode(s);
                order.setUser(UserHelper.getUser());
                order.setInstallationId(BmobInstallation.getInstallationId(AppU.app()));
                order.save(AppU.app());

                LogU.e("订单号", s);
            }

            @Override
            public void succeed() {
                BP.ForceFree();
                waitForQuery();

                LogU.e("支付回调，进入方法succeed()");
            }

            @Override
            public void fail(int i, String s) {
                isPaying = false;
                if (payListener != null) {
                    payListener.onFailure(i, s, Pay.this.orderId);
                }

                LogU.e("充值失败", "code:" + i, "msg:" + s);
            }

            @Override
            public void unknow() {
                BP.ForceFree();
                waitForQuery();

                LogU.e("充值失败，未知问题");
            }
        });
    }

    private void waitForQuery() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 1800);
    }

    private void query() {
        BP.query(Pay.this.orderId, new QListener() {
            @Override
            public void succeed(String s) {
                isPaying = false;
                if (payListener != null) {
                    if (STATE_SUCCESS.equals(s)) {
                        payListener.onSuccess(Pay.this.orderId);

                    } else if (STATE_NOT_PAY.equals(s)) {
                        payListener.onFailure(
                                RESULT_NOT_PAY, "支付失败", Pay.this.orderId);
                    } else {
                        payListener.onFailure(
                                RESULT_UNKNOWN, "支付结果未知", Pay.this.orderId);
                    }
                }
            }

            @Override
            public void fail(int i, String s) {
                isPaying = false;
                if (payListener != null) {
                    payListener.onFailure(RESULT_UNKNOWN, "支付结果未知", Pay.this.orderId);
                }
            }
        });
    }

    public static class Build {

        private Pay pay;
        private double amount;
        private String title;
        private String desc;
        private PayMethod payMethod;
        private Context context;

        public Build(Context context) {
            this.context = context;
        }

        public Build setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Build setTitle(String title) {
            this.title = title;
            return this;
        }

        public Build setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Build setPayMethod(PayMethod payMethod) {
            this.payMethod = payMethod;
            return this;
        }

        public Pay build() {
            if (pay == null) {
                pay = new Pay(context, amount);
            }
            pay.setTitle(title);
            pay.setPayMethod(payMethod);
            pay.setDesc(desc);
            pay.setAmount(amount);
            return pay;
        }

        public Pay pay(PayListener payListener) {
            build().pay(payListener);
            return pay;
        }
    }
}
