package com.fynn.oyseckill.widget.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.ProgressBar;

public interface IPrompter {

    public static final int BUTTON_POSITIVE = -1;

    public static final int BUTTON_NEGATIVE = -2;

    interface OnClickListener {
        public void onClick(Dialog dialog, int which);
    }

    interface OnItemClickListener {
        public void onItemClick(Dialog dialog, View view, int position);
    }

    interface OnItemStateClickListener {
        public void onItemClick(Dialog dialog, ProgressBar progressBar, int position);
    }
}