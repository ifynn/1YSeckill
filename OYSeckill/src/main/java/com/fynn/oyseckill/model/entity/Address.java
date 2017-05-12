package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/6/10.
 */
public class Address extends BmobObject {

    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String county;
    private String detail;
    private Boolean isDefault;
    private Boolean isDeleted;
    private OysUser user;
    private County address;

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public County getAddress() {
        return address;
    }

    public Address setAddress(County address) {
        this.address = address;
        return this;
    }
}
