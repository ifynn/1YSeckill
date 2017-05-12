package com.fynn.oyseckill.app.module.home.card;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.app.module.account.pay.PayConfirmActivity;
import com.fynn.oyseckill.app.module.home.detail.ProductDetailActivity;
import com.fynn.oyseckill.app.module.home.util.ProductUtils;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.WrapGridView;
import com.fynn.oyseckill.widget.dialog.SeckillCountPicker;

import org.appu.common.ParamMap;
import org.appu.common.utils.NetUtils;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Fynn on 2016/4/26.
 */
public class HomeProductView extends LinearLayout {

    private WrapGridView gridView;
    private TextView tvRefresh;
    private TextView tvDesc;
    private ImageView ivState;

    private List<Product> products;
    private HomeProductAdapter adapter;
    private BaseActivity activity;

    public HomeProductView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }
        LayoutInflater.from(context).inflate(R.layout.view_home_card_product, this);

        gridView = (WrapGridView) findViewById(R.id.wrap_grid_view);
        tvRefresh = (TextView) findViewById(R.id.tv_refresh);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        ivState = (ImageView) findViewById(R.id.iv_state);

        adapter = new HomeProductAdapter();
        products = new ArrayList<Product>();

        gridView.setFocusable(false);
        gridView.setAdapter(adapter);
        gridView.setEmptyView(ViewUtils.findViewById(this, R.id.ll_empty));
    }

    public WrapGridView getView() {
        return gridView;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        setEmpty();
    }

    public void setEmpty() {
        if (products.size() <= 0) {
            ivState.setVisibility(VISIBLE);
            tvDesc.setVisibility(VISIBLE);
            if (!NetUtils.isNetworkAvailable()) {
                ivState.setImageResource(R.drawable.icon_empty_logo_fly);
                tvRefresh.setVisibility(VISIBLE);
                tvDesc.setText("嗖，网络飞走了~");
            } else {
                ivState.setImageResource(R.drawable.icon_empty_logo_profile);
                tvRefresh.setVisibility(GONE);
                tvDesc.setText("商品空空如也~");
            }
        }
    }

    public TextView getRefreshButton() {
        return tvRefresh;
    }

    private void payPicker(int maxValue, int minValue, int value, final Product product) {
        new SeckillCountPicker.Builder(getContext())
                .setMaxValue(maxValue)
                .setMinValue(minValue)
                .setValue(value)
                .setOnSeckillClickListener(new SeckillCountPicker.OnSeckillClickListener() {
                    @Override
                    public void onSeckillClick(SeckillCountPicker picker, int value) {
                        picker.dismiss();
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("amount", value);
                        params.put("picUrl", product.getImage());
                        params.put("productName", product.getName());
                        params.put("proObjId", product.getObjectId());
                        activity.startActivity(PayConfirmActivity.class, params);
                    }
                })
                .show();
    }

    private void queryData(String proObjId) {
        activity.showProgress();
        BmobQuery<Product> query = new BmobQuery<>();
        query.include("currentIssue");
        query.getObject(activity, proObjId, new GetListener<Product>() {
            @Override
            public void onSuccess(Product product) {
                activity.hideProgress();
                if (product != null) {
                    Boolean canBuy = product.getCanBuy();
                    if (canBuy == null || canBuy) {
                        Issue issue = product.getCurrentIssue();
                        if (issue == null) {
                            activity.showShortToast("该商品已下架");
                        } else {
                            Double price = product.getPrice() == null ? 0 : product.getPrice();
                            Long pt = issue.getPersonTimes() == null ? 0 : issue.getPersonTimes();
                            Double restD = price - pt;
                            int rest = restD.intValue();
                            if (rest <= 0) {
                                activity.showShortToast("本期已结束");
                            } else {
                                payPicker(rest, 1, rest > 10 ? 10 : rest, product);
                            }
                        }
                    } else {
                        activity.showShortToast("该商品已下架");
                    }
                } else {
                    activity.showShortToast("出现错误");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                activity.hideProgress();
                activity.showShortToast("出现错误");
            }
        });
    }

    class HomeProductAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public HomeProductAdapter() {
            mInflater = LayoutInflater.from(HomeProductView.this.getContext());
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mInflater.inflate(R.layout.layout_home_product_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Product product = products.get(position);

            //设置商品标题
            holder.tvName.setText(product.getName());

            final Issue issue = product.getCurrentIssue();

            if (issue == null) {
                holder.tvProgress.setText("0%");
                holder.pbProgress.setProgress(0);
                holder.ivPic.setImageDrawable(null);
                holder.tvSeckill.setOnClickListener(null);
                convertView.setOnClickListener(null);
                return convertView;
            }

            int crtProgress = ProductUtils.getProgressInHundred(
                    product.getPrice() == null ? 0 : product.getPrice(),
                    issue.getPersonTimes() == null ? 0 : issue.getPersonTimes());

            //设置商品进度文描
            holder.tvProgress.setText(String.valueOf(crtProgress) + "%");

            //设置进度
            holder.pbProgress.setProgress(crtProgress);

            //加载图片
            String url = product.getImage();
            if (!TextUtils.isEmpty(url)) {
                ImageUtils.getInstance().display(url, holder.ivPic, R.drawable.pic_default_square_white);
            } else {
                holder.ivPic.setImageResource(R.drawable.pic_default_square_white);
            }

            holder.tvSeckill.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        queryData(product.getObjectId());

                    } else {
                        activity.startActivity(LoginActivity.class);
                    }
                }
            });

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueNo", issue.getIssueNumber());
                    activity.startActivity(ProductDetailActivity.class, params);
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        private ImageView ivPic;
        private TextView tvName;
        private TextView tvProgress;
        private ProgressBar pbProgress;
        private TextView tvSeckill;

        public ViewHolder(View view) {
            ivPic = ViewUtils.findViewById(view, R.id.iv_pic);
            tvName = ViewUtils.findViewById(view, R.id.tv_name);
            tvProgress = ViewUtils.findViewById(view, R.id.tv_progress);
            pbProgress = ViewUtils.findViewById(view, R.id.pb_progress);
            tvSeckill = ViewUtils.findViewById(view, R.id.tv_seckill);

        }
    }
}
