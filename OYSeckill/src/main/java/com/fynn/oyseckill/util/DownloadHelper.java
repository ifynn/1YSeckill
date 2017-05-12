package com.fynn.oyseckill.util;

import com.fynn.oyseckill.util.constants.Event;

import org.appu.AppU;
import org.appu.common.AppHelper;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.security.Base64Helper;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by Fynn on 2016/6/22.
 */
public class DownloadHelper {

    public static void loadProfile() {
        loadProfile(true);
    }

    /**
     * 加载用户头像
     *
     * @param ignoreExisted 是否忽略本地已存在的头像文件
     */
    public static void loadProfile(boolean ignoreExisted) {
        if (!UserHelper.isLogin()) {
            return;
        }

        BmobFile profile = UserHelper.getUser().getProfile();
        if (profile == null || TextUtils.isEmpty(profile.getFileUrl(AppU.app()))) {
            return;
        }

        final String profileName = Base64Helper.encode(UserHelper.getObjectId());
        File faceFile = new File(AppU.getCachePath(), profileName);
        if (!ignoreExisted && faceFile.exists()) {
            return;
        }

        BmobFile bmobFile = new BmobFile(profileName, "", profile.getFileUrl(AppU.app()));
        File saveFile = new File(AppU.getCachePath(), profileName);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        bmobFile.download(AppU.app(), saveFile, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {
                AppHelper.sendLocalEvent(Event.EVENT_UPDATE_PROFILE);
                LogU.e("头像缓存成功", "缓存路径：" + s);
            }

            @Override
            public void onFailure(int i, String s) {
                LogU.e("头像缓存失败", "code:" + i, "msg:" + s);
            }
        });
    }
}
