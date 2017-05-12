package com.fynn.oyseckill.app.module.home.card;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.widget.VerticalViewPager;
import com.fynn.oyseckill.widget.adapter.PagerAdapter;

import java.util.ArrayList;

/**
 * Created by Fynn on 2016/4/25.
 */
public class VerPagerCard extends LinearLayout {

    private VerticalViewPager vertiPager;
    private ArrayList<String> list = new ArrayList<String>();

    public VerPagerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_verti_pager_card, this);
        vertiPager = (VerticalViewPager) findViewById(R.id.verti_pager);
        vertiPager.setScrollDuration(1000);
        vertiPager.setScrollEnable(false);
        vertiPager.setCyclic(true);

        list.add("秒杀iPhone6S，赶快来抢！");
        list.add("低价促销，仅此一天");

        vertiPager.setAdapter(new VerPagerAdapter());
    }

    class VerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            tv.setText(list.get(position));
            tv.setEllipsize(TextUtils.TruncateAt.END);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(tv, params);

            return tv;
        }
    }
}
