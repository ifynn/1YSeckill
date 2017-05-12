package com.fynn.oyseckill.app.module.home.detail;

import android.os.Bundle;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;

/**
 * Created by Fynn on 2016/7/15.
 */
public class ProductImageTextDetailActivity extends BaseActivity {

    @Override
    public int getContentResId() {
        return R.layout.activity_fragment_base;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        ProductImageTextFragment fragment = new ProductImageTextFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_base, fragment).commit();
    }
}
