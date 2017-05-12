package com.fynn.oyseckill.util;

import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.AppU;
import org.appu.common.AppHelper;
import org.appu.common.utils.TextUtils;

import cn.bmob.v3.BmobUser;

/**
 * Created by fynn on 16/6/5.
 */
public final class UserHelper {

    private UserHelper() {
    }

    public static boolean isLogin() {
        OysUser user = BmobUser.getCurrentUser(AppU.app(), OysUser.class);
        if (user == null) {
            return false;
        }
        return true;
    }

    public static boolean logout() {
        BmobUser.logOut(AppU.app());   //清除缓存用户对象
        if (!isLogin()) {
            AppHelper.sendLocalEvent(Event.EVENT_LOGOUT);
            return true;
        }
        return false;
    }

    public static OysUser getUser() {
        OysUser user = BmobUser.getCurrentUser(AppU.app(), OysUser.class);
        return user;
    }

    public static String getObjectId() {
        OysUser user = BmobUser.getCurrentUser(AppU.app(), OysUser.class);
        if (user == null) {
            return "";
        } else {
            return user.getObjectId();
        }
    }

    public static String getNickname() {
        OysUser user = BmobUser.getCurrentUser(AppU.app(), OysUser.class);
        return getNickname(user);
    }

    public static String getNickname(OysUser user) {
        if (user == null) {
            return "";
        }
        Long id = user.getUserId();
        String nick = user.getNickname();

        if (!TextUtils.isEmpty(nick)) {
            return nick;
        } else {
            return String.format("oys%s", id.toString());
        }
    }
}
