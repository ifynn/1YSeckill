package org.appu.common.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.appu.AppU;

import java.util.List;

public class PkgUtils {

    public static boolean isAvailableIntent(Intent i) {
        try {
            List<ResolveInfo> localList = AppU.app().getPackageManager()
                    .queryIntentActivities(i, PackageManager.GET_RESOLVED_FILTER);
            if (localList != null && localList.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            LogU.e(e);
            return false;
        }
        return false;
    }

    public static String getAppVersion() {
        String version = "";
        try {
            PackageManager manager = AppU.app().getPackageManager();
            PackageInfo info = manager.getPackageInfo(AppU.app().getPackageName(), 0);
            version = info.versionName + "（" + info.versionCode + "）";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return version;
        }
    }

    public static String getVersionName(String packageName) {
        String versionName = "";
        try {
            PackageManager manager = AppU.app().getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            versionName = info.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return versionName;
        }
    }

    public static String getVersionName() {
        return getVersionName(AppU.app().getPackageName());
    }

    public static int getVersionCode(String packageName) {
        int versionName = -1;
        try {
            PackageManager manager = AppU.app().getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            versionName = info.versionCode;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return versionName;
        }
    }

    public static int getVersionCode() {
        return getVersionCode(AppU.app().getPackageName());
    }

    public static boolean isPackageInstalled(String packageName) {
        PackageManager packageManager = AppU.app().getPackageManager();

        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
}
