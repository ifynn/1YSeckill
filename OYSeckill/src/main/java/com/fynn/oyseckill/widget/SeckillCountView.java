package com.fynn.oyseckill.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fynn.oyseckill.R;

import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

/**
 * Created by Fynn on 2016/7/15.
 */
public class SeckillCountView extends LinearLayout {

    private ImageView ivMinus;
    private ImageView ivPlus;
    private EditText etNum;
    private LinearLayout llNumPicker;

    private int maxNum = Integer.MAX_VALUE;
    private int minNum = 1;

    public SeckillCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(
                R.layout.layout_seckill_count_select_view, this);

        ivMinus = ViewUtils.findViewById(this, R.id.iv_minus);
        ivPlus = ViewUtils.findViewById(this, R.id.iv_plus);
        etNum = ViewUtils.findViewById(this, R.id.et_num);
        llNumPicker = ViewUtils.findViewById(this, R.id.ll_num_picker);

        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = getNum();
                if (num > minNum) {
                    num--;
                    setNum(num);
                }
            }
        });

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = getNum();
                if (num < maxNum) {
                    num++;
                    setNum(num);
                }
            }
        });

        etNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getNum() <= minNum) {
                    ivMinus.setEnabled(false);
                    if (getNum() < minNum) {
                        etNum.setText(String.valueOf(minNum));
                    }
                } else {
                    ivMinus.setEnabled(true);
                }

                if (getNum() >= maxNum) {
                    ivPlus.setEnabled(false);
                    if (getNum() > maxNum) {
                        etNum.setText(String.valueOf(maxNum));
                    }

                } else {
                    ivPlus.setEnabled(true);
                }

                etNum.setSelection(etNum.getText().toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        etNum.setClickable(true);
        etNum.setOnClickListener(l);
    }

    public void setEditable(boolean editable) {
        etNum.setFocusable(editable);
        etNum.setFocusableInTouchMode(editable);
    }

    public int getNum() {
        String numStr = etNum.getText().toString().trim();
        int num = minNum;
        try {
            if (!TextUtils.isEmpty(numStr)) {
                num = Integer.valueOf(numStr);
            }
        } catch (NumberFormatException e) {
            LogU.e(e);
        }
        return num;
    }

    public void setNum(int num) {
        etNum.setText(String.valueOf(num));
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setLimitNum(int maxNum, int minNum) {
        this.maxNum = maxNum;
        this.minNum = minNum;
    }
}
