package com.fynn.oyseckill.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;

import org.appu.common.utils.DensityUtils;

/**
 * Created by fynn on 16/6/19.
 */
public class OverListView extends ListView {
    public OverListView(Context context) {
        super(context);
    }

    public OverListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean overScrollBy(
            int deltaX, int deltaY,
            int scrollX, int scrollY,
            int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY,
            boolean isTouchEvent) {

        return super.overScrollBy(
                deltaX, deltaY,
                scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, DensityUtils.dip2px(80),
                isTouchEvent);
    }
}
