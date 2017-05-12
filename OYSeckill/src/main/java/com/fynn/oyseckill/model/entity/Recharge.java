package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/6/28.
 */
public class Recharge extends BmobObject {

    private String orderCode;
    private String method;
    private String name;
    private String desc;
    private Double amount;
    private String state; //@see #Pay.STATE_..
    private OysUser user;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }
}
