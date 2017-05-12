package com.fynn.oyseckill.app.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fynn.oyseckill.model.entity.RefusedVersion;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.common.AppHelper;
import org.appu.common.utils.PkgUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/9/2.
 */
public class CoreService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        queryRefusedVersion();
        return START_REDELIVER_INTENT;
    }

    private void queryRefusedVersion() {
        BmobQuery<RefusedVersion> query = new BmobQuery<>();
        query.addWhereEqualTo("versionCode", PkgUtils.getVersionCode());
        query.addWhereEqualTo("refused", true);
        query.findObjects(this, new FindListener<RefusedVersion>() {
            @Override
            public void onSuccess(List<RefusedVersion> list) {
                if (list != null && !list.isEmpty()) {
                    AppHelper.sendLocalEvent(Event.EVENT_REFUSED_VERSION);
                }
                stopSelf();
            }

            @Override
            public void onError(int i, String s) {
                stopSelf();
            }
        });
    }
}
