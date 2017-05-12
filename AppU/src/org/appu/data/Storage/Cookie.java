package org.appu.data.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import org.appu.AppU;
import org.appu.data.Plumage;

/**
 * Created by Fynn on 2016/7/1.
 */
public class Cookie extends Plumage {

    private static final String NAME_COOKIE = "com.fynn.oyseckill.sp.COOKIE";

    private SharedPreferences sp;
    private static Object object = new Object();
    private static Cookie cookie;

    private Cookie() {
        sp = AppU.app().getSharedPreferences(NAME_COOKIE, Context.MODE_PRIVATE);
    }

    public static Cookie get() {
        if (cookie == null) {
            synchronized (object) {
                if (cookie == null) {
                    cookie = new Cookie();
                }
            }
        }
        return cookie;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return sp;
    }
}
