package com.fynn.oyseckill.app.module.account.config;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Feedback;
import com.fynn.oyseckill.model.entity.OysInstallation;
import com.fynn.oyseckill.util.BitmapUtils;
import com.fynn.oyseckill.util.ImageFileUtils;
import com.fynn.oyseckill.util.UploadFileHelper;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.data.Storage.Storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fynn on 16/7/24.
 */
public class FeedbackActivity extends BaseActivity {

    private static final int PATH_COUNT = 4;
    private static final String KEY_COMMIT_TIMES = "key.commit.times";
    private LinearLayout llPicture;
    private EditText etContent;
    private EditText etContact;
    private ArrayList<Uri> paths;
    private boolean isLoading;

    @Override
    public int getContentResId() {
        return R.layout.activity_feedback;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        paths = new ArrayList<>(3);

        llPicture = $(R.id.ll_pictures);
        etContent = $(R.id.et_content);
        etContact = $(R.id.et_contact);

        titlebar.setRightActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    return;
                }
                final String content = etContent.getText().toString().trim();
                final String contact = etContact.getText().toString().trim();
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(contact)) {
                    showShortToast("反馈内容或联系方式不能为空");
                } else {
                    isLoading = true;
                    checkLimit(new OnCheckResultListener() {
                        @Override
                        public void onCheckSuccess(int count) {
                            if (count >= 5) {
                                showShortToast("提交频繁");
                                isLoading = false;
                            } else {
                                uploadFiles(content, contact);
                            }
                        }

                        @Override
                        public void onCheckError(int code, String msg) {
                            showShortToast("提交失败");
                            isLoading = false;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        paths.add(null);
        updateImage();
    }

    private void checkLimit(final OnCheckResultListener listener) {
        int cmtTimes = getTodayCmtTimes();

        if (cmtTimes < 5) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            Date sDate = calendar.getTime();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            Date eDate = calendar.getTime();
            BmobQuery<Feedback> query = new BmobQuery<>();
            query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
            query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(sDate));
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(eDate));
            query.count(me, Feedback.class, new CountListener() {
                @Override
                public void onSuccess(int i) {
                    if (listener != null) {
                        listener.onCheckSuccess(i);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    if (listener != null) {
                        listener.onCheckError(i, s);
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.onCheckSuccess(cmtTimes);
            }
        }
    }

    private void uploadFiles(final String content, final String contact) {
        isLoading = true;
        showProgress();

        String[] urls = getUrls();
        if (urls == null || urls.length == 0) {
            Feedback f = new Feedback();
            f.setContact(contact);
            f.setContent(content);
            f.setInstaller(OysInstallation.getCurrentInstallation(me).getInstallationId());
            f.setUser(UserHelper.getUser());
            f.save(me, new SaveListener() {
                @Override
                public void onSuccess() {
                    isLoading = false;
                    hideProgress();
                    showShortToast("提交成功");
                    addTodayCmtTimes(); //今日提交次数增加
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    isLoading = false;
                    hideProgress();
                    showShortToast("提交失败");

                    LogU.e("提交失败", "code:" + i, "msg:" + s);
                }
            });

        } else {
            UploadFileHelper.uploadFiles(urls, new UploadFileHelper.BatchUploadListener() {
                @Override
                public void onSuccess(List<BmobFile> files, List<String> urls) {
                    Feedback f = new Feedback();
                    f.setContact(contact);
                    f.setContent(content);
                    f.setInstaller(OysInstallation.getCurrentInstallation(me).getInstallationId());
                    f.setUser(UserHelper.getUser());
                    f.setPictures(urls);
                    f.save(me, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            isLoading = false;
                            hideProgress();
                            showShortToast("提交成功");
                            addTodayCmtTimes(); //今日提交次数增加
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            isLoading = false;
                            hideProgress();
                            showShortToast("提交失败");

                            LogU.e("提交失败", "code:" + i, "msg:" + s);
                        }
                    });
                }

                @Override
                public void onFailure(int code, String message) {
                    isLoading = false;
                    hideProgress();
                    showShortToast("提交失败");

                    LogU.e("提交失败", "code:" + code, "msg:" + message);
                }
            });
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

        updateImage();
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

    private void updateImage() {
        int size = paths.size();
        llPicture.removeAllViews();
        for (int i = 0; i < size; i++) {
            final int finalI = i;

            View root = LayoutInflater.from(me).inflate(
                    R.layout.layout_upload_image_item, null);
            ImageView ivPic = (ImageView) root.findViewById(R.id.iv_pic);
            ImageView ivDelete = (ImageView) root.findViewById(R.id.iv_delete);

            Uri uri = paths.get(i);
            if (uri == null) {
                ivDelete.setVisibility(View.GONE);
                ivPic.setImageResource(R.drawable.icon_plus_normal);
                int pxv = DensityUtils.dip2px(10);
                ivPic.setPadding(pxv, pxv, pxv, pxv);
            } else {
                ivDelete.setVisibility(View.VISIBLE);
                Bitmap bm = BitmapUtils.uriToBitmap(uri);
                ivPic.setImageBitmap(bm);
                int pxv = DensityUtils.dip2px(0);
                ivPic.setPadding(pxv, pxv, pxv, pxv);

                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paths.remove(finalI);
                        onDataChange();
                    }
                });
            }

            ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageFileUtils.openImageChooser(me, finalI);
                }
            });

            llPicture.addView(root);
        }
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

    private int getTodayCmtTimes() {
        int cmtTimes = 0;
        String timesPattern = Storage.getString(KEY_COMMIT_TIMES, "");
        if (!TextUtils.isEmpty(timesPattern)) {
            String day = timesPattern.substring(0, timesPattern.indexOf("_"));
            String times = timesPattern.substring(timesPattern.indexOf("_") + 1);
            String date = DateTimeUtils.formatDate(new Date(), "yyyyMMdd");
            if (day.equals(date)) {
                try {
                    cmtTimes = Integer.valueOf(times);
                } catch (NumberFormatException e) {
                    LogU.w(e);
                }
            }
        }

        return cmtTimes;
    }

    private void addTodayCmtTimes() {
        int t = getTodayCmtTimes();
        String date = DateTimeUtils.formatDate(new Date(), "yyyyMMdd");
        Storage.put(KEY_COMMIT_TIMES, date + "_" + ++t);
    }

    interface OnCheckResultListener {
        void onCheckSuccess(int count);

        void onCheckError(int code, String msg);
    }
}
