package com.fynn.oyseckill.web;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;

import org.appu.common.ParamMap;

/**
 * Created by fynn on 16/6/5.
 */
public class WebActivity extends BaseActivity {

    private WebFragment webFragment;
    private ProgressBar progressBar;

    private String title;

    @Override
    public int getContentResId() {
        return R.layout.activity_web;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        webFragment = (WebFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_web);
        progressBar = $(R.id.progress_bar);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(title)) {
            titlebar.setTitle(title);
        }

        titlebar.setGoBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!webFragment.canGoback()) {
                    finish();
                }
            }
        });

        titlebar.setRightAction("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void handleIntent() {
        ParamMap params = getParams();
        if (params != null) {
            title = (String) params.get("title");
        }
    }

    public void setWebTitle(String title) {
        titlebar.setTitle(title);
    }

    public void setProgressBar(int progress) {
        progressBar.setProgress(progress);
        if (progress != 100) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!webFragment.canGoback()) {
            super.onBackPressed();
        }
    }
}
