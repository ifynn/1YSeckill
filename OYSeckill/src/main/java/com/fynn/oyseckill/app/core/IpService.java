package com.fynn.oyseckill.app.core;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Fynn on 2016/8/12.
 */
public class IpService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public IpService(String name) {
        super(name);
    }

    public IpService() {
        this("IpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        IpThread ipThread = new IpThread();
        ipThread.start();
    }
}
