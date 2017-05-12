package com.fynn.oyseckill.app.module.account.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;

/**
 * Created by Fynn on 2016/7/20.
 */
public class MySeckillActivity extends BaseActivity {

    private TabLayout tab;
    private ViewPager pager;

    private SeckillAdapter adapter;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_my_seckill;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tab = $(R.id.tab_seckill);
        pager = $(R.id.vp_seckill);

        adapter = new SeckillAdapter(getSupportFragmentManager());
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
    }

    private class SeckillAdapter extends FragmentPagerAdapter {
        public SeckillAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MySeckillFragment fragment = new MySeckillFragment();
            Bundle params = new Bundle();
            params.putInt("position", position);
            fragment.setArguments(params);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "全部";

                case 1:
                    return "进行中";

                case 2:
                    return "已揭晓";

                default:
                    return null;
            }
        }
    }
}
