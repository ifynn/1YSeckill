package com.fynn.oyseckill.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Fynn on 2016/4/26.
 */
public class StateScrollView extends ScrollView {

    private OnScrollListener onScrollListener;

    public StateScrollView(Context context) {
        super(context);
    }

    public StateScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StateScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public interface OnScrollListener {
        void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY);
    }
}
