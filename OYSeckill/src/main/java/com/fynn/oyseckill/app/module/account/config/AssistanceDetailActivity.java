package com.fynn.oyseckill.app.module.account.config;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.Link;
import com.fynn.oyseckill.model.entity.Assistance;
import com.fynn.oyseckill.web.WebActivity;
import com.fynn.oyseckill.widget.MyExpandableListView;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 2016/9/6.
 */
public class AssistanceDetailActivity extends BaseActivity {

    private List<Assistance> assistanceList;
    private AAdapter aAdapter;

    private MyExpandableListView elvQa;
    private SwipeRefreshLayout refreshView;

    private boolean isLoading;
    private int typeId = -1;
    private String typeName;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            Object o = params.get("typeId");
            if (o != null) {
                try {
                    typeId = Integer.valueOf(String.valueOf(o));
                } catch (Exception e) {
                    LogU.e(e);
                }
            }

            Object tn = params.get("typeName");
            typeName = String.valueOf(tn);
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_assistance_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        assistanceList = new ArrayList<>();
        aAdapter = new AAdapter(assistanceList);

        elvQa = $(R.id.elv_qa);
        refreshView = $(R.id.refresh_view);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        titlebar.setTitle(typeName);

        elvQa.setAdapter(aAdapter);
        refreshView.setColorSchemeColors(Color.parseColor("#f85757"));
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                query();
            }
        });

        query();
    }

    private void query() {
        isLoading = true;

        BmobQuery<Assistance> query = new BmobQuery<>();
        query.setLimit(100);
        query.order("id");
        query.addWhereEqualTo("groupTypeId", typeId);
        query.findObjects(this, new FindListener<Assistance>() {
            @Override
            public void onSuccess(List<Assistance> list) {
                isLoading = false;
                hideProgress();
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }

                assistanceList.clear();
                if (list != null && !list.isEmpty()) {
                    assistanceList.addAll(list);
                }
                aAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
            }
        });
    }

    class AAdapter extends BaseExpandableListAdapter {

        private List<Assistance> assistanceList;

        public AAdapter(List<Assistance> assistanceList) {
            this.assistanceList = assistanceList;
        }

        @Override
        public int getGroupCount() {
            return assistanceList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<Link> links = assistanceList.get(groupPosition).getLinks();
            if (links != null && !links.isEmpty()) {
                return links.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return assistanceList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            List<Link> links = assistanceList.get(groupPosition).getLinks();
            if (links != null && !links.isEmpty()) {
                return links.get(childPosition);
            } else {
                return null;
            }
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
            GroupViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(me).inflate(
                        R.layout.layout_assistance_item, null);

                holder = new GroupViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            Assistance assistance = (Assistance) getGroup(groupPosition);

            if (isExpanded) {
                holder.ivIndicator.setImageResource(R.drawable.icon_arrow_down_small_black);
            } else {
                holder.ivIndicator.setImageResource(R.drawable.icon_arrow_right_small_black);
            }

            holder.tvName.setText(assistance.getName());

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(me).inflate(
                        R.layout.layout_assistance_item, null);
                holder = new ChildViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            final Link link = (Link) getChild(groupPosition, childPosition);
            if (link == null) {
                holder.tvName.setText("");
            } else {
                holder.tvName.setText(link.getName());
            }

            final String url = link.getUrl();
            if (!TextUtils.isEmpty(url)) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("url", url);
                        params.put("title", link.getName());
                        startActivity(WebActivity.class, params);
                    }
                });
            } else {
                convertView.setOnClickListener(null);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class GroupViewHolder {
            private TextView tvName;
            private ImageView ivIndicator;

            public GroupViewHolder(View convertView) {
                tvName = ViewUtils.findViewById(convertView, R.id.tv_name);
                ivIndicator = ViewUtils.findViewById(convertView, R.id.iv_indicator);
            }
        }

        class ChildViewHolder {
            private TextView tvName;
            private ImageView ivIndicator;
            private View divider;

            public ChildViewHolder(View convertView) {
                tvName = ViewUtils.findViewById(convertView, R.id.tv_name);
                ivIndicator = ViewUtils.findViewById(convertView, R.id.iv_indicator);
                divider = ViewUtils.findViewById(convertView, R.id.divider);

                tvName.setTextColor(0xff707070);
                tvName.setTextSize(13);
                ivIndicator.setImageResource(R.drawable.icon_arrow_right_small_gray);
                divider.setVisibility(View.GONE);
            }
        }
    }
}
