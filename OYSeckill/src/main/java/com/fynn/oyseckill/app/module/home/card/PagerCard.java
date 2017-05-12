package com.fynn.oyseckill.app.module.home.card;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.home.detail.ProductDetailActivity;
import com.fynn.oyseckill.model.entity.Banner;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.web.WebActivity;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import org.appu.common.ParamMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fynn on 2016/4/25.
 */
public class PagerCard extends LinearLayout {

    private RollPagerView rollPagerView;
    private RollAdapter rollAdapter;

    private List<Banner> banners;

    public PagerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        banners = new ArrayList<>();

        LayoutInflater.from(getContext()).inflate(R.layout.view_pager_card, this);

        rollPagerView = (RollPagerView) findViewById(R.id.roll_pager_view);
        rollAdapter = new RollAdapter(rollPagerView);
        rollPagerView.setAdapter(rollAdapter);

        ColorPointHintView cphv = new ColorPointHintView(
                getContext(), Color.WHITE, Color.parseColor("#99D6D6D6"));
        rollPagerView.setHintView(cphv);
    }

    public RollPagerView getRollPagerView() {
        return rollPagerView;
    }

    public void setPagerData(List<Banner> banners) {
        this.banners.clear();
        this.banners.addAll(banners);
        rollAdapter.notifyDataSetChanged();
    }

    class RollAdapter extends LoopPagerAdapter {

        public RollAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            Banner banner = banners.get(position);
            if (!TextUtils.isEmpty(banner.getPicUrl())) {
                ImageUtils.getInstance().display(banner.getPicUrl(), view, R.drawable.pic_default_rect_banner);
            } else {
                view.setImageResource(R.drawable.pic_default_rect_banner);
            }

            Long issueNo = null;
            String h5Url = null;
            Product product = banner.getProduct();
            if (product != null) {
                Issue issue = product.getCurrentIssue();
                if (issue != null) {
                    issueNo = issue.getIssueNumber();
                }
            }

            if (issueNo == null) {
                h5Url = banner.getH5Url();
            }

            if (issueNo != null) {
                final Long finalIssueNo = issueNo;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("issueNo", finalIssueNo);
                        ((BaseActivity) getContext()).startActivity(ProductDetailActivity.class, params);
                    }
                });
            } else {
                if (!TextUtils.isEmpty(h5Url)) {
                    final String finalH5Url = h5Url;
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParamMap<String, Object> params = new ParamMap<String, Object>();
                            params.put("url", finalH5Url);
                            ((BaseActivity) getContext()).startActivity(WebActivity.class, params);
                        }
                    });
                } else {
                    view.setOnClickListener(null);
                }
            }

            return view;
        }

        @Override
        public int getRealCount() {
            return banners.size();
        }
    }
}
