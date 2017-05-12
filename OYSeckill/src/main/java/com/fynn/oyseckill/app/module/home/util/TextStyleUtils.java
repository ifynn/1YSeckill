package com.fynn.oyseckill.app.module.home.util;

import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import org.appu.common.utils.DensityUtils;

/**
 * Created by Fynn on 2016/7/13.
 */
public final class TextStyleUtils {

    public static SpannableString genAppearanceText(
            String text, @ColorInt int color, int spSize) {
        return genAppearanceText(text, color, spSize, null);
    }

    public static SpannableString genAppearanceText(
            String text, @ColorInt final int color, int spSize, final View.OnClickListener listener) {
        SpannableString sString = new SpannableString(text);
        sString.setSpan(
                new ForegroundColorSpan(color),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sString.setSpan(
                new AbsoluteSizeSpan(DensityUtils.dip2px(spSize)),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        if (listener != null) {
            sString.setSpan(
                    new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            listener.onClick(widget);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(color);
                            ds.setUnderlineText(false);
                        }
                    },
                    0,
                    sString.length(),
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        return sString;
    }

    public static SpannableString genColorText(String text, @ColorInt int color) {
        SpannableString sString = new SpannableString(text);
        sString.setSpan(
                new ForegroundColorSpan(color),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sString;
    }

    public static SpannableString genSizeText(String text, int spSize) {
        SpannableString sString = new SpannableString(text);
        sString.setSpan(
                new AbsoluteSizeSpan(DensityUtils.dip2px(spSize)),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        return sString;
    }
}
