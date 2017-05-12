package com.fynn.oyseckill.app.module.home.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Seckill;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.ParamMap;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Fynn on 2016/8/13.
 */
public class CompNumDetailActivity extends BaseActivity {

    private TextView tvNumA;
    private TextView tvExpand;
    private TextView tvNumB;
    private TextView tvLotteryNo;
    private TextView tvLuckNo;
    private LinearLayout llNumADetail;
    private LinearLayout llNumAList;

    private String issueObjId;

    private boolean isLoading;

    @Override
    public void handleIntent() {
        ParamMap params = getParams();
        if (params != null) {
            issueObjId = String.valueOf(params.get("issueObjId"));
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_comp_num_detail;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvNumA = $(R.id.tv_num_a);
        tvExpand = $(R.id.tv_expand);
        tvNumB = $(R.id.tv_num_b);
        tvLotteryNo = $(R.id.tv_lottery_no);
        tvLuckNo = $(R.id.tv_luck_no);
        llNumADetail = $(R.id.ll_num_a_detail);
        llNumAList = $(R.id.ll_num_a_list);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvExpand);

        if (!TextUtils.isEmpty(issueObjId)) {
            queryData();
        }
    }

    private void queryData() {
        isLoading = true;

        BmobQuery<Issue> query = new BmobQuery<>();
        query.getObject(me, issueObjId, new GetListener<Issue>() {
            @Override
            public void onSuccess(Issue issue) {
                querySeckill(issue);
            }

            @Override
            public void onFailure(int i, String s) {
                isLoading = false;
            }
        });
    }

    private void querySeckill(final Issue issue) {
        BmobQuery<Seckill> query = new BmobQuery<>();
        query.include("user");
        query.order("-createdAt");
        query.setLimit(50);
        query.addWhereEqualTo("issue", new BmobPointer(issue));
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                updateView(issue, list);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void updateView(Issue issue, List<Seckill> list) {
        //数值A
        long numA = compNumA(list);
        tvNumA.setText(String.valueOf(numA));

        //数值A detail
        int size = list.size();
        for (int i = 0; i < size; i++) {
            View item = LayoutInflater.from(me).inflate(R.layout.layout_comp_num_item, null);
            TextView tvTime = ViewUtils.findViewById(item, R.id.tv_time);
            TextView tvTimeRst = ViewUtils.findViewById(item, R.id.tv_time_rst);
            TextView tvUser = ViewUtils.findViewById(item, R.id.tv_user);

            Seckill seckill = list.get(i);
            tvTime.setText(DateTimeUtils.formatDate(seckill.getSeckillAt(),
                    "yyyy-MM-dd HH:mm:ss.SSS"));
            tvTimeRst.setText(String.valueOf(DateTimeUtils.formatDateToLong(seckill.getSeckillAt())));
            tvUser.setText(UserHelper.getNickname(seckill.getUser()));

            llNumAList.addView(item);
        }

        //数值B
        String on = issue.getOtherNo();
        if (!TextUtils.isEmpty(on)) {
            tvNumB.setText(on);
        } else {
            tvNumB.setText("等待揭晓...");
        }

        long dateMill = BmobDate.getTimeStamp(issue.getFinishedAt().getDate());
        String finishDate = DateTimeUtils.formatDate(dateMill, "yyyyMMdd");

        String numAStr = String.valueOf(numA);
        String numASuffix = "0" + numAStr.substring(numAStr.length() - 2);

        String lottNo = finishDate + numASuffix;
        tvLotteryNo.setText("(第" + lottNo + "期)");

        //幸运号码计算结果
        Long l = issue.getSucceedSeckillNo();
        if (l != null) {
            tvLuckNo.setText(l.toString());
        } else {
            tvLuckNo.setText("即将揭晓...");
        }
    }

    private long compNumA(List<Seckill> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        long numA = 0;
        Iterator<Seckill> iterator = list.iterator();
        while (iterator.hasNext()) {
            Seckill seckill = iterator.next();
            Long sa = seckill.getSeckillAt();
            if (sa != null) {
                long num = DateTimeUtils.formatDateToLong(sa);
                numA += num;
            }
        }

        return numA;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_expand:
                if (llNumADetail.isShown()) {
                    llNumADetail.setVisibility(View.GONE);
                    tvExpand.setText("展开");
                } else {
                    llNumADetail.setVisibility(View.VISIBLE);
                    tvExpand.setText("收起");
                }
                break;
        }
    }
}
