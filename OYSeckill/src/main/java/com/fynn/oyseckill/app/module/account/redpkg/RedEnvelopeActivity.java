package com.fynn.oyseckill.app.module.account.redpkg;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;

/**
 * Created by Fynn on 2016/6/29.
 */
public class RedEnvelopeActivity extends BaseActivity {

    private TabLayout tabRedEnvelope;
    private ViewPager vpRe;

    private REAdapter adapter;

    @Override
    public int getContentResId() {
        return R.layout.activity_red_envelope;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tabRedEnvelope = $(R.id.tab_red_envelope);
        vpRe = $(R.id.vp_re);

        adapter = new REAdapter(getSupportFragmentManager());
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        vpRe.setAdapter(adapter);
        tabRedEnvelope.setupWithViewPager(vpRe);
    }

    class REAdapter extends FragmentPagerAdapter {

        public REAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RedEnvelopeFragment fragment = new RedEnvelopeFragment();
            Bundle params = new Bundle();
            switch (position) {
                case 0:
                    params.putInt("type", RedPkgType.AVAILABLE.getValue());
                    break;

                case 1:
                    params.putInt("type", RedPkgType.UNAVAILABLE.getValue());

                    break;
            }
            fragment.setArguments(params);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "未使用";

                case 1:
                    return "不可用";

                default:
                    return null;
            }
        }
    }
}
