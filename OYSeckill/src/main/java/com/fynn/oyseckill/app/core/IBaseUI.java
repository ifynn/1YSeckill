package com.fynn.oyseckill.app.core;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by fynn on 16/4/23.
 */
public interface IBaseUI {
    int getContentResId();

    int getTitlebarResId();

    void initViews(Bundle savedInstanceState);

    void initActions(Bundle savedInstanceState);

    void handleIntent();

    void handleMessage(Message msg);

    boolean isFinish();

    void finish();
}
