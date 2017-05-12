package com.fynn.oyseckill.app.module.home.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.widget.SeckillCountView;

/**
 * Created by Fynn on 2016/7/7.
 */
public class DetailBottomActionBarFragment extends BaseFragment {

    private TextView tvSeckill;
    private TextView tvActionDesc;
    private SeckillCountView scvNumberChooser;

    @Override
    public int getContentResId() {
        return R.layout.fragment_detail_bottom_actionbar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvSeckill = $(R.id.tv_seckill);
        tvActionDesc = $(R.id.tv_action_desc);
        scvNumberChooser = $(R.id.scv_number_chooser);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {

    }

    public void setOnActionClickListener(View.OnClickListener listener) {
        tvSeckill.setOnClickListener(listener);
    }

    public void setAction(String text, View.OnClickListener listener) {
        tvSeckill.setText(text);
        tvSeckill.setOnClickListener(listener);
    }

    public void setDesc(String text) {
        tvActionDesc.setText(text);
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case HIDE_NUM_PICKER:
                scvNumberChooser.setVisibility(View.GONE);
                tvActionDesc.setText("最近一期正在火热进行中...");
                break;

            case DEFAULT:
            default:
                scvNumberChooser.setVisibility(View.VISIBLE);
                tvActionDesc.setText("参与人次");
                break;
        }
    }

    public SeckillCountView getNumberChooser() {
        return scvNumberChooser;
    }
}
