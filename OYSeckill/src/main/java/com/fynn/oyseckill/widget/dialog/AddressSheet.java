package com.fynn.oyseckill.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;

import org.appu.common.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fynn on 16/6/17.
 */
public class AddressSheet extends Dialog {

    public AddressSheet(Context context) {
        super(context);
    }

    public AddressSheet(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddressSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        private Context mContext;
        private AddressSheet sheet;
        private int theme = R.style.Prompter;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private List<SheetItem> items = new ArrayList<SheetItem>();
        private BAdapter adapter;
        private IPrompter.OnItemStateClickListener onItemClickListener;
        private IPrompter.OnClickListener onConfirmListener;

        private String message;
        private int messageColor = Color.parseColor("#707070");
        private float messageSize = 12;

        private String confirmText;
        private int confirmTextColor = Color.parseColor("#4F4F4F");
        private float confirmTextSize = 14;

        private ProgressBar progress;

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

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setItems(List<SheetItem> items) {
            if (items != null && !items.isEmpty()) {
                this.items.addAll(items);
            }
            return this;
        }

        public Builder addItem(SheetItem item) {
            if (item != null) {
                items.add(item);
            }
            return this;
        }

        public Builder addItem(String itemText) {
            if (itemText != null) {
                SheetItem sheetItem = new SheetItem(itemText);
                items.add(sheetItem);
            }
            return this;
        }

        public Builder addItem(String itemText, int color) {
            if (itemText != null) {
                SheetItem sheetItem = new SheetItem(itemText, color);
                items.add(sheetItem);
            }
            return this;
        }

        public Builder setOnItemClickListener(IPrompter.OnItemStateClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
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

        public Builder setMessageSize(float messageSize) {
            this.messageSize = messageSize;
            return this;
        }

        public Builder setConfirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        public Builder setConfirmTextColor(int confirmTextColor) {
            this.confirmTextColor = confirmTextColor;
            return this;
        }

        public Builder setConfirmTextSize(float confirmTextSize) {
            this.confirmTextSize = confirmTextSize;
            return this;
        }

        public Builder setConfirm(String text, IPrompter.OnClickListener onConfirmListener) {
            this.confirmText = text;
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        public void setOnConfirmListener(IPrompter.OnClickListener onConfirmListener) {
            this.onConfirmListener = onConfirmListener;
        }

        public AddressSheet create() {
            if (sheet == null) {
                sheet = new AddressSheet(mContext, theme);
            }

            final View contentView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_sheet_base, null);
            ListView lvMenu = (ListView) contentView.findViewById(R.id.lv_menu);
            TextView tvMsg = (TextView) contentView.findViewById(R.id.tv_message);
            final TextView tvOk = (TextView) contentView.findViewById(R.id.tv_ok);
            progress = (ProgressBar) contentView.findViewById(R.id.progress);

            if (message == null) {
                tvMsg.setVisibility(View.GONE);
            } else {
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(message);
                tvMsg.setTextSize(messageSize);
                tvMsg.setTextColor(messageColor);
            }

            if (confirmText == null) {
                tvOk.setVisibility(View.GONE);
                lvMenu.setPadding(0, 0, 0, 0);
            } else {
                tvOk.setVisibility(View.VISIBLE);
                tvOk.setText(confirmText);
                tvOk.setTextSize(confirmTextSize);
                tvOk.setTextColor(confirmTextColor);
                if (onConfirmListener != null) {
                    tvOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onConfirmListener.onClick(sheet, IPrompter.BUTTON_POSITIVE);
                        }
                    });
                }
                lvMenu.setPadding(0, 0, 0, DensityUtils.dip2px(8));
            }

            if (items != null && !items.isEmpty()) {
                adapter = new BAdapter();
                lvMenu.setAdapter(adapter);
            }

            sheet.setCancelable(cancelable);
            sheet.setCanceledOnTouchOutside(canceledOnTouchOutside);

            sheet.setContentView(contentView);

            size();

            return sheet;
        }

        public AddressSheet show() {
            create().show();
            return sheet;
        }

        private void size() {
            Window window = sheet.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DensityUtils.getScreenWidth();
            if (items.size() > 8) {
                params.height = DensityUtils.getScreenHeight() * 3 / 4;
            }
            params.y = 0;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }

        class BAdapter extends BaseAdapter {
            @Override
            public int getCount() {
                return items.size();
            }

            @Override
            public Object getItem(int position) {
                return items.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.sheet_item, null);

                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);

                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                SheetItem is = items.get(position);
                int color = is.getColor();
                float size = is.getSize();
                String text = is.getText();

                viewHolder.tvItem.setText(text);
                viewHolder.tvItem.setTextSize(size);
                viewHolder.tvItem.setTextColor(color);

                if (onItemClickListener != null) {
                    viewHolder.tvItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onItemClick(sheet, progress, position);
                        }
                    });
                }

                return convertView;
            }

            class ViewHolder {
                private TextView tvItem;

                public ViewHolder(View convertView) {
                    tvItem = (TextView) convertView.findViewById(R.id.tv_item);
                }
            }
        }
    }
}
