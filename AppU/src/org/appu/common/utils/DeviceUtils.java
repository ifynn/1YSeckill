package org.appu.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.appu.AppU;
import org.appu.data.Storage.Storage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class DeviceUtils {

    /**
     * 获取手机imei
     *
     * @return
     */
    public static String getImei() {
        TelephonyManager tm = (TelephonyManager) AppU.app().getSystemService(
                Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei == null || imei.trim().equals(""))
            return "null";
        return imei;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取SDK版本
     *
     * @return
     */
    public static String getSdkVersion() {
        String sdk = String.valueOf(Build.VERSION.SDK_INT);
        String release = String.valueOf(Build.VERSION.RELEASE);
        return release + "（" + sdk + "）";
    }

    /**
     * 获取手机号码
     *
     * @return
     */
    public static String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) AppU.app().getSystemService(
                Context.TELEPHONY_SERVICE);
        String number = tm.getLine1Number();
        if (TextUtils.isEmpty(number)) {
            return "";
        }
        return number;
    }

    /**
     * 获取已安装应用程序列表
     *
     * @return
     */
    public static String getAppList() {
        PackageManager packageManager = AppU.app().getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        String appList = "";
        for (int i = 0; i < info.size(); i++) {
            String app = info.get(i).applicationInfo.loadLabel(packageManager).toString();
            appList = appList + app + "，";
        }
        appList = appList.substring(0, appList.length() - 1);
        appList = "{" + appList + "}";
        return appList;
    }

    /**
     * 获取手机归属：移动、联通或电信
     *
     * @return
     */
    public static String getSimOperatorName() {
        TelephonyManager tm = (TelephonyManager) AppU.app().getSystemService(
                Context.TELEPHONY_SERVICE);
        String name = tm.getSimOperatorName();
        if (name == null || name.trim().equals("")) {
            name = "";
        }
        return name;
    }

    /**
     * 获取设备像素
     *
     * @return
     */
    public static String getPixels() {
        DisplayMetrics dm = AppU.app().getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        String px = w + "*" + h;
        return px;
    }

    /**
     * 获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    public static String getMacAddress() {
        String result = "";
        WifiManager wifiManager = (WifiManager)
                AppU.app().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return result;
    }

    public static String getPublicIp() {
        String ip = Storage.getString("ip", "");
        return ip;
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nti = en.nextElement();
                Enumeration<InetAddress> enia = nti.getInetAddresses();
                while (enia.hasMoreElements()) {
                    InetAddress inetAddress = enia.nextElement();
                    LogU.e("ip address", inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LogU.w(e);
        }
        return "";
    }
}
