package com.fynn.oyseckill.app.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fynn.oyseckill.util.constants.Event;

import org.appu.AppU;
import org.appu.common.utils.LogU;
import org.appu.common.utils.NetUtils;

import cn.bmob.push.PushConstants;

/**
 * Created by Fynn on 2016/8/12.
 */
public class CoreReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        if (Event.EVENT_CONNECTIVITY_CHANGE.equals(intent.getAction())) {
            if (NetUtils.isNetworkAvailable()) {
                Intent intentService = new Intent();
                intentService.setClass(AppU.app(), IpService.class);
                AppU.app().startService(intentService);

                AppU.app().startService(new Intent(AppU.app(), CoreService.class));
            }
        } else if (PushConstants.ACTION_MESSAGE.equals(intent.getAction())) {
            String jsonMsg = intent.getStringExtra("msg");
            LogU.e("消息推送", jsonMsg);
            Intent msgIntent = new Intent();
            msgIntent.putExtra("msg", jsonMsg);
            msgIntent.setClass(AppU.app(), MessageService.class);
            AppU.app().startService(msgIntent);

        } else {
            Intent intentService = new Intent();
            intentService.setClass(AppU.app(), IpService.class);
            AppU.app().startService(intentService);
        }
    }
}
