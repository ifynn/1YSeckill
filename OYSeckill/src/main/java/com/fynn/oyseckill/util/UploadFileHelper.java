package com.fynn.oyseckill.util;

import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Picture;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.AppU;
import org.appu.common.AppHelper;
import org.appu.common.utils.LogU;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Fynn on 2016/6/22.
 */
public class UploadFileHelper {

    public static void uploadProfile(final BaseActivity activity, String filePath) {
        uploadFile(filePath, new UploadListener() {
            @Override
            public void onSuccess(BmobFile bmobFile) {
                OysUser user = new OysUser();
                user.setProfile(bmobFile);
                user.update(AppU.app(), UserHelper.getObjectId(), new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        activity.showShortToast("头像上传成功");
                        AppHelper.sendLocalEvent(Event.EVENT_UPDATE_PROFILE);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogU.e("头像更新失败", "code:" + code + ", msg:" + msg);
                    }
                });
            }

            @Override
            public void onFailure(int code, String message) {
                LogU.e("头像上传失败", "code:" + code + ", msg:" + message);
            }
        });
    }

    public static void uploadFile(String filePath, UploadListener uploadListener) {
        BmobFile bmobFile = new BmobFile(new File(filePath));
        uploadFile(bmobFile, uploadListener);
    }

    public static void uploadFile(final BmobFile bmobFile, final UploadListener uploadListener) {
        bmobFile.uploadblock(AppU.app(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                Picture picture = new Picture();
                picture.setPicture(bmobFile);
                picture.save(AppU.app());

                if (uploadListener != null) {
                    uploadListener.onSuccess(bmobFile);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (uploadListener != null) {
                    uploadListener.onFailure(i, s);
                }
            }
        });
    }

    public static void uploadFiles(final String[] filePaths, final BatchUploadListener listener) {
        if (filePaths == null || filePaths.length == 0) {
            throw new InvalidParameterException("filePaths is null, or filePaths.length is 0.");
        }

        BmobFile.uploadBatch(AppU.app(), filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if (urls != null && urls.size() == filePaths.length) {
                    record(files);
                    if (listener != null) {
                        listener.onSuccess(files, urls);
                    }
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {
                if (listener != null) {
                    listener.onFailure(i, s);
                }
            }
        });
    }

    private static void record(List<BmobFile> files) {
        List<BmobObject> pictures = new ArrayList<>();
        Iterator<BmobFile> i = files.iterator();
        while (i.hasNext()) {
            BmobFile bf = i.next();
            Picture picture = new Picture();
            picture.setPicture(bf);
            pictures.add(picture);
        }
        new BmobObject().insertBatch(AppU.app(), pictures, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public interface UploadListener {
        void onSuccess(BmobFile bmobFile);

        void onFailure(int code, String message);
    }

    public interface BatchUploadListener {
        void onSuccess(List<BmobFile> files, List<String> urls);

        void onFailure(int code, String message);
    }

}
