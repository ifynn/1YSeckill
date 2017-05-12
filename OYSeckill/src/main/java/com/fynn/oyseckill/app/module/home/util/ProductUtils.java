package com.fynn.oyseckill.app.module.home.util;

import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.math.BigDecimal;

/**
 * Created by Fynn on 2016/7/7.
 */
public class ProductUtils {

    public static int getProgressInHundred(double price, long crtPersonTimes) {
        double percent = crtPersonTimes / price;
        double p = new BigDecimal(percent).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

        int crtProgress = new Double(p * 100).intValue();

        return crtProgress;
    }

    public static SpannableString getColorfulString(String text, @ColorInt int color) {
        SpannableString sString = new SpannableString(text);
        sString.setSpan(
                new ForegroundColorSpan(color),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sString;
    }
}
