package com.fynn.oyseckill.app.module.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.home.detail.ProductDetailActivity;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OrderShare;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.UserPersonTimes;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.CircleImageView;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Fynn on 2016/7/19.
 */
public class OrderShareDetailActivity extends BaseActivity {

    private ListView lvOrderShare;
    private View headerView;
    private CircleImageView civUserProfile;
    private TextView tvUsername;
    private TextView tvIssueNo;
    private TextView tvTime;
    private TextView tvProductName;
    private TextView tvPt;
    private TextView tvLuckNo;
    private TextView tvFinishTime;
    private TextView tvCommentDesc;
    private LinearLayout llProduct;

    private List<String> images;
    private ImageAdapter iAdapter;

    private String objId;
    private String issueObjId;

    private OrderShare orderShare;

    @Override
    public void handleIntent() {
        objId = (String) getParams().get("objId");
        issueObjId = (String) getParams().get("issueObjId");
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_order_share_detail;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        lvOrderShare = $(R.id.lv_order_share);
        headerView = LayoutInflater.from(me).inflate(
                R.layout.layout_order_share_detail_header, null);
        civUserProfile = ViewUtils.findViewById(headerView, R.id.civ_user_profile);
        tvUsername = ViewUtils.findViewById(headerView, R.id.tv_username);
        tvIssueNo = ViewUtils.findViewById(headerView, R.id.tv_issue_no);
        tvTime = ViewUtils.findViewById(headerView, R.id.tv_time);
        tvProductName = ViewUtils.findViewById(headerView, R.id.tv_product_name);
        tvPt = ViewUtils.findViewById(headerView, R.id.tv_pt);
        tvLuckNo = ViewUtils.findViewById(headerView, R.id.tv_luck_no);
        tvFinishTime = ViewUtils.findViewById(headerView, R.id.tv_finish_time);
        tvCommentDesc = ViewUtils.findViewById(headerView, R.id.tv_comment_desc);
        llProduct = ViewUtils.findViewById(headerView, R.id.ll_product);

        images = new ArrayList<String>();
        iAdapter = new ImageAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        lvOrderShare.addHeaderView(headerView, null, false);
        lvOrderShare.setAdapter(iAdapter);

        llProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderShare != null && orderShare.getIssue() != null &&
                        orderShare.getIssue().getIssueNumber() != null) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("issueNo", orderShare.getIssue().getIssueNumber());
                    startActivity(ProductDetailActivity.class, params);
                }
            }
        });

        query();
    }

    private void query() {
        BmobQuery<OrderShare> query = new BmobQuery<OrderShare>();
        query.include("issue,issue.product,user,issue.luckUserPt");
        if (!TextUtils.isEmpty(objId)) {
            query.getObject(me, objId, new GetListener<OrderShare>() {
                @Override
                public void onSuccess(OrderShare orderShare) {
                    if (me.isFinish() || orderShare == null) {
                        return;
                    }

                    OrderShareDetailActivity.this.orderShare = orderShare;
                    refreshView(orderShare);
                }

                @Override
                public void onFailure(int i, String s) {
                    showShortToast("加载失败");
                    LogU.e("晒单详情加载失败", "code:" + i, "msg:" + s);
                }
            });

        } else if (!TextUtils.isEmpty(issueObjId)) {
            query.addWhereEqualTo("issue", new BmobPointer(new Issue(issueObjId)));
            query.findObjects(me, new FindListener<OrderShare>() {
                @Override
                public void onSuccess(List<OrderShare> list) {
                    if (me.isFinish() || list == null || list.isEmpty()) {
                        return;
                    }

                    OrderShareDetailActivity.this.orderShare = list.get(0);
                    refreshView(orderShare);
                }

                @Override
                public void onError(int i, String s) {
                    showShortToast("加载失败");
                    LogU.e("晒单详情加载失败", "code:" + i, "msg:" + s);
                }
            });
        }
    }

    private void refreshView(OrderShare orderShare) {
        OysUser user = orderShare.getUser();
        Issue issue = orderShare.getIssue();
        String desc = orderShare.getDesc();
        List<String> picUrls = orderShare.getPictures();
        String time = orderShare.getCreatedAt();
        BmobFile profile = user.getProfile();
        Product product = issue.getProduct();
        UserPersonTimes upt = issue.getLuckUserPt();

        if (profile == null || TextUtils.isEmpty(profile.getFileUrl(me))) {
            civUserProfile.setImageResource(R.drawable.icon_user_profile_normal);
        } else {
            ImageUtils.getInstance().display(profile.getFileUrl(me), civUserProfile,
                    R.drawable.icon_user_profile_normal);
        }

        tvUsername.setText(UserHelper.getNickname(user));
        tvTime.setText(time);
        tvIssueNo.setText("第" + issue.getIssueNumber() + "期");

        tvProductName.setText(product.getName());
        tvPt.setText("本期参与：" + (upt == null ? "0" : upt.getPersonTimes()) + "人次");
        tvLuckNo.setText("幸运号码：" + issue.getSucceedSeckillNo());
        tvFinishTime.setText("揭晓时间：" + issue.getAnnouncedAt().getDate());

        tvCommentDesc.setText(desc);

        if (picUrls != null && !picUrls.isEmpty()) {
            images.addAll(picUrls);
        }

        iAdapter.notifyDataSetChanged();
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
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(me).inflate(
                        R.layout.layout_order_share_detail_image_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String url = images.get(position);

            if (!TextUtils.isEmpty(url)) {
                ImageUtils.getInstance().displayOriginal(url, holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.pic_default_rect_gray);
            }

            return convertView;
        }

        class ViewHolder {
            private ImageView imageView;

            public ViewHolder(View convertView) {
                imageView = ViewUtils.findViewById(convertView, R.id.image);
            }
        }
    }
}
