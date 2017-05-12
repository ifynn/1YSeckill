package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 16/9/3.
 */
public class UserAccess extends BmobObject {

    private Boolean isLocked;
    private String lockReason;
    private OysUser user;

    public String getLockReason() {
        return lockReason;
    }

    public UserAccess setLockReason(String lockReason) {
        this.lockReason = lockReason;
        return this;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public UserAccess setLocked(Boolean locked) {
        isLocked = locked;
        return this;
    }

    public OysUser getUser() {
        return user;
    }

    public UserAccess setUser(OysUser user) {
        this.user = user;
        return this;
    }
}
