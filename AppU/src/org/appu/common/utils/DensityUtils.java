package org.appu.common.utils;

import org.appu.AppU;

/**
 * Created by Fynn on 2016/3/22.
 */
public final class DensityUtils {

    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;
    private static int statusBarHeight = -1;

    public static float getDensity() {
        if (density <= 0) {
            density = AppU.app().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = AppU.app().getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }

    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = AppU.app().getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }

    public static int getStatusBarHeight() {
        if (statusBarHeight <= 0) {
            int resId = AppU.app().getResources().getIdentifier(
                    "status_bar_height", "dimen", "android");
            if (resId > 0) {
                statusBarHeight = AppU.app().getResources().getDimensionPixelSize(resId);
            } else {
                statusBarHeight = 0;
            }
        }
        return statusBarHeight;
    }
}
