package com.fynn.oyseckill.app.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Mob;
import com.fynn.oyseckill.widget.RotateLoader;
import com.fynn.oyseckill.widget.Titlebar;
import com.tencent.stat.StatService;

import org.appu.common.AppHelper;
import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.ToastUtils;
import org.appu.common.utils.ViewUtils;
import org.appu.model.Result;
import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by fynn on 16/4/23.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements IBaseUI, View.OnClickListener {

    protected Titlebar titlebar;
    protected RobustHandler handler;
    protected BaseActivity me = this;
    private SafeBroadcastReceiver safeReceiver;
    private SmsHandler smsHandler;
    private RotateLoader loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogU.i(this.getClass().getSimpleName() + " onCreate");

        handleIntent();
        initContentView();
        initTitlebar();

        handler = new RobustHandler(this);

        initViews(savedInstanceState);
        initActions(savedInstanceState);
    }

    private void initContentView() {
        int id = getContentResId();
        if (id > 0) {
            setContentView(id);
        }
    }

    private void initTitlebar() {
        int id = getTitlebarResId();
        if (id > 0) {
            titlebar = $(getTitlebarResId());
            titlebar.setGoBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public Titlebar getTitlebar() {
        return titlebar;
    }

    @Override
    public int getTitlebarResId() {
        return 0;
    }

    public void showShortToast(String msg) {
        ToastUtils.showShortToast(msg);
    }

    public void showLongToast(String msg) {
        ToastUtils.showLongToast(msg);
    }

    public <E extends View> E $(int resId) {
        return ViewUtils.findViewById(this, resId);
    }

    public void onEvent(String action, Bundle data) {

    }

    protected ParamMap getParams() {
        return AppHelper.getParams(this);
    }

    public void startActivity(Class clazz) {
        AppHelper.startActivity(this, clazz);
    }

    public void startActivity(Class clazz, ParamMap<String, Object> paramMap) {
        AppHelper.startActivity(this, clazz, paramMap);
    }

    public void startActivityForLogin(Class target) {
        startActivityForLogin(target, null);
    }

    public void startActivityForLogin(Class target, ParamMap params) {
        AppHelper.startActivityForLogin(this, UserHelper.isLogin(), target, LoginActivity.class, params);
    }

    public void startActivityForResult(Class target, int requestCode) {
        startActivityForResult(target, requestCode, null);
    }

    public void startActivityForResult(
            Class target, int requestCode, ParamMap<String, Object> params) {
        AppHelper.startActivityForResult(this, target, requestCode, params);
    }

    protected void register(String... event) {
        if (safeReceiver == null) {
            safeReceiver = new SafeBroadcastReceiver();
        }

        if (!safeReceiver.isRegister) {
            IntentFilter filter = new IntentFilter();
            for (String eventAction : event) {
                filter.addAction(eventAction);
            }

            safeReceiver.isRegister = true;
            AppHelper.register(safeReceiver, filter);
        }
    }

    public void showProgress() {
        showProgress(null, true, false);
    }

    public void showProgress(String message) {
        showProgress(message, true, false);
    }

    public void showProgress(boolean cancelable, boolean canceledOnTouchOutside) {
        showProgress(null, cancelable, canceledOnTouchOutside);
    }

    public void showProgress(
            String message, boolean cancelable, boolean canceledOnTouchOutside) {
        if (loader == null) {
            loader = new RotateLoader.Builder(this)
                    .setMessage(message)
                    .setCancelable(cancelable)
                    .setCanceledOnTouchOutside(canceledOnTouchOutside)
                    .show();
        } else {
            if (!loader.isShowing()) {
                loader = new RotateLoader.Builder(this)
                        .setMessage(message)
                        .setCancelable(cancelable)
                        .setCanceledOnTouchOutside(canceledOnTouchOutside)
                        .show();
            }
        }

        if (!loader.isShowing()) {
            loader.show();
        }
    }

    public void hideProgress() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    protected void registerSms() {
        if (smsHandler == null) {
            smsHandler = new SmsHandler();
        }

        if (!smsHandler.isRegister) {
            SMSSDK.registerEventHandler(smsHandler);
            smsHandler.isRegister = true;
        }
    }

    protected void unregisterSms() {
        if (smsHandler != null && smsHandler.isRegister) {
            SMSSDK.unregisterEventHandler(smsHandler);
            smsHandler.isRegister = false;
        }
    }

    private void unregister() {
        if (safeReceiver != null && safeReceiver.isRegister) {
            AppHelper.unregister(safeReceiver);
            safeReceiver.isRegister = false;
        }
    }

    public void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        hideProgress();
        unregister();
        unregisterSms();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

        LogU.i(this.getClass().getSimpleName() + " onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(me);
        com.baidu.mobstat.StatService.onResume(me);
        LogU.i(this.getClass().getSimpleName() + " onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogU.i(this.getClass().getSimpleName() + " onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogU.i(this.getClass().getSimpleName() + " onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(me);
        com.baidu.mobstat.StatService.onPause(me);
        LogU.i(this.getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogU.i(this.getClass().getSimpleName() + " onAttachedToWindow");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        LogU.i(this.getClass().getSimpleName() + " onAttachFragment " + fragment.getClass().getSimpleName());
    }

    private class SafeBroadcastReceiver extends BroadcastReceiver {
        public boolean isRegister = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                onEvent(intent.getAction(), intent.getExtras());
            } catch (Exception e) {
                LogU.e("发生了异常！", e.getMessage());
            }
        }
    }

    private class SmsHandler extends EventHandler {

        public boolean isRegister = false;

        @Override
        public void afterEvent(int event, int result, Object data) {
            LogU.e("event:" + event, "result:" + result, "data:" + data);

            if (result == SMSSDK.RESULT_COMPLETE) {
                Message msg = handler.obtainMessage(event);
                Result<Object> rst = new Result<Object>();
                rst.setCode(String.valueOf(result));
                rst.setMessage("");
                rst.setData(data);
                msg.obj = rst;
                handler.sendMessage(msg);

            } else {
                Message msg = handler.obtainMessage(event);
                Result<Throwable> rst = new Result<Throwable>();
                Throwable th = ((Throwable) data);

                rst.setCode(String.valueOf(result));
                try {
                    JSONObject jObject = new JSONObject(th.getMessage());
                    String status = jObject.getString("status");
                    String detail = jObject.getString("detail");
                    String message = "操作失败";

                    if (status.equals(Mob.SMS_CODE_VERIFY_ERROR)) {
                        message = "短信验证码错误";
                    } else if (status.equals(Mob.SMS_CODE_SEND_FREQUENT)) {
                        message = "短信验证码发送频繁";
                    } else if (status.equals(Mob.SMS_CODE_SEND_OVERRUN_WITH_CURRENT_APP)) {
                        message = "每天发送短信的次数超限";
                    } else if (status.equals(Mob.SMS_CODE_SEND_OVERRUN_WITH_DAY)) {
                        message = "当前设备每天发送短信的次数超限";
                    } else if (status.equals(Mob.SMS_CODE_SEND_OVERRUN_WITH_ALL_APP)) {
                        message = "每天发送短信的次数超限";
                    } else if (status.equals(Mob.SMS_CODE_VERIFY_FREQUENT)) {
                        message = "验证码验证频繁，请重新获取";
                    } else if (status.equals(Mob.SMS_CODE_FREQUENT_WITH_SMS_SDK)) {
                        message = "每天发送短信的次数超限";
                    }

                    rst.setMessage(message);
                    rst.setCode(status);
                    rst.setDetail(detail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rst.setData(th);
                msg.obj = rst;
                handler.sendMessage(msg);
            }
        }
    }
}
