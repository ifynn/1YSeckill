package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/7/25.
 */
public class RefusedVersion extends BmobObject {

    private Boolean refused;
    private Integer versionCode;

    public Boolean isRefused() {
        return refused;
    }

    public void setRefused(Boolean refused) {
        this.refused = refused;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }
}
