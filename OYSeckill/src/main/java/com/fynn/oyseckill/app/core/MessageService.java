package com.fynn.oyseckill.app.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.module.account.user.LuckRecordActivity;
import com.fynn.oyseckill.app.module.main.MainActivity;
import com.fynn.oyseckill.db.UserDb;
import com.fynn.oyseckill.model.Push;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;

/**
 * Created by Fynn on 2016/8/25.
 */
public class MessageService extends IntentService {

    private NotificationManager notificationManager;
    private Notification mNotification;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MessageService(String name) {
        super(name);
    }

    public MessageService() {
        this("MessageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msgJson = intent.getStringExtra("msg");
        Push push = null;
        try {
            push = new Gson().fromJson(msgJson, Push.class);
            LogU.e(push);

        } catch (JsonSyntaxException e) {
            LogU.e(e);
        }

        if (push == null || TextUtils.isEmpty(push.getTitle()) ||
                TextUtils.isEmpty(push.getMessage())) {
            return;
        }

        int type = push.getType();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_notice_small)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notice_large))
                .setContentTitle(push.getTitle())
                .setContentText(push.getMessage())
                .setTicker(push.getTitle())
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        if (type == Push.TYPE_GAIN_REWARD) {
            builder.setContentIntent(PendingIntent.getActivity(this, Push.TYPE_GAIN_REWARD,
                    new Intent(this, LuckRecordActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        } else {
            builder.setContentIntent(PendingIntent.getActivity(this, Push.TYPE_NOTIFICATION,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }

        mNotification = builder.build();
        notificationManager.notify(type, mNotification);

        if (push.getType() == Push.TYPE_GAIN_REWARD) {
            UserDb.putReward(true);
        }
    }
}
