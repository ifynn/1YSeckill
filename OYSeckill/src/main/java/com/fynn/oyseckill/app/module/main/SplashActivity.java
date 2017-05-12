package com.fynn.oyseckill.app.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.core.CoreService;
import com.fynn.oyseckill.app.core.IpService;
import com.fynn.oyseckill.model.entity.OysInstallation;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.SpKey;

import org.appu.common.utils.DeviceUtils;
import org.appu.common.utils.NetUtils;
import org.appu.common.utils.PkgUtils;
import org.appu.common.utils.TextUtils;
import org.appu.data.Storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Fynn on 16/9/15.
 */
public class SplashActivity extends BaseActivity {

    private Handler handler;
    private Timer timer;
    private TextView tvSkip;
    private ViewPager vpPreview;

    private int time = 3000;
    private boolean isPaused;

    @Override
    public int getContentResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvSkip = $(R.id.tv_skip);
        vpPreview = $(R.id.vp_preview);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                timer.cancel();

                String text = "跳过\n" + (time / 1000);
                tvSkip.setVisibility(View.VISIBLE);
                tvSkip.setText(text);

                if (time <= 0) {
                    handler.removeCallbacksAndMessages(null);
                    if (!isPaused) {
                        gotoMain();
                    } else {
                        tvSkip.setText("跳过");
                    }
                } else {
                    time -= 1000;
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        };

        timer = new Timer();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        if (!handlePreview()) {
            vpPreview.setVisibility(View.GONE);
            tvSkip.setVisibility(View.GONE);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 2000);

            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    gotoMain();
                }
            });

        } else {
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMain();
                }
            });
        }

        doInit();
    }

    public void doInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(SplashActivity.this, IpService.class));

                if (UserHelper.getUser() != null) {
                    String sessionToken = UserHelper.getUser().getSessionToken();
                    if (TextUtils.isEmpty(sessionToken.trim())) {
                        UserHelper.logout();
                    }
                }

                //Bmob Installer
                BmobQuery<OysInstallation> query = new BmobQuery<OysInstallation>();
                query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(SplashActivity.this));
                query.findObjects(SplashActivity.this, new FindListener<OysInstallation>() {

                    @Override
                    public void onSuccess(List<OysInstallation> list) {
                        if (list != null && list.size() > 0) {
                            OysInstallation oi = list.get(0);
                            String model = DeviceUtils.getModel();
                            String installedApps = DeviceUtils.getAppList();
                            String imei = DeviceUtils.getImei();
                            String ip = DeviceUtils.getPublicIp();
                            String macAdr = DeviceUtils.getMacAddress();
                            String phone = DeviceUtils.getPhoneNumber();
                            String pixels = DeviceUtils.getPixels();
                            String sdkVersion = DeviceUtils.getSdkVersion();
                            String simOperator = DeviceUtils.getSimOperatorName();
                            String appVersion = PkgUtils.getAppVersion();
                            String netTypeName = NetUtils.getNetTypeName();

                            oi.setModel(model);
                            oi.setInstalledApps(installedApps);
                            oi.setImei(imei);
                            oi.setIp(ip);
                            oi.setAppVersion(appVersion);
                            oi.setMacAdr(macAdr);
                            oi.setNetType(netTypeName);
                            oi.setPhone(phone);
                            oi.setPixels(pixels);
                            oi.setSdkVersion(sdkVersion);
                            oi.setSimOperator(simOperator);
                            oi.update(me);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        }).start();
    }

    private void gotoMain() {
        startActivity(MainActivity.class);
        startService(new Intent(me, CoreService.class));
        finish();
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        isPaused = false;
        if (tvSkip.getText().toString().equals("跳过")) {
            gotoMain();
        }
        super.onResume();
    }

    private boolean handlePreview() {
        if (Storage.getInt(SpKey.CURRENT_VERSION, -1) == PkgUtils.getVersionCode()) {
            return false;
        } else {
            Storage.put(SpKey.CURRENT_VERSION, PkgUtils.getVersionCode());
        }

        final List<View> views = new ArrayList<>();
        int[] bgs = new int[]{R.drawable.preview_1_bg, R.drawable.preview_2_bg,
                R.drawable.preview_3_bg, R.drawable.preview_4_bg};
        int[] images = new int[]{R.drawable.preview_1_image, R.drawable.preview_2_image,
                R.drawable.preview_3_image, R.drawable.preview_4_image};

        for (int i = 0; i < 4; i++) {
            View v = LayoutInflater.from(me).inflate(R.layout.layout_preview_page_item, null);
            ViewGroup root = (ViewGroup) v.findViewById(R.id.ll_root);
            ImageView ivSnapshot = (ImageView) v.findViewById(R.id.iv_snapshot);
            root.setBackgroundResource(bgs[i]);
            ivSnapshot.setImageResource(images[i]);
            views.add(v);
        }

        vpPreview.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position), 0);
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        vpPreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int pos = position + 1;
                if (pos == views.size()) {
                    tvSkip.setText("进入");
                } else {
                    tvSkip.setText(pos + "/" + views.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvSkip.setVisibility(View.VISIBLE);
        tvSkip.setText("1/" + views.size());

        return true;
    }
}
