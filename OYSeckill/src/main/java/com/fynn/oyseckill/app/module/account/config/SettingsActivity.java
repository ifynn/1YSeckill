package com.fynn.oyseckill.app.module.account.config;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.util.FileUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Prompter;
import com.fynn.oyseckill.widget.dialog.Sheet;
import com.fynn.oyseckill.widget.dialog.SheetItem;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.PkgUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.AppVersion;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/**
 * Created by fynn on 16/6/10.
 */
public class SettingsActivity extends BaseActivity {

    private static final int MESSAGE_CACHE_SIZE = 0x01;
    private static final int MESSAGE_CLEAR_CACHE = 0x02;
    private TextView btnLogout;
    private LinearLayout llQaa;
    private LinearLayout llClearCache;
    private LinearLayout llFeedback;
    private LinearLayout llAbout;
    private LinearLayout llCheckUpgrade;
    private LinearLayout llPayHelp;
    private TextView tvCacheSize;
    private TextView tvVersionName;
    private ProgressBar pbCheckingUpgrade;
    private TextView tvNewVersionDesc;
    private boolean isCheckingUpgrade;

    @Override
    public int getContentResId() {
        return R.layout.activity_settings;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        btnLogout = $(R.id.btn_logout);
        llQaa = $(R.id.ll_qaa);
        llClearCache = $(R.id.ll_clear_cache);
        llFeedback = $(R.id.ll_feedback);
        llAbout = $(R.id.ll_about);
        llCheckUpgrade = $(R.id.ll_check_upgrade);
        tvCacheSize = $(R.id.tv_cache_size);
        tvVersionName = $(R.id.tv_version_name);
        pbCheckingUpgrade = $(R.id.pb_checking_upgrade);
        tvNewVersionDesc = $(R.id.tv_new_version_desc);
        llPayHelp = $(R.id.ll_pay_help);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(btnLogout, llQaa, llClearCache, llFeedback, llAbout, llCheckUpgrade, llPayHelp);
        tvVersionName.setText("V" + PkgUtils.getVersionName());
        getCacheFormatSize();
        checkUpgrade();
    }

    private void checkUpgrade() {
        BmobQuery<AppVersion> query = new BmobQuery<>();
        query.setLimit(1);
        query.order("-version_i");
        query.findObjects(me, new FindListener<AppVersion>() {
            @Override
            public void onSuccess(List<AppVersion> list) {
                if (list != null && !list.isEmpty()) {
                    Integer verCode = list.get(0).getVersion_i();
                    if (verCode != null) {
                        if (verCode > PkgUtils.getVersionCode()) {
                            tvNewVersionDesc.setVisibility(View.VISIBLE);
                        }
                    }

                    LogU.w("有新版本",
                            "version name:" + list.get(0).getVersion(),
                            "version code:" + verCode);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_CACHE_SIZE:
                tvCacheSize.setText(msg.obj.toString());
                break;

            case MESSAGE_CLEAR_CACHE:
                hideProgress();
                showShortToast("缓存清除成功");
                getCacheFormatSize();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                List<SheetItem> list = new ArrayList<SheetItem>();
                SheetItem is = new SheetItem("注销", getResources().getColor(R.color.red_F85757));
                list.add(is);

                new Sheet.Builder(this)
                        .setMessage("是否注销当前账户？")
                        .setConfirm("取消", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setItems(list)
                        .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Dialog dialog, View view, int position) {
                                dialog.dismiss();
                                if (UserHelper.logout()) {
                                    showShortToast("注销成功");
                                    finish();
                                } else {
                                    showShortToast("注销失败");
                                }
                            }
                        })
                        .show();
                break;

            case R.id.ll_clear_cache:
                new Prompter.Builder(this)
                        .setMessage("确定清除所有缓存？")
                        .setNegativeButton("取消", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("清除缓存", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                                showProgress("正在清除...");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FileUtils.clearCache();
                                        long size = FileUtils.getCacheSize();
                                        Message message = handler.obtainMessage(MESSAGE_CLEAR_CACHE);
                                        message.obj = size;
                                        handler.sendMessage(message);
                                    }
                                }).start();
                            }
                        })
                        .show();
                break;

            case R.id.ll_about:
                startActivity(AboutActivity.class);
                break;

            case R.id.ll_qaa:
                startActivity(QaActivity.class);
                break;

            case R.id.ll_check_upgrade:
                if (isCheckingUpgrade) {
                    return;
                }
                BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        isCheckingUpgrade = false;
                        pbCheckingUpgrade.setVisibility(View.GONE);

                        switch (updateStatus) {
                            case UpdateStatus.Yes:
                                //do noting
                                break;

                            case UpdateStatus.No:
                                showShortToast("已是最新版本");
                                break;

                            case UpdateStatus.TimeOut:
                                showShortToast("请检查您的网络");
                                break;

                            default:
                                showShortToast("已是最新版本");
                                break;
                        }
                    }
                });

                isCheckingUpgrade = true;
                pbCheckingUpgrade.setVisibility(View.VISIBLE);
                BmobUpdateAgent.forceUpdate(this);
                break;

            case R.id.ll_feedback:
                startActivity(FeedbackActivity.class);
                break;

            case R.id.ll_pay_help:
                ParamMap<String, Object> params = new ParamMap<>();
                params.put("typeId", 1); //1表示支付问题
                params.put("typeName", "支付帮助");
                startActivity(AssistanceDetailActivity.class, params);
                break;
        }
    }

    private void getCacheFormatSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String size = FileUtils.getCacheFormatSize();
                Message message = handler.obtainMessage(MESSAGE_CACHE_SIZE);
                message.obj = size;
                handler.sendMessage(message);
            }
        }).start();
    }
}
