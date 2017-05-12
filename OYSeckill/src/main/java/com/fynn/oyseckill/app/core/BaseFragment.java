package com.fynn.oyseckill.app.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.RotateLoader;
import com.fynn.oyseckill.widget.Titlebar;

import org.appu.common.AppHelper;
import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.ToastUtils;
import org.appu.common.utils.ViewUtils;

/**
 * Created by fynn on 16/4/24.
 */
public abstract class BaseFragment extends Fragment
        implements IBaseUI, View.OnClickListener {

    protected Titlebar titlebar;

    protected RobustHandler handler;
    protected BaseActivity activity;
    private SafeBroadcastReceiver safeReceiver;
    private RotateLoader loader;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogU.i(this.getClass().getSimpleName() + " onCreateView");

        View view = null;

        int id = getContentResId();
        if (id > 0) {
            view = inflater.inflate(id, container, false);
        }

        if (view != null) {
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogU.i(this.getClass().getSimpleName() + " onActivityCreated");

        handleIntent();
        initTitlebar();

        handler = new RobustHandler(this);

        initViews(savedInstanceState);
        initActions(savedInstanceState);
    }

    public <E extends View> E $(int resId) {
        return ViewUtils.findViewById(getView(), resId);
    }

    public void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

    }

    protected ParamMap getParams() {
        return AppHelper.getParams(getActivity());
    }

    protected void startActivity(Class clazz) {
        AppHelper.startActivity(getActivity(), clazz);
    }

    protected void startActivity(Class clazz, ParamMap<String, Object> paramMap) {
        AppHelper.startActivity(getActivity(), clazz, paramMap);
    }

    protected void startActivityForLogin(Class target) {
        startActivityForLogin(target, null);
    }

    protected void startActivityForLogin(Class target, ParamMap params) {
        AppHelper.startActivityForLogin(getActivity(), UserHelper.isLogin(), target, LoginActivity.class, params);
    }

    public void register(String... event) {
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

    private void unregister() {
        if (safeReceiver != null && safeReceiver.isRegister) {
            AppHelper.unregister(safeReceiver);
            safeReceiver.isRegister = false;
        }
    }

    public void showShortToast(String msg) {
        ToastUtils.showShortToast(msg);
    }

    public void showLongToast(String msg) {
        ToastUtils.showLongToast(msg);
    }

    private void initTitlebar() {
        int id = getTitlebarResId();
        if (id > 0) {
            titlebar = $(getTitlebarResId());
            titlebar.setGoBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public int getTitlebarResId() {
        return 0;
    }

    public void onEvent(String action, Bundle data) {

    }

    protected void showProgress() {
        showProgress(null, true, false);
    }

    protected void showProgress(String message) {
        showProgress(message, true, false);
    }

    protected void showProgress(boolean cancelable, boolean canceledOnTouchOutside) {
        showProgress(null, cancelable, canceledOnTouchOutside);
    }

    protected void showProgress(
            String message, boolean cancelable, boolean canceledOnTouchOutside) {
        if (loader == null) {
            loader = new RotateLoader.Builder(activity)
                    .setMessage(message)
                    .setCancelable(cancelable)
                    .setCanceledOnTouchOutside(canceledOnTouchOutside)
                    .show();
        } else {
            if (!loader.isShowing()) {
                loader = new RotateLoader.Builder(activity)
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

    protected void hideProgress() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public boolean isFinish() {
        return !isAdded();
    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        unregister();
        super.onDestroyView();

        LogU.i(this.getClass().getSimpleName() + " onDestroyView");
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }
        super.onAttach(context);

        LogU.i(this.getClass().getSimpleName() + " onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogU.i(this.getClass().getSimpleName() + " onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogU.i(this.getClass().getSimpleName() + " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogU.i(this.getClass().getSimpleName() + " onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogU.i(this.getClass().getSimpleName() + " onHiddenChanged " + hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        String activityName = activity.getClass().getSimpleName();
        String fragmentName = this.getClass().getSimpleName();
        com.baidu.mobstat.StatService.onPageEnd(activity, activityName + "_" + fragmentName);
        LogU.i(fragmentName + " onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        String activityName = activity.getClass().getSimpleName();
        String fragmentName = this.getClass().getSimpleName();
        com.baidu.mobstat.StatService.onPageStart(activity, activityName + "_" + fragmentName);
        LogU.i(this.getClass().getSimpleName() + " onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogU.i(this.getClass().getSimpleName() + " onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogU.i(this.getClass().getSimpleName() + " onStop");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        LogU.i(this.getClass().getSimpleName() + " onInflate");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogU.i(this.getClass().getSimpleName() + " isVisibleToUser " + isVisibleToUser);
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
}
