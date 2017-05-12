package com.fynn.oyseckill.app.module.account.config;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.QA;
import com.fynn.oyseckill.widget.MyExpandableListView;

import org.appu.common.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/7/22.
 */
public class QaActivity extends BaseActivity {

    private List<QA> qas;
    private QaAdapter qaAdapter;

    private MyExpandableListView elvQa;
    private SwipeRefreshLayout refreshView;
    private TextView tvEmpty;

    private boolean isLoading;

    @Override
    public int getContentResId() {
        return R.layout.activity_qa;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        qas = new ArrayList<>();
        qaAdapter = new QaAdapter(qas);

        elvQa = $(R.id.elv_qa);
        refreshView = $(R.id.refresh_view);
        tvEmpty = $(R.id.tv_empty);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        elvQa.setAdapter(qaAdapter);
        refreshView.setColorSchemeColors(Color.parseColor("#f85757"));
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                query(true, false);
            }
        });

        query(false, true);
    }

    private void query(boolean online, boolean showDialog) {
        isLoading = true;
        if (showDialog) {
            showProgress();
        }

        BmobQuery<QA> query = new BmobQuery<>();
        query.addWhereEqualTo("isVisibleToUser", true);
        query.order("qaId");
        query.setLimit(100);
        query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(2));
        if (!online) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }
        query.findObjects(this, new FindListener<QA>() {
            @Override
            public void onSuccess(List<QA> list) {
                isLoading = false;
                hideProgress();
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }

                qas.clear();
                if (list != null && !list.isEmpty()) {
                    qas.addAll(list);
                }
                qaAdapter.notifyDataSetChanged();
                updateEmptyView();
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                hideProgress();
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
            }
        });
    }

    private void updateEmptyView() {
        if (qaAdapter.getGroupCount() <= 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    class QaAdapter extends BaseExpandableListAdapter {

        private List<QA> qas;

        public QaAdapter(List<QA> qas) {
            this.qas = qas;
        }

        @Override
        public int getGroupCount() {
            return qas.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return qas.get(groupPosition).getQuestion();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return qas.get(groupPosition).getAnswer();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(me);
            int pxv = DensityUtils.dip2px(10);
            textView.setPadding(pxv, pxv, pxv, pxv);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.parseColor("#4F4F4F"));
            textView.setTextSize(15);
            textView.setText((groupPosition + 1) + "." + getGroup(groupPosition).toString());
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = new TextView(me);
            int pxv = DensityUtils.dip2px(10);
            textView.setPadding(pxv, pxv, pxv, pxv);
            textView.setBackgroundColor(Color.parseColor("#F0F0F0"));
            textView.setTextColor(Color.parseColor("#828282"));
            textView.setTextSize(13);
            textView.setLineSpacing(0.0f, 1.2f);
            textView.setText(Html.fromHtml(getChild(groupPosition, childPosition).toString()));
            return textView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
