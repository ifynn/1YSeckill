package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Fynn on 2016/6/29.
 */
public class RedEnvelope extends BmobObject {

    private Double amount;
    private Double limitUseAmount;
    private String name;
    private BmobDate effectiveDate;
    private BmobDate expiryDate;
    private String desc;
    private OysUser user;
    private Boolean available;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getLimitUseAmount() {
        return limitUseAmount;
    }

    public void setLimitUseAmount(Double limitUseAmount) {
        this.limitUseAmount = limitUseAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(BmobDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BmobDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(BmobDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
