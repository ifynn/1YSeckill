package com.fynn.oyseckill.app.module.home.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.widget.RectImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fynn on 2016/7/7.
 */
public class ProductDetailHeaderPager extends ViewPager {

    private Context mContext;
    private List<String> picsUrl;
    private HeaderPagerAdapter adapter;

    public ProductDetailHeaderPager(Context context) {
        super(context);
        init(context);
    }

    public ProductDetailHeaderPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        picsUrl = new ArrayList<String>();
        adapter = new HeaderPagerAdapter();
        setAdapter(adapter);
    }

    public void update(List<String> urls) {
        if (urls == null) {
            return;
        }
        picsUrl.clear();
        picsUrl.addAll(urls);
        adapter.notifyDataSetChanged();
    }

    class HeaderPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return picsUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RectImageView imageView = new RectImageView(getContext());
            imageView.setBackgroundColor(0xffffffff);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            ImageUtils.getInstance().displayOriginal(picsUrl.get(position), imageView,
                    R.drawable.pic_default_square_white);
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
