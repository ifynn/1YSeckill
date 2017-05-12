package com.fynn.oyseckill.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fynn.oyseckill.R;

/**
 * Created by Fynn on 2016/8/4.
 */
public class WatermarkImageView extends ImageView {

    private Bitmap mWatermark = null;

    public WatermarkImageView(Context context) {
        super(context);
    }

    public WatermarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WatermarkImageView);

        int resId = a.getResourceId(R.styleable.WatermarkImageView_watermark, -1);
        if (resId != -1) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
            if (bm != null) {
                setImageWatermark(bm);
            }
        }

        a.recycle();
    }

    private void setImageWatermark(Bitmap bm) {
        if (mWatermark != bm) {
            mWatermark = bm;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWatermark != null) {
            Drawable d = getDrawable();
            if (d != null) {
                Rect rect = getDrawable().copyBounds();
                int h = mWatermark.getHeight();
                int w = mWatermark.getWidth();

                int left = (rect.right - rect.left) / 2 - w / 2 + getPaddingLeft();
                int right = (rect.bottom - rect.top) / 2 - h / 2 + getPaddingTop();

                canvas.drawBitmap(mWatermark, left, right, new Paint());
            }
        }
    }
}
