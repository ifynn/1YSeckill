package org.appu.data.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import org.appu.AppU;
import org.appu.data.Plumage;

/**
 * Created by Fynn on 2016/7/1.
 */
public class Session extends Plumage {

    private static final String NAME_SESSION = "com.fynn.oyseckill.sp.SESSION";

    private SharedPreferences sp;
    private static Object object = new Object();
    private static Session session;

    public static Session get() {
        if (session == null) {
            synchronized (object) {
                if (session == null) {
                    session = new Session();
                }
            }
        }
        return session;
    }

    private Session() {
        sp = AppU.app().getSharedPreferences(NAME_SESSION, Context.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return sp;
    }
}
