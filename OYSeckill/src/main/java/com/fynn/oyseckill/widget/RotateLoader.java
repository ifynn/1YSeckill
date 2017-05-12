package com.fynn.oyseckill.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;

public class RotateLoader extends Dialog {

    public static final int ROTATE_ORDER = 0X01;
    public static final int ROTATE_REVERSE = 0X02;

    public RotateLoader(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {

        private RotateLoader loadingDialog;
        private Context context;
        private String message;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;

        public Builder(Context context) {
            this(context, null);
        }

        public Builder(Context context, String message) {
            this.context = context;
            this.message = message;
            loadingDialog = new RotateLoader(context,
                    R.style.LoadingDialogStyle);
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        @SuppressLint("InflateParams")
        public RotateLoader create() {
            if (loadingDialog == null) {
                return null;
            }

            LayoutInflater inflater = LayoutInflater.from(context);
            View root = inflater.inflate(R.layout.dialog_progress_normal, null);
            TextView messageTips = (TextView) root.findViewById(R.id.message);

            if (TextUtils.isEmpty(message)) {
                messageTips.setVisibility(View.GONE);
            } else {
                messageTips.setVisibility(View.VISIBLE);
                messageTips.setText(message);
            }

            loadingDialog.setCancelable(cancelable);
            loadingDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

            loadingDialog.setContentView(root, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            return loadingDialog;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public RotateLoader show() {
            create().show();
            return loadingDialog;
        }
    }
}
