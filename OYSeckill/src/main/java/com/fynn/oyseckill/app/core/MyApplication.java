package com.fynn.oyseckill.app.core;

import android.app.Application;

import com.fynn.oyseckill.util.constants.Access;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;

import org.appu.AppU;
import org.appu.common.utils.LogU;

import java.util.HashMap;

import c.b.BP;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.smssdk.SMSSDK;

/**
 * Created by fynn on 16/4/23.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppU.init(this);

        LogU.e("MyApplication onCreate");
        AppU.setCachePath(getCacheDir() + "/oyseckill/");
        CrashHandler.init();

        initSDK();
    }

    private void initSDK() {
        //SMS SDK
        SMSSDK.initSDK(getApplicationContext(), Access.Mob.SMS.APP_KEY, Access.Mob.SMS.APP_SECRET);

        //Tencent MTA
        StatConfig.setDebugEnable(AppU.isDebug());
        try {
            StatService.startStatService(this, Access.Tencent.MTA.APP_KEY, StatConstants.VERSION);
        } catch (MtaSDkException e) {
            e.printStackTrace();
        }
        StatService.registerActivityLifecycleCallbacks(this);

        //Bmob SDK
        Bmob.initialize(this, Access.Bmob.APP_ID);
        BP.init(this, Access.Bmob.APP_ID);
        BmobInstallation.getCurrentInstallation(this).save();
        BmobPush.startWork(this);

        //Baidu MTJ
        com.baidu.mobstat.StatService.start(this);
        com.baidu.mobstat.StatService.setDebugOn(AppU.isDebug());

        //Mob Share
        ShareSDK.initSDK(this, Access.Mob.Share.APP_KEY);
        initShareSDK();
    }

    private void initShareSDK() {
        //微信朋友圈
        HashMap<String, Object> wechatMoment = new HashMap<String, Object>();
        wechatMoment.put("Id", "1");
        wechatMoment.put("SortId", "1");
        wechatMoment.put("AppId", Access.Tencent.Wechat.APP_ID);
        wechatMoment.put("AppSecret", Access.Tencent.Wechat.APP_SECRET);
        wechatMoment.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechatMoment);

        //微信好友
        HashMap<String, Object> wechat = new HashMap<String, Object>();
        wechat.put("Id", "2");
        wechat.put("SortId", "2");
        wechat.put("AppId", Access.Tencent.Wechat.APP_ID);
        wechat.put("AppSecret", Access.Tencent.Wechat.APP_SECRET);
        wechat.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);

        //QQ
        HashMap<String, Object> qq = new HashMap<String, Object>();
        qq.put("Id", "3");
        qq.put("SortId", "3");
        qq.put("AppId", Access.Tencent.QQ.APP_ID);
        qq.put("AppKey", Access.Tencent.QQ.APP_KEY);
        qq.put("Enable", "true");
        qq.put("ShareByAppClient", "true");
        ShareSDK.setPlatformDevInfo(QQ.NAME, qq);

        //QQ空间
        HashMap<String, Object> qzone = new HashMap<String, Object>();
        qzone.put("Id", "4");
        qzone.put("SortId", "4");
        qzone.put("AppId", Access.Tencent.QQ.APP_ID);
        qzone.put("AppKey", Access.Tencent.QQ.APP_KEY);
        qzone.put("Enable", "true");
        qzone.put("ShareByAppClient", "true");
        ShareSDK.setPlatformDevInfo(QZone.NAME, qzone);

        //微博
        HashMap<String, Object> weibo = new HashMap<String, Object>();
        weibo.put("Id", "5");
        weibo.put("SortId", "5");
        weibo.put("AppKey", Access.Weibo.APP_KEY);
        weibo.put("AppSecret", Access.Weibo.APP_SECRET);
        weibo.put("Enable", "false");
        weibo.put("ShareByAppClient", "true");
        weibo.put("RedirectUrl", "http://oyseckill.bmob.cn");
        ShareSDK.setPlatformDevInfo(SinaWeibo.NAME, weibo);

        //微信收藏
        HashMap<String, Object> wechatFavorite = new HashMap<String, Object>();
        wechatFavorite.put("Id", "6");
        wechatFavorite.put("SortId", "6");
        wechatFavorite.put("AppId", Access.Tencent.Wechat.APP_ID);
        wechatFavorite.put("AppSecret", Access.Tencent.Wechat.APP_SECRET);
        wechatFavorite.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(WechatFavorite.NAME, wechatFavorite);

        //短信
        HashMap<String, Object> sms = new HashMap<String, Object>();
        wechatFavorite.put("Id", "7");
        wechatFavorite.put("SortId", "7");
        wechatFavorite.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(ShortMessage.NAME, sms);
    }
}
