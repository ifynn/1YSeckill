package com.fynn.oyseckill.app.core;

import org.appu.AppU;

/**
 * Created by Fynn on 2016/9/26.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler INSTANCE = new CrashHandler();

    public static void init() {
        if (!AppU.isDebug()) {
            Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
