package com.fynn.oyseckill.util;

import android.os.Environment;

import org.appu.AppU;

import java.io.File;

/**
 * Created by Fynn on 2016/6/23.
 */
public final class DirectoryUtils {

    public static final String EXTERNAL_MAIN = "oyseckill";
    public static final String EXTERNAL_TEMP = EXTERNAL_MAIN + "/temp";
    public static final String EXTERNAL_CACHE = EXTERNAL_TEMP + "/cache";

    public static String getExternalMainPath() {
        String path = Environment.getExternalStorageDirectory() + "/" + EXTERNAL_MAIN;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getExternalCachePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + EXTERNAL_CACHE;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getExternalTempPath() {
        String path = Environment.getExternalStorageDirectory() + "/" + EXTERNAL_TEMP;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getInternalCachePath() {
        String path = AppU.getCachePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
}
