package com.fynn.oyseckill.app.module.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.db.UserDb;
import com.fynn.oyseckill.model.entity.OysInstallation;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.UserAccess;
import com.fynn.oyseckill.util.FileUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.util.view.PrompterUtils;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Prompter;

import org.appu.AppU;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private BottomNavigationBar bnbTab;
    private Fragment[] fragments;
    private String[] tags;

    private boolean isLogout;

    @Override
    public int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        bnbTab = $(R.id.bnb_tab);

        fragments = new Fragment[4];
        fragments[0] = new HomeFragment();
        fragments[1] = new RecentFragment();
        fragments[2] = new OrderShareFragment();
        fragments[3] = new MineFragment();

        tags = new String[4];
        tags[0] = "tab0";
        tags[1] = "tab1";
        tags[2] = "tab2";
        tags[3] = "tab3";
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        bnbTab.setMode(BottomNavigationBar.MODE_FIXED);
        bnbTab.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bnbTab.setTabSelectedListener(this);
        bnbTab.addItem(new BottomNavigationItem(R.drawable.tab_main_selector, "秒杀"))
                .addItem(new BottomNavigationItem(R.drawable.tab_recent_selector, "最近揭晓"))
                .addItem(new BottomNavigationItem(R.drawable.tab_find_selector, "发现"))
                .addItem(new BottomNavigationItem(R.drawable.tab_profile_selector, "我的"))
                .setFirstSelectedPosition(0)
                .setBarBackgroundColor(R.color.white)
                .setInActiveColor(R.color.gray_A9A9A9)
                .initialise();

        if (savedInstanceState == null) {
            fragments[0] = new HomeFragment();
            fragments[1] = new RecentFragment();
            fragments[2] = new OrderShareFragment();
            fragments[3] = new MineFragment();

            Bundle params = new Bundle();
            params.putBoolean("lazyLoad", true);
            fragments[2].setArguments(params);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_content, fragments[0], tags[0])
                    .add(R.id.fl_content, fragments[1], tags[1])
                    .add(R.id.fl_content, fragments[2], tags[2])
                    .add(R.id.fl_content, fragments[3], tags[3])
                    .commit();
        } else {
            fragments[0] = getSupportFragmentManager().findFragmentByTag(tags[0]);
            fragments[1] = getSupportFragmentManager().findFragmentByTag(tags[1]);
            fragments[2] = getSupportFragmentManager().findFragmentByTag(tags[2]);
            fragments[3] = getSupportFragmentManager().findFragmentByTag(tags[3]);
        }

        showTab(0);

        register(Event.EVENT_LOGOUT, Event.EVENT_LOGIN, Event.EVENT_REFUSED_VERSION);
        checkUpgrade();
        recordLaunchTime();
        checkNeedLogout();
        silentLogin();
        checkLocked();
    }

    private void checkLocked() {
        if (!UserHelper.isLogin()) {
            return;
        }
        BmobQuery<UserAccess> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.addWhereEqualTo("isLocked", true);
        query.findObjects(me, new FindListener<UserAccess>() {
            @Override
            public void onSuccess(List<UserAccess> list) {
                if (list != null && !list.isEmpty()) {
                    if (!isLogout) {
                        isLogout = true;
                        UserHelper.logout();
                        UserAccess ua = list.get(0);
                        String uar = ua.getLockReason();
                        if (!TextUtils.isEmpty(uar)) {
                            uar = "\n\n锁定原因：" + uar;
                        }
                        PrompterUtils.showCaution(me, "当前用户已被锁定登录" + uar, "确定", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void silentLogin() {
        if (!UserHelper.isLogin()) {
            return;
        }
        String un = UserDb.getUn();
        String pw = UserDb.getPP();
        if (!TextUtils.isEmpty(un) && !TextUtils.isEmpty(pw)) {
            BmobUser.loginByAccount(
                    this, un, pw, new LogInListener<OysUser>() {

                        @Override
                        public void done(OysUser oysUser, BmobException e) {
                            if (e != null && e.getErrorCode() == 101) {
                                if (UserHelper.isLogin() && !isLogout) {
                                    isLogout = true;
                                    showShortToast("登录过期，请重新登录");
                                    UserHelper.logout();
                                    startActivity(LoginActivity.class);
                                }
                            }
                        }
                    });
        }
    }

    private void recordLaunchTime() {
        Bmob.getServerTime(me, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {
                final Date d = new Date(l * 1000L);
                BmobQuery<OysInstallation> query = new BmobQuery<OysInstallation>();
                query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(me));
                query.findObjects(me, new FindListener<OysInstallation>() {
                    @Override
                    public void onSuccess(List<OysInstallation> list) {
                        if (list != null && !list.isEmpty()) {
                            OysInstallation oi = list.get(0);
                            BmobDate launchedAt = oi.getLaunchedAt();
                            if (launchedAt != null) {
                                long timeStamp = BmobDate.getTimeStamp(launchedAt.getDate());
                                Date launchedDate = new Date(timeStamp);
                                Date afterLaunchDate = DateTimeUtils.addDate(
                                        launchedDate, Calendar.DAY_OF_MONTH, 15);
                                if (afterLaunchDate.compareTo(d) < 0) {
                                    if (UserHelper.isLogin() && !isLogout) {
                                        isLogout = true;
                                        UserHelper.logout();
                                        showShortToast("登录过期，请重新登录");
                                        startActivity(LoginActivity.class);
                                    }
                                }
                            }

                            if (UserHelper.isLogin()) {
                                oi.setUser(UserHelper.getUser());
                            } else {
                                oi.remove("user");
                                oi.remove("loggedAt");
                            }
                            oi.setLaunchedAt(new BmobDate(d));
                            oi.update(me);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void checkNeedLogout() {
        if (!UserHelper.isLogin()) {
            return;
        }
        BmobQuery<OysInstallation> query = new BmobQuery<OysInstallation>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.order("loggedAt");
        query.findObjects(me, new FindListener<OysInstallation>() {
            @Override
            public void onSuccess(List<OysInstallation> list) {
                if (list != null && list.size() >= 3) {
                    int size = list.size() - 2;
                    for (int i = 0; i < size; i++) {
                        OysInstallation oi = list.get(i);
                        if (oi.getInstallationId().equals(OysInstallation.getInstallationId(me))) {
                            if (UserHelper.isLogin() && !isLogout) {
                                UserHelper.logout();
                                showShortToast("登录过期，请重新登录");
                                startActivity(LoginActivity.class);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void checkUpgrade() {
        BmobUpdateAgent.setUpdateListener(null); //防止设置中的『检查更新』监听对此处起作用
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.setUpdateCheckConfig(false);
        BmobUpdateAgent.update(this);
    }

    private void showTab(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == position) {
                transaction.show(fragments[i]);
                int selectedTabIdx = bnbTab.getCurrentSelectedPosition();
                if (selectedTabIdx != i) {
                    bnbTab.selectTab(i, false);
                }
            } else {
                transaction.hide(fragments[i]);
            }
        }
        transaction.commit();
    }

    @Override
    public void onTabSelected(int position) {
        showTab(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {
        if (AppU.isDebug()) {
            showShortToast("Refreshing Tab " + position);
        }
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (Event.EVENT_LOGOUT.equals(action)) {
            FileUtils.clearCache();
            UserDb.clearAll();

        } else if (Event.EVENT_LOGIN.equals(action)) {
            Bmob.getServerTime(me, new GetServerTimeListener() {
                @Override
                public void onSuccess(long l) {
                    final Date d = new Date(l * 1000L);
                    BmobQuery<OysInstallation> query = new BmobQuery<OysInstallation>();
                    query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(me));
                    query.findObjects(me, new FindListener<OysInstallation>() {
                        @Override
                        public void onSuccess(List<OysInstallation> list) {
                            if (list != null && list.size() > 0) {
                                OysInstallation mbi = list.get(0);
                                mbi.setUser(UserHelper.getUser());
                                mbi.setLoggedAt(new BmobDate(d));
                                mbi.update(me);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        } else if (Event.EVENT_REFUSED_VERSION.equals(action)) {
            new Prompter.Builder(me)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .setMessage("当前版本不可用，请升级到最新版本！")
                    .setPositiveButton("好的", new IPrompter.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        }
    }
}
