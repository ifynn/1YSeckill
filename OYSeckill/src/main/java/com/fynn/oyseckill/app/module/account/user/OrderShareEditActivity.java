package com.fynn.oyseckill.app.module.account.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.OrderState;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.LuckOrder;
import com.fynn.oyseckill.model.entity.OrderShare;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.util.BitmapUtils;
import com.fynn.oyseckill.util.ImageFileUtils;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UploadFileHelper;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.common.AppHelper;
import org.appu.common.ParamMap;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by fynn on 16/7/31.
 */
public class OrderShareEditActivity extends BaseActivity {

    private static final int PATH_COUNT = 8;
    private GridView gvPhoto;
    private ImageView ivPic;
    private TextView tvProductName;
    private TextView tvIssueNo;
    private EditText etComment;
    private List<Uri> paths;
    private UploadImageAdapter uiAdapter;
    private String picUrl;
    private String productName;
    private String issueNo;
    private String productObjId;
    private String issueObjId;
    private boolean isVirtual;
    private String orderObjId;

    private boolean isLoading;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            Object pu = params.get("picUrl");
            if (pu != null) {
                picUrl = String.valueOf(pu);
            }

            productName = String.valueOf(params.get("productName"));
            issueNo = "期号：" + String.valueOf(params.get("issueNo"));
            productObjId = String.valueOf(params.get("productObjId"));
            issueObjId = String.valueOf(params.get("issueObjId"));
            orderObjId = String.valueOf(params.get("orderObjId"));

            Object oiv = params.get("isVirtual");
            if (oiv != null) {
                isVirtual = (boolean) oiv;
            }
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_order_share_edit;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        paths = new ArrayList<>();
        uiAdapter = new UploadImageAdapter();

        gvPhoto = $(R.id.gv_photo);
        ivPic = $(R.id.iv_pic);
        tvProductName = $(R.id.tv_product_name);
        tvIssueNo = $(R.id.tv_issue_no);
        etComment = $(R.id.et_comment);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        titlebar.setRightActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    share();
                }
            }
        });

        paths.add(null);
        gvPhoto.setAdapter(uiAdapter);

        if (!TextUtils.isEmpty(picUrl)) {
            ImageUtils.getInstance().display(picUrl, ivPic, R.drawable.pic_default_square_gray);
        } else {
            ivPic.setImageResource(R.drawable.pic_default_square_gray);
        }

        tvIssueNo.setText(issueNo);
        tvProductName.setText(productName);
    }

    private void share() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        showProgress();

        String[] urls = getUrls();
        if (urls == null || urls.length <= 0) {
            saveComment(null);
        } else {
            uploadPicture(urls);
        }
    }

    private void uploadPicture(String[] urls) {
        UploadFileHelper.uploadFiles(urls, new UploadFileHelper.BatchUploadListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                saveComment(urls);
            }

            @Override
            public void onFailure(int code, String message) {
                isLoading = false;
                hideProgress();
                showShortToast("图片上传失败");

                LogU.e("图片上传失败", "code:" + code, "msg:" + message);
            }
        });
    }

    private void saveComment(List<String> urls) {
        String comment = etComment.getText().toString().trim();
        OrderShare orderShare = new OrderShare();
        orderShare.setAccess(-1);
        orderShare.setDesc(comment);
        orderShare.setIssue(new Issue(issueObjId));
        orderShare.setUser(UserHelper.getUser());
        orderShare.setProduct(new Product(productObjId));
        if (urls != null && !urls.isEmpty()) {
            orderShare.setPictures(urls);
        }
        orderShare.save(me, new SaveListener() {
            @Override
            public void onSuccess() {
                isLoading = false;
                hideProgress();

                LuckOrder luckOrder = new LuckOrder();
                luckOrder.setObjectId(orderObjId);
                luckOrder.setState(OrderState.STATE_SHARED);
                luckOrder.update(me, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        LogU.e("更新订单成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogU.e("更新订单失败", "code:" + i, "msg:" + s);
                    }
                });

                showShortToast("分享成功，审核通过后即可展示");
                AppHelper.sendLocalEvent(Event.EVENT_ORDER_SHARED);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                isLoading = false;
                hideProgress();

                switch (i) {
                    case 401:
                        showShortToast("已经分享过了");
                        break;

                    default:
                        showShortToast("分享失败");
                        break;
                }


                LogU.e("提交失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private boolean checkInput() {
        String comment = etComment.getText().toString().trim();
        String[] urls = getUrls();

        if (TextUtils.isEmpty(comment)) {
            showShortToast("请输入评论内容");
            return false;
        }

        if (urls == null || urls.length <= 0) {
            if (!isVirtual) {
                showShortToast("至少选择一张图片");
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri u = data.getData();
            if (paths.contains(u)) {
                showShortToast("不能重复选择");
                return;
            }

            paths.set(requestCode, u);
            onDataChange();
        }
    }

    private void onDataChange() {
        int size = paths.size();
        if (size <= 0) {
            paths.add(null);
        } else if (size < PATH_COUNT) {
            if (paths.get(paths.size() - 1) != null) {
                paths.add(null);
            }
        }

        uiAdapter.notifyDataSetChanged();
    }

    private String[] getUrls() {
        int size = paths.size();
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Uri uri = paths.get(i);
            if (uri != null) {
                urls.add(ImageFileUtils.getRealFilePath(uri));
            }
        }

        int l = urls.size();
        String[] us = new String[l];
        for (int j = 0; j < l; j++) {
            us[j] = urls.get(j);
        }

        return us;
    }

    class UploadImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public Object getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(me).inflate(R.layout.layout_upload_image_item, null);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Uri uri = (Uri) getItem(position);

            if (uri == null) {
                holder.ivDelete.setVisibility(View.GONE);
                holder.ivPic.setImageResource(R.drawable.icon_plus_normal);
                int pxv = DensityUtils.dip2px(10);
                holder.ivPic.setPadding(pxv, pxv, pxv, pxv);
            } else {
                holder.ivDelete.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapUtils.uriToBitmap(uri);
                holder.ivPic.setImageBitmap(bitmap);

                int pxv = DensityUtils.dip2px(0);
                holder.ivPic.setPadding(pxv, pxv, pxv, pxv);

                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paths.remove(position);
                        onDataChange();
                    }
                });
            }

            holder.ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageFileUtils.openImageChooser(me, position);
                }
            });

            return convertView;
        }

        class ViewHolder {

            private ImageView ivPic;
            private ImageView ivDelete;

            public ViewHolder(View convertView) {
                ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            }
        }
    }
}
