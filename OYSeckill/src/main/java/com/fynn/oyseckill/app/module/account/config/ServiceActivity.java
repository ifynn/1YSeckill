package com.fynn.oyseckill.app.module.account.config;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.Link;
import com.fynn.oyseckill.model.entity.Assistance;
import com.fynn.oyseckill.model.entity.AssistanceGroup;
import com.fynn.oyseckill.util.view.PrompterUtils;
import com.fynn.oyseckill.web.WebActivity;
import com.fynn.oyseckill.widget.dialog.IPrompter;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.PkgUtils;
import org.appu.common.utils.TextUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/9/23.
 */
public class ServiceActivity extends BaseActivity {

    private LinearLayout llCommonQa;
    private LinearLayout llCategoryQuery;
    private TextView tvService;
    private TextView tvFeedback;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_service;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        llCategoryQuery = $(R.id.ll_category_query);
        llCommonQa = $(R.id.ll_common_qa);
        tvService = $(R.id.tv_service);
        tvFeedback = $(R.id.tv_feedback);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvService, tvFeedback);

        queryHot();
        queryCategory();
    }

    private void queryHot() {
        BmobQuery<Assistance> query = new BmobQuery<Assistance>();
        query.setLimit(100);
        query.findObjects(me, new FindListener<Assistance>() {
            @Override
            public void onSuccess(List<Assistance> list) {
                if (list != null && !list.isEmpty()) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        Assistance a = list.get(i);
                        List<Link> links = a.getLinks();
                        if (links != null) {
                            int linkSize = links.size();
                            for (int j = 0; j < linkSize; j++) {
                                final Link link = links.get(j);
                                if (link != null && link.isHot()) {
                                    View v = LayoutInflater.from(me).inflate(R.layout.layout_service_item, null);
                                    TextView tvName = (TextView) v.findViewById(R.id.tv_name);
                                    tvName.setText(link.getName());
                                    v.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ParamMap<String, Object> params = new ParamMap<String, Object>();
                                            params.put("url", link.getUrl());
                                            params.put("title", link.getName());
                                            startActivity(WebActivity.class, params);
                                        }
                                    });
                                    llCommonQa.addView(v);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("Assistance", "code:" + i, "msg:" + s);
            }
        });
    }

    private void queryCategory() {
        BmobQuery<AssistanceGroup> query = new BmobQuery<AssistanceGroup>();
        query.setLimit(100);
        query.findObjects(me, new FindListener<AssistanceGroup>() {
            @Override
            public void onSuccess(List<AssistanceGroup> list) {
                if (list != null && !list.isEmpty()) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        final AssistanceGroup ag = list.get(i);
                        View v = LayoutInflater.from(me).inflate(R.layout.layout_service_item, null);
                        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
                        tvName.setText(ag.getName());
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ParamMap<String, Object> params = new ParamMap<>();
                                params.put("typeId", ag.getTypeId());
                                params.put("typeName", ag.getName());
                                startActivity(AssistanceDetailActivity.class, params);
                            }
                        });
                        llCategoryQuery.addView(v);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_feedback:
                startActivity(FeedbackActivity.class);
                break;

            case R.id.tv_service:
                TextUtils.copyText("oyseckill");
                PrompterUtils.showConfirm(me,
                        "微信号已复制，是否打开微信并关注公众号客服？",
                        "打开", "取消", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case IPrompter.BUTTON_POSITIVE:
                                        final Intent intent = getPackageManager().
                                                getLaunchIntentForPackage("com.tencent.mm");
                                        if (PkgUtils.isAvailableIntent(intent)) {
                                            startActivity(intent);
                                            showLongToast("点击微信首页上方的搜索按钮，粘贴微信号");
                                        } else {
                                            showShortToast("微信未安装或已被禁用");
                                        }
                                        break;
                                }
                            }
                        });
                break;
        }
    }
}
