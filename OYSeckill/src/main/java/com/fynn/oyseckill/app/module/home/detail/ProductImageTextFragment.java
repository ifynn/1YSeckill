package com.fynn.oyseckill.app.module.home.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.ImageUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/7/15.
 */
public class ProductImageTextFragment extends BaseFragment {

    private ListView lvImages;
    private LinearLayout llEmpty;

    private List<String> images;
    private ImageAdapter adapter;

    private int productId;

    @Override
    public int getContentResId() {
        return R.layout.fragment_product_detail_image_text;
    }

    @Override
    public void handleIntent() {
        ParamMap params = getParams();
        if (params != null) {
            productId = (int) params.get("productId");
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvImages = $(R.id.lv_detail_pics);
        llEmpty = $(R.id.ll_empty);

        images = new ArrayList<String>();
        adapter = new ImageAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        lvImages.setEmptyView(llEmpty);
        queryDetailImage();
    }

    private void queryDetailImage() {
        BmobQuery<Product> query = new BmobQuery<Product>();
        query.addWhereEqualTo("productId", productId);
        query.findObjects(activity, new FindListener<Product>() {
            @Override
            public void onSuccess(List<Product> list) {
                if (list != null && !list.isEmpty()) {
                    Product product = list.get(0);
                    images.clear();
                    images.addAll(product.getDetailImages());
                    if (!TextUtils.isEmpty(product.getTopTips())) {
                        View v = LayoutInflater.from(activity).inflate(R.layout.layout_detail_text, null);
                        TextView tvDesc = ViewUtils.findViewById(v, R.id.tv_product_detail_desc);
                        tvDesc.setText(Html.fromHtml(product.getTopTips()));
                        tvDesc.setLineSpacing(0.0f, 1.2f);
                        lvImages.addHeaderView(v, null, false);
                    }

                    if (!TextUtils.isEmpty(product.getBottomTips())) {
                        View v = LayoutInflater.from(activity).inflate(R.layout.layout_detail_text, null);
                        TextView tvDesc = ViewUtils.findViewById(v, R.id.tv_product_detail_desc);
                        tvDesc.setText(Html.fromHtml(product.getBottomTips()));
                        tvDesc.setLineSpacing(0.0f, 1.2f);
                        lvImages.addFooterView(v, null, false);
                    }

                    lvImages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();//需要调用，否则只有HeaderView的时候EmptyView会一直显示
                }
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("图文详情获取失败", "code:" + i, "msg:" + s);
            }
        });
    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.layout_product_detail_image_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String url = images.get(position);
            holder.imageView.setTag(R.id.image_url, url);

            if (!TextUtils.isEmpty(url)) {
                ImageUtils.getInstance().displayOriginal(
                        url, holder.imageView, R.drawable.pic_default_rect_gray, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                ((ImageView) view).setImageResource(R.drawable.pic_default_rect_gray);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                if (s != null && bitmap != null && s.equals(view.getTag(R.id.image_url))) {
                                    ((ImageView) view).setImageBitmap(bitmap);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
            } else {
                holder.imageView.setImageResource(R.drawable.pic_default_rect_gray);
            }

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (lvImages.getHeaderViewsCount() > 0 || lvImages.getFooterViewsCount() > 0) {
                lvImages.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
            } else if (!adapter.isEmpty()) {
                lvImages.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
            }
        }

        class ViewHolder {
            private ImageView imageView;

            public ViewHolder(View convertView) {
                imageView = ViewUtils.findViewById(convertView, R.id.image);
            }
        }
    }
}
