package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Fynn on 2016/6/8.
 */
public class OysUser extends BmobUser {

    private Long userId;
    private String nickname;
    private BmobFile profile;
    private BmobRelation issues;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nick) {
        this.nickname = nick;
    }

    public BmobFile getProfile() {
        return profile;
    }

    public void setProfile(BmobFile profile) {
        this.profile = profile;
    }

    public BmobRelation getIssues() {
        return issues;
    }

    public void setIssues(BmobRelation issues) {
        this.issues = issues;
    }
}
