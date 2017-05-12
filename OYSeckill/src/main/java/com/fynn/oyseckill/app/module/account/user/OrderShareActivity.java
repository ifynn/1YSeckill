package com.fynn.oyseckill.app.module.account.user;

import android.os.Bundle;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.main.OrderShareFragment;

/**
 * Created by fynn on 16/7/17.
 */
public class OrderShareActivity extends BaseActivity {

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
        titlebar.setTitle("我的晒单");
        OrderShareFragment osFragment = new OrderShareFragment();
        Bundle params = new Bundle();
        params.putString("from", "my.order.share");
        osFragment.setArguments(params);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_base, osFragment)
                .commit();
    }
}
