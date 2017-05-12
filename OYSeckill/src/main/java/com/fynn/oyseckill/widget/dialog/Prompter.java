package com.fynn.oyseckill.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;

import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.TextUtils;

/**
 * Created by fynn on 16/6/17.
 */
public class Prompter extends Dialog {

    public Prompter(Context context) {
        super(context);
    }

    public Prompter(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected Prompter(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        private Context mContext;
        private Prompter prompter;
        private int theme = R.style.Prompter;
        private View contentView;
        private String positiveButtonText;
        private String negativeButtonText;
        private IPrompter.OnClickListener onPositiveButtonClickListener;
        private IPrompter.OnClickListener onNegativeButtonClickListener;
        private int positiveButtonTextColor = Color.parseColor("#4876FF");
        private int negativeButtonTextColor = Color.parseColor("#383838");
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = false;
        private String title;
        private int titleColor = Color.parseColor("#383838");
        private String message;
        private int messageColor = Color.parseColor("#5E5E5E");
        private float titleSize = 15;
        private float messageSize = 14;
        private float positiveButtonTextSize = 14;
        private float negativeButtonTextSize = 14;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder(Context mContext, int theme) {
            this.mContext = mContext;
            this.theme = theme;
        }

        public Builder(Context mContext, boolean cancelable, boolean canceledOnTouchOutside) {
            this.mContext = mContext;
            this.cancelable = cancelable;
            this.canceledOnTouchOutside = canceledOnTouchOutside;
        }

        public Builder setPositiveButton(String text, IPrompter.OnClickListener onPositiveButtonClickListener) {
            this.positiveButtonText = text;
            this.onPositiveButtonClickListener = onPositiveButtonClickListener;
            return this;
        }

        public Builder setNegativeButton(String text, IPrompter.OnClickListener onNegativeButtonClickListener) {
            this.negativeButtonText = text;
            this.onNegativeButtonClickListener = onNegativeButtonClickListener;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setOnPositiveButtonClickListener(IPrompter.OnClickListener onPositiveButtonClickListener) {
            this.onPositiveButtonClickListener = onPositiveButtonClickListener;
            return this;
        }

        public Builder setOnNegativeButtonClickListener(IPrompter.OnClickListener onNegativeButtonClickListener) {
            this.onNegativeButtonClickListener = onNegativeButtonClickListener;
            return this;
        }

        public Builder setPositiveButtonTextColor(int positiveButtonTextColor) {
            this.positiveButtonTextColor = positiveButtonTextColor;
            return this;
        }

        public Builder setNegativeButtonTextColor(int negativeButtonTextColor) {
            this.negativeButtonTextColor = negativeButtonTextColor;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageColor(int messageColor) {
            this.messageColor = messageColor;
            return this;
        }

        public Builder setTitleSize(float titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        public Builder setMessageSize(float messageSize) {
            this.messageSize = messageSize;
            return this;
        }

        public Builder setPositiveButtonTextSize(float positiveButtonTextSize) {
            this.positiveButtonTextSize = positiveButtonTextSize;
            return this;
        }

        public Builder setNegativeButtonTextSize(float negativeButtonTextSize) {
            this.negativeButtonTextSize = negativeButtonTextSize;
            return this;
        }

        public Prompter create() {
            if (prompter == null) {
                prompter = new Prompter(mContext, theme);
            }

            View contentView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_dialog_base, null);
            TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
            TextView tvMessage = (TextView) contentView.findViewById(R.id.tv_message);
            TextView singleButton = (TextView) contentView.findViewById(R.id.button_single);
            TextView leftButton = (TextView) contentView.findViewById(R.id.button_left);
            TextView rightButton = (TextView) contentView.findViewById(R.id.button_right);
            LinearLayout llDividerContentBtn = (LinearLayout) contentView.findViewById(R.id.ll_divider_btn_content);
            LinearLayout llDividerBtnBtn = (LinearLayout) contentView.findViewById(R.id.ll_divider_btn_btn);
            LinearLayout llContent = (LinearLayout) contentView.findViewById(R.id.ll_content);
            LinearLayout llBtnContainer = (LinearLayout) contentView.findViewById(R.id.ll_btn_container);

            if (title == null) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
                tvTitle.setTextColor(titleColor);
                tvTitle.setTextSize(titleSize);
            }

            if (positiveButtonText == null && negativeButtonText == null) {
                llBtnContainer.setVisibility(View.GONE);
                llDividerContentBtn.setVisibility(View.GONE);

            } else {
                llBtnContainer.setVisibility(View.VISIBLE);
                llDividerContentBtn.setVisibility(View.VISIBLE);

                if (positiveButtonText != null && negativeButtonText != null) {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    singleButton.setVisibility(View.GONE);
                    llDividerBtnBtn.setVisibility(View.VISIBLE);

                    rightButton.setTextSize(positiveButtonTextSize);
                    rightButton.setText(positiveButtonText);
                    rightButton.setTextColor(positiveButtonTextColor);

                    leftButton.setTextSize(negativeButtonTextSize);
                    leftButton.setTextColor(negativeButtonTextColor);
                    leftButton.setText(negativeButtonText);

                    if (onPositiveButtonClickListener != null) {
                        rightButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onPositiveButtonClickListener.onClick(prompter, IPrompter.BUTTON_POSITIVE);
                            }
                        });
                    }

                    if (onNegativeButtonClickListener != null) {
                        leftButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onNegativeButtonClickListener.onClick(prompter, IPrompter.BUTTON_NEGATIVE);
                            }
                        });
                    }

                } else {
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.GONE);
                    singleButton.setVisibility(View.VISIBLE);
                    llDividerBtnBtn.setVisibility(View.GONE);

                    float textSize = positiveButtonText == null ? negativeButtonTextSize : positiveButtonTextSize;
                    String text = positiveButtonText == null ? negativeButtonText : positiveButtonText;
                    int color = positiveButtonText == null ? negativeButtonTextColor : positiveButtonTextColor;
                    final int witch = positiveButtonText == null ? IPrompter.BUTTON_NEGATIVE : IPrompter.BUTTON_POSITIVE;

                    singleButton.setTextSize(textSize);
                    singleButton.setText(text);
                    singleButton.setTextColor(color);

                    if (positiveButtonText != null) {
                        if (onPositiveButtonClickListener != null) {
                            singleButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onPositiveButtonClickListener.onClick(prompter, witch);
                                }
                            });
                        }
                    } else if (negativeButtonText != null) {
                        if (onNegativeButtonClickListener != null) {
                            singleButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onNegativeButtonClickListener.onClick(prompter, witch);
                                }
                            });
                        }
                    }

                }
            }

            if (this.contentView != null) {
                llContent.removeAllViews();
                llContent.addView(this.contentView);

            } else {
                if (TextUtils.isEmpty(message)) {
                    tvMessage.setVisibility(View.GONE);
                } else {
                    tvMessage.setText(message);
                    tvMessage.setTextSize(messageSize);
                    tvMessage.setTextColor(messageColor);
                }
            }

            prompter.setCancelable(cancelable);
            prompter.setCanceledOnTouchOutside(canceledOnTouchOutside);

            prompter.setContentView(contentView);

            size();

            return prompter;
        }

        public Prompter show() {
            create().show();
            return prompter;
        }

        private void size() {
            Window window = prompter.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            int margin = DensityUtils.dip2px(10);
            params.width = DensityUtils.getScreenWidth() - margin * 2;
            params.y = margin;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
    }
}
