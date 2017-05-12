package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/6/28.
 */
public class Order extends BmobObject {

    private OysUser user;
    private String orderCode;
    private String installationId;

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }
}
