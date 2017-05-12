package com.fynn.oyseckill.app.module.account.user;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Seckill;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.ParamMap;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fynn on 16/7/31.
 */
public class SeckillNoDetailActivity extends BaseActivity {

    private ExpandableListView elvSeckillNo;
    private View headView;
    private TextView tvProductName;
    private TextView tvIssueNo;
    private TextView tvSeckillTitle;

    private List<Seckill> seckills;
    private SeckillAdapter sAdapter;

    private String issueObjId;
    private String productName;
    private String issueNo;
    private String pts;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            issueObjId = (String) params.get("issueObjId");
            productName = String.valueOf(params.get("productName"));
            issueNo = String.valueOf(params.get("issueNo"));
            pts = String.valueOf(params.get("pts"));
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_all_seckill_no;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        seckills = new ArrayList<>();
        sAdapter = new SeckillAdapter();

        elvSeckillNo = $(R.id.elv_seckill_no);
        headView = LayoutInflater.from(me).inflate(R.layout.layout_seckill_no_detail_top, null);
        tvProductName = ViewUtils.findViewById(headView, R.id.tv_product_name);
        tvIssueNo = ViewUtils.findViewById(headView, R.id.tv_issue_no);
        tvSeckillTitle = ViewUtils.findViewById(headView, R.id.tv_seckill_title);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        elvSeckillNo.addHeaderView(headView, null, false);
        elvSeckillNo.setAdapter(sAdapter);

        tvProductName.setText(productName);
        tvIssueNo.setText("期号：" + issueNo);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("您已参与")
                .append(TextStyleUtils.genColorText(pts, Color.parseColor("#F85757")))
                .append("人次，以下是所有参与记录");

        tvSeckillTitle.setText(ssb);

        fetchData();
    }

    private void fetchData() {
        BmobQuery<Seckill> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.addWhereEqualTo("issue", new BmobPointer(new Issue(issueObjId)));
        query.include("issue");
        query.order("-createdAt");
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                if (list != null && !list.isEmpty()) {
                    seckills.addAll(list);
                    sAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    class SeckillAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return seckills.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return seckills.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return seckills.get(groupPosition).getSeckillNo();
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
                        R.layout.layout_all_seckill_no_item, null);

                holder = new GroupViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            Seckill seckill = (Seckill) getGroup(groupPosition);

            holder.tvTime.setText(DateTimeUtils.formatDate(seckill.getSeckillAt(),
                    "yyyy-MM-dd HH:mm:ss.SSS"));

            SpannableString pt = TextStyleUtils.genColorText(
                    seckill.getPersonTimes() + "",
                    Color.parseColor("#F85757"));
            holder.tvPt.setText(new SpannableStringBuilder().append(pt).append("人次"));

            if (isExpanded) {
                holder.ivIndicator.setImageResource(R.drawable.icon_arrow_down_small_gray);
            } else {
                holder.ivIndicator.setImageResource(R.drawable.icon_arrow_right_small_gray);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null) {
                TextView textView = new TextView(me);
                int pxv = DensityUtils.dip2px(10);
                textView.setPadding(pxv, pxv, pxv, pxv);
                textView.setBackgroundColor(Color.parseColor("#F5F5F5"));
                textView.setTextColor(Color.parseColor("#4F4F4F"));
                textView.setTextSize(13);
                textView.setLineSpacing(0.0f, 1.2f);

                convertView = textView;
                holder = new ChildViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            List<Long> seckillNos = (List<Long>) getChild(groupPosition, childPosition);
            Seckill seckill = (Seckill) getGroup(groupPosition);
            int size = seckillNos.size();
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            Long luckNo = seckill.getIssue().getSucceedSeckillNo();

            for (int i = 0; i < size; i++) {
                Long sn = seckillNos.get(i);
                if (sn != null) {
                    if (sn.equals(luckNo)) {
                        SpannableString ss = TextStyleUtils.genColorText(
                                sn.toString(), Color.parseColor("#F85757"));
                        ssb.append(ss);

                    } else {
                        ssb.append(sn.toString());
                    }

                    if (i != size - 1) {
                        ssb.append("  ");
                    }
                }
            }

            holder.tvSeckillNo.setText(ssb);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        class GroupViewHolder {
            private TextView tvTime;
            private TextView tvPt;
            private ImageView ivIndicator;

            public GroupViewHolder(View convertView) {
                tvTime = ViewUtils.findViewById(convertView, R.id.tv_time);
                tvPt = ViewUtils.findViewById(convertView, R.id.tv_pt);
                ivIndicator = ViewUtils.findViewById(convertView, R.id.iv_indicator);
            }
        }

        class ChildViewHolder {
            private TextView tvSeckillNo;

            public ChildViewHolder(View convertView) {
                tvSeckillNo = (TextView) convertView;
            }
        }
    }
}
