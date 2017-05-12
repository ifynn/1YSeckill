package com.fynn.oyseckill.model.entity;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Fynn on 2016/6/28.
 */
public class OysInstallation extends BmobInstallation {

    private OysUser user;
    private BmobDate launchedAt;
    private BmobDate loggedAt;
    private String model;

    private String installedApps;
    private String imei;
    private String ip;
    private String macAdr;
    private String phone;
    private String pixels;
    private String sdkVersion;
    private String simOperator;
    private String appVersion;
    private String netType;

    public OysInstallation(Context context) {
        super(context);
    }

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public BmobDate getLaunchedAt() {
        return launchedAt;
    }

    public void setLaunchedAt(BmobDate launchedAt) {
        this.launchedAt = launchedAt;
    }

    public BmobDate getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(BmobDate loggedAt) {
        this.loggedAt = loggedAt;
    }

    public String getModel() {
        return model;
    }

    public OysInstallation setModel(String model) {
        this.model = model;
        return this;
    }

    public String getInstalledApps() {
        return installedApps;
    }

    public OysInstallation setInstalledApps(String installedApps) {
        this.installedApps = installedApps;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public OysInstallation setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public OysInstallation setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getMacAdr() {
        return macAdr;
    }

    public OysInstallation setMacAdr(String macAdr) {
        this.macAdr = macAdr;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public OysInstallation setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPixels() {
        return pixels;
    }

    public OysInstallation setPixels(String pixels) {
        this.pixels = pixels;
        return this;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public OysInstallation setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
        return this;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public OysInstallation setSimOperator(String simOperator) {
        this.simOperator = simOperator;
        return this;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public OysInstallation setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getNetType() {
        return netType;
    }

    public OysInstallation setNetType(String netType) {
        this.netType = netType;
        return this;
    }
}
