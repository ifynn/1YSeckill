package com.fynn.oyseckill.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import org.appu.AppU;
import org.appu.common.utils.PkgUtils;

import java.io.File;

/**
 * Created by Fynn on 2016/6/22.
 */
public class ImageFileUtils {

    /**
     * 打开系统图片选择器
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean openImageChooser(Activity activity, int requestCode) {
        Intent picIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (PkgUtils.isAvailableIntent(picIntent)) {
            activity.startActivityForResult(picIntent, requestCode);
            return true;
        }

        return false;
    }

    /**
     * 选择图片后截图
     *
     * @param activity
     * @param inputUri
     * @param size
     * @param requestCode
     * @return
     */
    public static boolean cropImageFile(Activity activity, Uri inputUri, Uri outputUri, int size, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        //是否可裁剪
        intent.putExtra("crop", "true");
        //aspectX aspectY 宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        if (PkgUtils.isAvailableIntent(intent)) {
            activity.startActivityForResult(intent, requestCode);
            return true;
        }

        return false;
    }

    /**
     * 打开相机拍照
     *
     * @param activity
     * @param requestCode
     * @param outputFile
     * @return
     */
    public static boolean openCamera(Activity activity, int requestCode, File outputFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        if (PkgUtils.isAvailableIntent(intent)) {
            activity.startActivityForResult(intent, requestCode);
            return true;
        }

        return false;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = AppU.app().getContentResolver().query(uri, new String[]
                    {MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
