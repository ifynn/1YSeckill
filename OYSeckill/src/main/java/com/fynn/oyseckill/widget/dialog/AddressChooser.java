package com.fynn.oyseckill.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.widget.NoScrollViewPager;

import org.appu.common.utils.DensityUtils;

/**
 * Created by fynn on 16/6/25.
 */
public class AddressChooser extends Dialog {

    public AddressChooser(Context context) {
        super(context);
    }

    public AddressChooser(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddressChooser(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DensityUtils.getScreenWidth();
        params.height = DensityUtils.getScreenHeight() * 3 / 5;
        params.y = 0;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    public static class Builder {

        Context mContext;
        AddressChooser chooser;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public AddressChooser create() {
            if (chooser == null) {
                chooser = new AddressChooser(mContext, R.style.Prompter);
            }

            View contentView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_dialog_address_container, null);
            ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);
            TabLayout tabPcc = (TabLayout) contentView.findViewById(R.id.tab_pcc);
            NoScrollViewPager vpAddress = (NoScrollViewPager) contentView.findViewById(R.id.vp_address);
            ProgressBar progress = (ProgressBar) contentView.findViewById(R.id.progress);

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooser.dismiss();
                }
            });

            chooser.setContentView(contentView);

            return chooser;
        }

        public AddressChooser show() {
            create().show();
            return chooser;
        }

        public class SectionsPagerAdapter extends PagerAdapter {

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return super.getPageTitle(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return super.instantiateItem(container, position);
            }
        }
    }
}
