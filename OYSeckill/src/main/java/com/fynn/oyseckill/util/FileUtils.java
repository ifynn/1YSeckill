package com.fynn.oyseckill.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.appu.AppU;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by fynn on 16/6/25.
 */
public class FileUtils {

    public static String getFileFormatSize(long size) {
        DecimalFormat var3 = new DecimalFormat("#.00");
        String formatSize;
        if (size > 0L) {
            if (size < 1024L) {
                formatSize = var3.format((double) size) + "B";
            } else if (size < 1048576L) {
                formatSize = var3.format((double) size / 1024.0D) + "K";
            } else if (size < 1073741824L) {
                formatSize = var3.format((double) size / 1048576.0D) + "M";
            } else {
                formatSize = var3.format((double) size / 1.073741824E9D) + "G";
            }
        } else {
            formatSize = "0B";
        }

        return formatSize;
    }

    public static long getFileSize(File file) {
        long size = 0L;
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                size += getFileSize(files[i]);
            } else {
                size += files[i].length();
            }
        }
        return size;
    }

    public static String getCacheFormatSize() {
        File internalPath = AppU.app().getCacheDir();
        File extPath = new File(ImageUtils.getImageCacheDirPath());
        long totalSize = 0;

        if (internalPath.equals(extPath)) {
            totalSize = getFileSize(internalPath);
        } else {
            totalSize = getFileSize(internalPath) + getFileSize(extPath);
        }
        return getFileFormatSize(totalSize);
    }

    public static long getCacheSize() {
        File internalPath = AppU.app().getCacheDir();
        File extPath = new File(ImageUtils.getImageCacheDirPath());
        long totalSize = 0;

        if (internalPath.equals(extPath)) {
            totalSize = getFileSize(internalPath);
        } else {
            totalSize = getFileSize(internalPath) + getFileSize(extPath);
        }
        return totalSize;
    }

    public static void clearCache() {
        File file = AppU.app().getCacheDir();
        if (file != null) {
            deleteFile(file);
        }

        File extPath = new File(ImageUtils.getImageCacheDirPath());
        if (extPath != null) {
            deleteFile(extPath);
        }

        ImageUtils.getInstance().clearCache();
    }

    public static void deleteFile(File file) {
        if (!file.isFile() && file.list().length != 0) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                int length = files.length;

                for (int i = 0; i < length; ++i) {
                    file = files[i];
                    deleteFile(file);
                    file.delete();
                }
            }

        } else {
            file.delete();
        }
    }

    public static void installBmobPayPlugin(Context context) {
        String fileName = "BmobPayPlugin_7.apk";
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
