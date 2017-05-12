package org.appu.common.utils;

import android.widget.Toast;

import org.appu.AppU;

public class ToastUtils {

    private static Toast mToast;

    public static void showShortToast(CharSequence text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(CharSequence text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    private static void showToast(CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(AppU.app(), text, duration);

        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
