package com.fynn.oyseckill.db;

import org.appu.data.Storage.Storage;
import org.appu.security.Base64Helper;

/**
 * Created by Fynn on 2016/8/25.
 */
public class UserDb {

    private static final String USER_PREFIX = "user.db.";
    public static final String USER_REWARD_INFO = USER_PREFIX + "reward.info";
    public static final String USER_PP = USER_PREFIX + "lgpp";
    public static final String USER_UN = USER_PREFIX + "lgun";
    public static final String LAST_SUCCESS_LOGIN_USERNAME = USER_PREFIX + "last.success.login.username";

    /**
     * 存储用户中奖信息
     *
     * @param info
     */
    public static void putReward(boolean info) {
        Storage.put(USER_REWARD_INFO, info);
    }

    /**
     * 获取用户中奖信息
     */
    public static boolean isReward() {
        return Storage.getBoolean(USER_REWARD_INFO, false);
    }

    /**
     * 保存用户名至本地
     *
     * @param un
     * @return
     */
    public static void putUn(String un) {
        Storage.put(USER_UN, Base64Helper.encode(un));
    }

    /**
     * 获取本地存储的用户名
     *
     * @return
     */
    public static String getUn() {
        String un = Storage.getString(USER_UN, "");
        return Base64Helper.decode(un);
    }

    /**
     * 保存密码至本地
     *
     * @param pp
     * @return
     */
    public static void putPP(String pp) {
        Storage.put(USER_PP, Base64Helper.encode(pp));
    }

    /**
     * 获取本地存储的密码
     *
     * @return
     */
    public static String getPP() {
        String pp = Storage.getString(USER_PP, "");
        return pp;
    }

    /**
     * 存储上次成功登录的用户名
     *
     * @param un
     */
    public static void putLastSuccessLoginUsername(String un) {
        Storage.put(LAST_SUCCESS_LOGIN_USERNAME, un);
    }

    /**
     * 获取上次成功登录的用户名
     */
    public static String getLastSuccessLoginUsername() {
        return Storage.getString(LAST_SUCCESS_LOGIN_USERNAME, "");
    }

    public static void clearAll() {
        Storage.remove(USER_REWARD_INFO);
        Storage.remove(USER_UN);
        Storage.remove(USER_PP);
    }
}
