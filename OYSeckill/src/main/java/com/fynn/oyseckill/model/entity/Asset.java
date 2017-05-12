package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/6/29.
 */
public class Asset extends BmobObject {

    private Double oysCoin;
    private OysUser user;

    public Double getOysCoin() {
        return oysCoin;
    }

    public void setOysCoin(Double oysCoin) {
        this.oysCoin = oysCoin;
    }

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }
}
