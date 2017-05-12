package org.appu.security;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

/**
 * Created by fynn on 16/6/9.
 */
public class Base64Helper {

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String encode(String input) {
        String encode = Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);
        return encode;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String decode(String input) {
        byte[] decode = Base64.decode(input.getBytes(), Base64.NO_WRAP);
        return new String(decode);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String encodeByte(byte[] input) {
        String encode = Base64.encodeToString(input, Base64.NO_WRAP);
        return encode;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static byte[] decodeByte(String input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }
}
