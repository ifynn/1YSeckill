package com.fynn.oyseckill.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.fynn.oyseckill.R;

public class RectRelativeLayout extends RelativeLayout {

    // 默认是正方形
    private float activeType = 1.0f;
    private float heightToWidth = 1.0f;

    public RectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RectImageView);

        activeType = a.getFloat(R.styleable.RectImageView_widthToHeight, 1.0f);
        a.recycle();
    }

    public RectRelativeLayout(Context context) {
        super(context);
    }

    /**
     * 宽比高
     *
     * @param heightToWidth
     */
    public void setHeightToWidth(float heightToWidth) {
        this.heightToWidth = heightToWidth;
        invalidate();
    }

    /**
     * 高比宽
     *
     * @param widthToHeight
     */
    public void setWidthToHeight(float widthToHeight) {
        this.activeType = widthToHeight;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = measureWidth(widthMeasureSpec, heightMeasureSpec);
        int height = measureHeight(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        int result = 0;
        // 表示我们使用了fill_parent
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // 表示我们使用了wrap_parent
        else if (specMode == MeasureSpec.AT_MOST || specMode == MeasureSpec.UNSPECIFIED) {
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            // 如高是fill_parent
            if (heightSpecMode == MeasureSpec.EXACTLY) {
                // 将高设置为宽的一半
                result = (int) (MeasureSpec.getSize(heightMeasureSpec) * activeType);
            } else {
                result = specSize;
            }
        }

        return result;
    }

    private int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {

        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        int result = 0;
        // 表示我们使用了fill_parent
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // 表示我们使用了wrap_parent
        else if (specMode == MeasureSpec.AT_MOST || specMode == MeasureSpec.UNSPECIFIED) {
            int withSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            // 如宽是fill_parent
            if (withSpecMode == MeasureSpec.EXACTLY) {
                // 将高设置为宽的一半
                result = (int) (MeasureSpec.getSize(widthMeasureSpec) * activeType / heightToWidth);
            } else {
                result = specSize;
            }
        }
        return result;
    }

}
