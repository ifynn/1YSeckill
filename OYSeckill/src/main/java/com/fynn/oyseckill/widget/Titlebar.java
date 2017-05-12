package com.fynn.oyseckill.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;

/**
 * Created by fynn on 16/4/23.
 */
public class Titlebar extends RelativeLayout {

    public static final int IMAGE_ACTION_RIGHT_FIRST = 0x01;
    public static final int IMAGE_ACTION_RIGHT_SECOND = 0x02;
    private RelativeLayout rlParentPanel;
    private ImageView ivGoBack;
    private ImageView ivTitle;
    private ImageView ivRightFirstAction;
    private ImageView ivRightSecondAction;
    private TextView tvLeftAction;
    private TextView tvTitle;
    private TextView tvRightAction;
    private View divider;

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_titlebar, this);

        rlParentPanel = (RelativeLayout) findViewById(R.id.parent_panel);

        ivGoBack = (ImageView) findViewById(R.id.iv_titlebar_go_back);
        tvLeftAction = (TextView) findViewById(R.id.tv_titlebar_left_action);

        ivTitle = (ImageView) findViewById(R.id.iv_titlebar_title);
        tvTitle = (TextView) findViewById(R.id.tv_titlebar_title);

        ivRightFirstAction = (ImageView) findViewById(R.id.iv_titlebar_right_first_action);
        ivRightSecondAction = (ImageView) findViewById(R.id.iv_titlebar_right_second_action);
        tvRightAction = (TextView) findViewById(R.id.tv_titlebar_right_action);
        divider = findViewById(R.id.divider);

        ivGoBack.setVisibility(GONE);
        tvLeftAction.setVisibility(GONE);
        ivTitle.setVisibility(GONE);
        tvTitle.setVisibility(GONE);
        ivRightFirstAction.setVisibility(GONE);
        ivRightSecondAction.setVisibility(GONE);
        tvRightAction.setVisibility(GONE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Titlebar);

        if (a.hasValue(R.styleable.Titlebar_bar_title)) {
            String title = a.getString(R.styleable.Titlebar_bar_title);
            setTitle(title);
        }

        if (a.hasValue(R.styleable.Titlebar_title_src)) {
            int resId = a.getResourceId(R.styleable.Titlebar_title_src, 0);
            setTitleImage(resId);
        }

        if (a.hasValue(R.styleable.Titlebar_go_back)) {
            setGoBack(a.getResourceId(R.styleable.Titlebar_go_back, 0));

        } else {
            setGoBack(R.drawable.arrow_left_white_selector, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });
        }

        if (a.hasValue(R.styleable.Titlebar_go_back_display)) {
            setGoBackDisplay(a.getBoolean(R.styleable.Titlebar_go_back_display, true));
        }

        if (a.hasValue(R.styleable.Titlebar_left_action)) {
            String title = a.getString(R.styleable.Titlebar_left_action);
            setLeftAction(title);
        }

        if (a.hasValue(R.styleable.Titlebar_right_action)) {
            String title = a.getString(R.styleable.Titlebar_right_action);
            setRightAction(title);
        }

        if (a.hasValue(R.styleable.Titlebar_first_right_image_action)) {
            setRightImageAction(IMAGE_ACTION_RIGHT_FIRST,
                    a.getResourceId(R.styleable.Titlebar_first_right_image_action, 0));
        }

        if (a.hasValue(R.styleable.Titlebar_second_right_image_action)) {
            setRightImageAction(IMAGE_ACTION_RIGHT_SECOND,
                    a.getResourceId(R.styleable.Titlebar_second_right_image_action, 0));
        }

        if (a.hasValue(R.styleable.Titlebar_bar_background)) {
            int color = a.getColor(R.styleable.Titlebar_bar_background, Color.parseColor("#F85757"));
            setBackgroundColor(color);
        }

        if (a.hasValue(R.styleable.Titlebar_divider_color)) {
            int color = a.getColor(R.styleable.Titlebar_divider_color, Color.parseColor("#C7C7C7"));
            setDividerColor(color);
        }

        if (a.hasValue(R.styleable.Titlebar_title_color)) {
            ColorStateList color = a.getColorStateList(R.styleable.Titlebar_title_color);
            tvTitle.setTextColor(color);
        }

        if (a.hasValue(R.styleable.Titlebar_left_action_color)) {
            ColorStateList color = a.getColorStateList(R.styleable.Titlebar_left_action_color);
            tvLeftAction.setTextColor(color);
        }

        if (a.hasValue(R.styleable.Titlebar_right_action_color)) {
            ColorStateList color = a.getColorStateList(R.styleable.Titlebar_right_action_color);
            tvRightAction.setTextColor(color);
        }

        a.recycle();

    }

    private void setLeftActionColor(int color) {
        tvLeftAction.setTextColor(color);
    }

    public void setTitle(String title) {
        tvTitle.setVisibility(VISIBLE);
        ivTitle.setVisibility(GONE);
        tvTitle.setText(title);
    }

    public void setTitle(String title, OnClickListener onClickListener) {
        tvTitle.setVisibility(VISIBLE);
        ivTitle.setVisibility(GONE);
        tvTitle.setText(title);
        tvTitle.setClickable(true);
        tvTitle.setOnClickListener(onClickListener);
    }

    public void setTitleClickListener(OnClickListener onClickListener) {
        tvTitle.setClickable(true);
        tvTitle.setOnClickListener(onClickListener);
    }

    public void setTitleImage(int imageRes) {
        ivTitle.setVisibility(VISIBLE);
        tvTitle.setVisibility(GONE);
        ivTitle.setImageResource(imageRes);
    }

    public void setTitleImageClickListener(OnClickListener onClickListener) {
        ivTitle.setOnClickListener(onClickListener);
    }

    public void setGoBack(@DrawableRes int imageRes) {
        ivGoBack.setVisibility(VISIBLE);
        tvLeftAction.setVisibility(GONE);
        ivGoBack.setImageResource(imageRes);
    }

    public void setGoBack(@DrawableRes int imageRes, OnClickListener onClickListener) {
        ivGoBack.setVisibility(VISIBLE);
        tvLeftAction.setVisibility(GONE);
        ivGoBack.setImageResource(imageRes);
        ivGoBack.setOnClickListener(onClickListener);
    }

    public void setGoBackClickListener(OnClickListener onClickListener) {
        ivGoBack.setOnClickListener(onClickListener);
    }

    public void setLeftAction(String text) {
        tvLeftAction.setVisibility(VISIBLE);
        ivGoBack.setVisibility(GONE);
        tvLeftAction.setText(text);
    }

    public void setLeftAction(String text, OnClickListener onClickListener) {
        tvLeftAction.setVisibility(VISIBLE);
        ivGoBack.setVisibility(GONE);
        tvLeftAction.setText(text);
        tvLeftAction.setClickable(true);
        tvLeftAction.setOnClickListener(onClickListener);
    }

    public void setRightAction(String text) {
        tvRightAction.setVisibility(VISIBLE);
        ivRightFirstAction.setVisibility(GONE);
        ivRightSecondAction.setVisibility(GONE);
        tvRightAction.setText(text);
    }

    public void setLeftActionClickListener(OnClickListener listener) {
        tvLeftAction.setClickable(true);
        tvLeftAction.setOnClickListener(listener);
    }

    public void setRightActionClickListener(OnClickListener listener) {
        tvRightAction.setClickable(true);
        tvRightAction.setOnClickListener(listener);
    }

    public void setRightAction(String text, OnClickListener onClickListener) {
        tvRightAction.setVisibility(VISIBLE);
        ivRightFirstAction.setVisibility(GONE);
        ivRightSecondAction.setVisibility(GONE);
        tvRightAction.setText(text);
        tvRightAction.setClickable(true);
        tvRightAction.setOnClickListener(onClickListener);
    }

    public void setRightImageAction(int whichAction, @DrawableRes int imgId, OnClickListener onClickListener) {
        switch (whichAction) {
            case IMAGE_ACTION_RIGHT_FIRST:
                ivRightFirstAction.setVisibility(VISIBLE);
                ivRightSecondAction.setVisibility(GONE);
                tvRightAction.setVisibility(GONE);
                ivRightFirstAction.setImageResource(imgId);
                ivRightFirstAction.setOnClickListener(onClickListener);
                break;

            case IMAGE_ACTION_RIGHT_SECOND:
                if (ivRightFirstAction.isShown()) {
                    return;
                }
                ivRightSecondAction.setVisibility(VISIBLE);
                tvRightAction.setVisibility(GONE);
                ivRightSecondAction.setImageResource(imgId);
                ivRightSecondAction.setOnClickListener(onClickListener);
                break;

            default:
                break;
        }
    }

    public void setRightImageAction(int whichAction, @DrawableRes int imgId) {
        switch (whichAction) {
            case IMAGE_ACTION_RIGHT_FIRST:
                ivRightFirstAction.setVisibility(VISIBLE);
                ivRightSecondAction.setVisibility(GONE);
                tvRightAction.setVisibility(GONE);
                ivRightFirstAction.setImageResource(imgId);
                break;

            case IMAGE_ACTION_RIGHT_SECOND:
                if (ivRightFirstAction.isShown()) {
                    return;
                }
                ivRightSecondAction.setVisibility(VISIBLE);
                tvRightAction.setVisibility(GONE);
                ivRightSecondAction.setImageResource(imgId);
                break;

            default:
                break;
        }
    }

    public void setRightImageActionClickListener(int whichAction, OnClickListener onClickListener) {
        switch (whichAction) {
            case IMAGE_ACTION_RIGHT_FIRST:
                ivRightFirstAction.setOnClickListener(onClickListener);
                break;

            case IMAGE_ACTION_RIGHT_SECOND:
                ivRightSecondAction.setOnClickListener(onClickListener);
                break;

            default:
                break;
        }
    }

    public int getBackgroundColor() {
        Drawable drawable = rlParentPanel.getBackground();
        if (drawable instanceof ColorDrawable) {
            return ((ColorDrawable) drawable).getColor();
        }

        return Color.WHITE;
    }

    @Override
    public void setBackgroundColor(int color) {
        rlParentPanel.setBackgroundColor(color);
        if (getContext() instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Activity) getContext()).getWindow().setStatusBarColor(color);
            }
        }
    }

    public void setGoBackDisplay(boolean isShow) {
        if (isShow) {
            ivGoBack.setVisibility(VISIBLE);
        } else {
            ivGoBack.setVisibility(GONE);
        }
    }

    public void setDividerColor(int color) {
        divider.setBackgroundColor(color);
    }

    public void setTitleColor(int titleColor) {
        tvTitle.setTextColor(titleColor);
    }

    public void setRightActionColor(int rightActionColor) {
        tvRightAction.setTextColor(rightActionColor);
    }
}
