package com.fynn.oyseckill.app.core;

import android.os.Handler;
import android.os.Message;

import org.appu.common.utils.LogU;

import java.lang.ref.WeakReference;

/**
 * Created by Fynn on 2016/6/7.
 */
public class RobustHandler extends Handler {
    private WeakReference<IBaseUI> iBaseUIWeakReference;

    public RobustHandler(IBaseUI iBaseUI) {
        iBaseUIWeakReference = new WeakReference<IBaseUI>(iBaseUI);
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            IBaseUI iBaseUI = iBaseUIWeakReference.get();
            if (iBaseUI != null && !iBaseUI.isFinish()) {
                iBaseUI.handleMessage(msg);
            } else {
                LogU.e("UI已被回收，不对handleMessage进行回调");
            }
        } catch (Exception e) {
            LogU.e("发生异常！", e);
        }
    }
}
