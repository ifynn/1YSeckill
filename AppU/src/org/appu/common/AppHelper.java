package org.appu.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.appu.AppU;
import org.appu.common.utils.LogU;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by fynn on 16/6/5.
 */
public final class AppHelper {

    public static final String ACTIVITY_FORWARD = "from";
    public static final String ACTIVITY_TARGET = "to";

    public static void register(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            LocalBroadcastManager.getInstance(AppU.app()).registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregister(BroadcastReceiver receiver) {
        try {
            LocalBroadcastManager.getInstance(AppU.app()).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLocalEvent(String action) {
        sendLocalEvent(action, null);
    }

    public static void sendLocalEvent(String action, Object object) {
        Intent intent = new Intent(action);

        if (object instanceof Serializable) {
            intent.putExtra(action, (Serializable) object);
        } else if (object instanceof String) {
            intent.putExtra(action, (String) object);
        } else if (object instanceof Long) {
            intent.putExtra(action, (Long) object);
        } else if (object instanceof Integer) {
            intent.putExtra(action, (Integer) object);
        } else if (object instanceof Double) {
            intent.putExtra(action, (Double) object);
        } else if (object instanceof Float) {
            intent.putExtra(action, (Float) object);
        } else if (object instanceof Float) {
            intent.putExtra(action, (Float) object);
        } else if (object instanceof Bundle) {
            intent.putExtras((Bundle) object);
        } else {
            intent.putExtra(action, object == null ? null : object.toString());
        }

        LocalBroadcastManager.getInstance(AppU.app()).sendBroadcast(intent);
    }

    public static void startActivity(Activity activity, Class clazz) {
        startActivity(activity, clazz, null);
    }

    public static void startActivity(
            Activity activity, Class clazz, ParamMap<String, Object> params) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);

        if (params == null) {
            params = new ParamMap<>();
        }

        params.put(ACTIVITY_FORWARD, activity.getClass().getName());

        Set<String> keys = params.keySet();
        Set<String> deletingKeys = new HashSet<>();
        Iterator<String> i = keys.iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (params.get(key) == null) {
                deletingKeys.add(key);
            }
        }

        Iterator<String> ii = deletingKeys.iterator();
        while (ii.hasNext()) {
            String key = ii.next();
            params.remove(key);
        }

        JSONObject jObject = new JSONObject(params);
        intent.putExtra("params", jObject.toString());
        activity.startActivity(intent);
    }

    public static void startActivityForResult(
            Activity activity, Class clazz, int requestCode) {
        startActivityForResult(activity, clazz, requestCode, null);
    }

    public static void startActivityForResult(
            Activity activity, Class clazz, int requestCode,
            ParamMap<String, Object> params) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);

        if (params == null) {
            params = new ParamMap<>();
        }

        params.put(ACTIVITY_FORWARD, activity.getClass().getName());

        Set<String> keys = params.keySet();
        Iterator<String> i = keys.iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (params.get(key) == null) {
                params.removeAt(params.indexOfKey(key));
            }
        }

        JSONObject jObject = new JSONObject(params);
        intent.putExtra("params", jObject.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    public static ParamMap getParams(Activity activity) {
        Intent intent = activity.getIntent();
        ParamMap params = new ParamMap();
        if (intent != null) {
            String json = intent.getStringExtra("params");
            try {
                JSONObject jObject = new JSONObject(json);
                Iterator<String> i = jObject.keys();
                while (i.hasNext()) {
                    String key = i.next();
                    Object o = jObject.get(key);
                    if (o != null) {
                        params.put(key, o);
                    }
                }
            } catch (Exception e) {
                LogU.w(e);
            }
        }
        LogU.d(activity.getClass().getSimpleName() + " 获取到的来自上一个Activity传递的参数：", params);
        return params;
    }

    public static void startActivityForLogin(
            Activity activity, boolean isLogin,
            Class target, Class login, ParamMap<String, Object> params) {

        if (!isLogin) {
            if (params == null) {
                params = new ParamMap<>();
            }
            params.put(ACTIVITY_TARGET, target.getName());
            startActivity(activity, login, params);

        } else {
            startActivity(activity, target, params);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showInputMethod(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideInputMethod(Activity activity) {
        InputMethodManager inputMethodManager = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static boolean isInputMethodActive(View view) {
        InputMethodManager manager = ((InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE));

        return manager.isActive(view);
    }
}
