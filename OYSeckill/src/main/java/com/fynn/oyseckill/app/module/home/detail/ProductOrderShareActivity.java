package com.fynn.oyseckill.app.module.home.detail;

import android.os.Bundle;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.main.OrderShareFragment;

/**
 * Created by fynn on 16/7/17.
 */
public class ProductOrderShareActivity extends BaseActivity {

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_fragment_base;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        titlebar.setTitle("晒单");
        OrderShareFragment osFragment = new OrderShareFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_base, osFragment)
                .commit();
    }
}
