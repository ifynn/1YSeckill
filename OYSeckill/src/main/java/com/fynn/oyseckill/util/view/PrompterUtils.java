package com.fynn.oyseckill.util.view;

import android.app.Dialog;
import android.content.Context;

import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Prompter;

/**
 * Created by Fynn on 16/8/28.
 */
public final class PrompterUtils {

    /**
     * 显示提示对话框
     *
     * @param context
     * @param msg
     * @param ok
     * @param listener
     */
    public static void showCaution(
            Context context, String msg, String ok, IPrompter.OnClickListener listener) {
        new Prompter.Builder(context)
                .setMessage(msg)
                .setPositiveButton(ok, listener)
                .show();
    }

    public static void showConfirm(
            Context context, String msg, String ok, IPrompter.OnClickListener listener) {
        new Prompter.Builder(context)
                .setMessage(msg)
                .setNegativeButton("取消", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(ok, listener)
                .show();
    }

    public static void showConfirm(
            Context context, String msg, String ok, String cancel,
            IPrompter.OnClickListener listener) {
        new Prompter.Builder(context)
                .setMessage(msg)
                .setNegativeButton(cancel, new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(ok, listener)
                .show();
    }

    public static void showChooser(
            Context context, String msg, String ok, String cancel,
            IPrompter.OnClickListener listener) {
        new Prompter.Builder(context)
                .setMessage(msg)
                .setNegativeButton(cancel, listener)
                .setPositiveButton(ok, listener)
                .show();
    }
}
