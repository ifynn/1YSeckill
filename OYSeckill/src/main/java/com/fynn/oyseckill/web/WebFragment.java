package com.fynn.oyseckill.web;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.core.BaseFragment;

import org.appu.AppU;
import org.appu.common.ParamMap;

/**
 * Created by Fynn on 2016/6/7.
 */
public class WebFragment extends BaseFragment {

    private String title;
    private String url;

    private LinearLayout llAdrBar;
    private EditText etAdr;
    private ImageView ivClear;
    private WebView webView;

    @Override
    public int getContentResId() {
        return R.layout.fragment_web;
    }

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            title = (String) params.get("title");
            url = (String) params.get("url");
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        llAdrBar = $(R.id.ll_adr_bar);
        etAdr = $(R.id.et_adr);
        ivClear = $(R.id.iv_clear);
        webView = $(R.id.web_view);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        enableDebugBar();
        applyWebSettings();

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!TextUtils.isEmpty(title)) {
                    Activity activity = getActivity();
                    if (activity instanceof WebActivity) {
                        ((WebActivity) activity).setWebTitle(title);
                    }
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Activity activity = getActivity();
                if (activity instanceof WebActivity) {
                    ((WebActivity) activity).setProgressBar(newProgress);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                etAdr.setText(url);
                etAdr.clearFocus();
                return false;
            }
        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAdr.setText("");
            }
        });

        webView.loadUrl(url);
        etAdr.setText(url);
        etAdr.clearFocus();
    }

    private void applyWebSettings() {
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
    }

    private void enableDebugBar() {
        if (AppU.isDebug()) {
            llAdrBar.setVisibility(View.VISIBLE);
            BaseActivity activity = (BaseActivity) getActivity();
            llAdrBar.setBackgroundColor(activity.getTitlebar().getBackgroundColor());
            etAdr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                        case EditorInfo.IME_ACTION_GO:
                            String url = etAdr.getText().toString();
                            if (!url.startsWith("http")) {
                                url = "http://" + url;
                            }
                            webView.loadUrl(url);
                            etAdr.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getActivity()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(etAdr.getWindowToken(), 0);
                            }
                            return true;
                    }
                    return false;
                }
            });
        } else {
            llAdrBar.setVisibility(View.GONE);
        }
    }

    public boolean canGoback() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return false;
    }
}
