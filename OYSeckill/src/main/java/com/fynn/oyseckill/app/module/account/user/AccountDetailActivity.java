package com.fynn.oyseckill.app.module.account.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.core.BaseFragment;

/**
 * Created by Fynn on 2016/8/8.
 */
public class AccountDetailActivity extends BaseActivity {

    private TabLayout tab;
    private ViewPager pager;

    private AccountTabAdapter adapter;
    private BaseFragment[] fragments;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_account_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tab = $(R.id.tab_account_detail);
        pager = $(R.id.vp_account_detail);

        adapter = new AccountTabAdapter(getSupportFragmentManager());
        fragments = new BaseFragment[2];
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        fragments[0] = new RechargeRecordFragment();
        fragments[1] = new ExpenseRecordFragment();

        pager.setOffscreenPageLimit(fragments.length);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
    }

    private class AccountTabAdapter extends FragmentPagerAdapter {
        public AccountTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "充值";

                case 1:
                    return "消费";

                default:
                    return null;
            }
        }
    }
}
