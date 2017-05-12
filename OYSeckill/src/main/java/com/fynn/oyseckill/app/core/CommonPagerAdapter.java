package com.fynn.oyseckill.app.core;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import com.fynn.oyseckill.widget.VerticalViewPager;
import com.fynn.oyseckill.widget.adapter.PagerAdapter;

import java.util.List;

/**
 * Created by Fynn on 16/7/7.
 */
public class CommonPagerAdapter extends PagerAdapter {

    private List<View> views;

    public CommonPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }


    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((VerticalViewPager) container).addView(views.get(position), 0);
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
