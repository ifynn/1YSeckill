package com.fynn.oyseckill.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.widget.SeckillCountView;

import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.ViewUtils;

/**
 * Created by Fynn on 2016/7/15.
 */
public class SeckillCountPicker extends Dialog {

    public SeckillCountPicker(Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnSeckillClickListener {
        void onSeckillClick(SeckillCountPicker picker, int value);
    }

    public static class Builder {

        private Context mContext;
        private SeckillCountPicker picker;
        private int theme = R.style.Prompter;

        private int maxValue = Integer.MAX_VALUE;
        private int minValue = 1;
        private int value = 1;
        private OnSeckillClickListener onSeckillClickListener;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public SeckillCountPicker create() {
            if (picker == null) {
                picker = new SeckillCountPicker(mContext, theme);
            }

            final View contentView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_seckill_count_select_dialog, null);
            final SeckillCountView seckillCountView = ViewUtils.findViewById(
                    contentView, R.id.seckill_count_view);
            TextView tvSeckill = ViewUtils.findViewById(contentView, R.id.tv_seckill);
            LinearLayout llTopCount = ViewUtils.findViewById(contentView, R.id.ll_count_top);
            LinearLayout llBtmCount = ViewUtils.findViewById(contentView, R.id.ll_count_btm);
            ImageView ivClose = ViewUtils.findViewById(contentView, R.id.iv_close);

            seckillCountView.setEditable(true);
            seckillCountView.setMaxNum(maxValue);
            seckillCountView.setMinNum(minValue);
            seckillCountView.setNum(value);

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picker.dismiss();
                }
            });

            countBtnClick(llBtmCount, seckillCountView);
            countBtnClick(llTopCount, seckillCountView);

            enableCountBtn(llTopCount);
            enableCountBtn(llBtmCount);

            tvSeckill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSeckillClickListener != null) {
                        onSeckillClickListener.onSeckillClick(picker, seckillCountView.getNum());
                    }
                }
            });

            picker.setContentView(contentView);
            size();

            return picker;
        }

        public SeckillCountPicker show() {
            create().show();
            return picker;
        }

        public Builder setValue(int value) {
            this.value = value;
            return this;
        }

        public Builder setMaxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder setMinValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder setOnSeckillClickListener(OnSeckillClickListener onSeckillClickListener) {
            this.onSeckillClickListener = onSeckillClickListener;
            return this;
        }

        private void size() {
            Window window = picker.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DensityUtils.getScreenWidth();
            params.y = 0;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }

        private void countBtnClick(LinearLayout ll, final SeckillCountView scv) {
            int tCount = ll.getChildCount();
            for (int i = 0; i < tCount; i++) {
                TextView tv = (TextView) ll.getChildAt(i);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof TextView) {
                            String numStr = ((TextView) v).getText().toString();
                            scv.setNum(Integer.valueOf(numStr));
                        }
                    }
                });
            }
        }

        private void enableCountBtn(LinearLayout ll) {
            int tCount = ll.getChildCount();
            for (int i = 0; i < tCount; i++) {
                TextView v = (TextView) ll.getChildAt(i);
                String numStr = v.getText().toString();
                int num = Integer.valueOf(numStr);
                if (num <= maxValue && num >= minValue) {
                    v.setEnabled(true);
                } else {
                    v.setEnabled(false);
                }
            }
        }
    }
}
