package org.appu;

import android.app.Application;

import org.appu.common.utils.LogU;
import org.appu.common.utils.PkgUtils;
import org.appu.view.BindParser;
import org.appu.view.ViewBinder;

import java.io.File;

/**
 * Created by Fynn on 2016/3/22.
 */
public final class AppU {

    private static Application app;
    private static boolean isDebug;
    private static boolean flag = false;
    private static ViewBinder viewBinder;
    private static String cachePath;

    public static void init(Application application) {
        app = application;
    }

    public static Application app() {
        if (app == null) {
            throw new RuntimeException("Please add AppU.init(this) on Application.onCreate() and " +
                    "register your Application in AndroidManifest.");
        }
        return app;
    }

    public static ViewBinder viewBinder() {
        if (viewBinder == null) {
            viewBinder = new BindParser();
        }
        return viewBinder;
    }

    public static void setViewBinder(ViewBinder viewBinder) {
        AppU.viewBinder = viewBinder;
    }

    public static boolean isDebug() {
        if (!flag && PkgUtils.getVersionName().endsWith("-debug")) {
            isDebug = true;
        }
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        AppU.isDebug = isDebug;
        flag = true;
    }

    public static void setCachePath(String cachePath) {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        AppU.cachePath = dir.getPath();
        LogU.e("cachePath:", dir.getPath());
    }

    public static String getCachePath() {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return cachePath;
    }
}
