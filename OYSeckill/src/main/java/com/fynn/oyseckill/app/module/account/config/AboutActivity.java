package com.fynn.oyseckill.app.module.account.config;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Agreement;
import com.fynn.oyseckill.web.WebActivity;

import org.appu.common.ParamMap;
import org.appu.common.utils.PkgUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ELSE_NETWORK;

/**
 * Created by Fynn on 2016/7/20.
 */
public class AboutActivity extends BaseActivity {

    private TextView tvVersion;
    private TextView tvAgreement;

    private boolean isLoading;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvVersion = $(R.id.tv_version_name);
        tvAgreement = $(R.id.tv_agreement);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        tvVersion.setText("v" + PkgUtils.getVersionName());
        setOnClick(tvAgreement);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_agreement:
                if (!isLoading) {
                    queryAgreement();
                }
                break;
        }
    }

    private void queryAgreement() {
        isLoading = true;
        BmobQuery<Agreement> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(1);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(30)); //30 days
        query.setCachePolicy(CACHE_ELSE_NETWORK);
        query.findObjects(me, new FindListener<Agreement>() {
            @Override
            public void onSuccess(List<Agreement> list) {
                isLoading = false;

                if (list != null && !list.isEmpty()) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("url", list.get(0).getUrl());
                    params.put("title", "用户协议");
                    startActivity(WebActivity.class, params);
                } else {
                    showShortToast("error");
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;

                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("无网络连接");
                        break;
                }
            }
        });
    }
}
