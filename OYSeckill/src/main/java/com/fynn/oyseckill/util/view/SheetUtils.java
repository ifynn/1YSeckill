package com.fynn.oyseckill.util.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.Nullable;
import android.view.View;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Sheet;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fynn on 2016/6/24.
 */
public final class SheetUtils {

    public static void showConfirmSheet(
            Activity activity, String message, String ok, String cancel,
            final IPrompter.OnClickListener onClickListener) {

        new Sheet.Builder(activity)
                .setMessage(message)
                .setConfirm(cancel, new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, IPrompter.BUTTON_NEGATIVE);
                        }
                    }
                })
                .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, View view, int position) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, IPrompter.BUTTON_POSITIVE);
                        }
                    }
                })
                .addItem(ok)
                .show();
    }

    public static void showConfirmSheet(
            Activity activity, String message, String ok,
            final IPrompter.OnClickListener onClickListener) {
        new Sheet.Builder(activity)
                .setMessage(message)
                .setConfirm("取消", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, View view, int position) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, IPrompter.BUTTON_POSITIVE);
                        }
                    }
                })
                .addItem(ok)
                .show();
    }

    public static void showCautionSheet(
            Activity activity, String message, String ok,
            final IPrompter.OnClickListener onClickListener) {
        new Sheet.Builder(activity)
                .setMessage(message)
                .setConfirm("取消", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, View view, int position) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, IPrompter.BUTTON_POSITIVE);
                        }
                    }
                })
                .addItem(ok, activity.getResources().getColor(R.color.red_F85757))
                .show();
    }

    public static void showItemSheet(
            Activity activity, IPrompter.OnItemClickListener listener,
            @Nullable String msg, String... items) {
        new Sheet.Builder(activity)
                .setConfirm("取消", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setSimpleItems(Arrays.asList(items))
                .setOnItemClickListener(listener)
                .setMessage(msg)
                .show();
    }

    public static void showItemSheet(
            Activity activity, IPrompter.OnItemClickListener listener,
            @Nullable String msg, List<String> items) {
        new Sheet.Builder(activity)
                .setConfirm("取消", new IPrompter.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setSimpleItems(items)
                .setOnItemClickListener(listener)
                .setMessage(msg)
                .show();
    }
}
