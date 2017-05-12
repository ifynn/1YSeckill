package com.fynn.oyseckill.model.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/7/24.
 */
public class Feedback extends BmobObject {

    private String content;
    private String contact;
    private List<String> pictures;
    private OysUser user;
    private String installer;

    public String getContent() {
        return content;
    }

    public Feedback setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public Feedback setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public Feedback setPictures(List<String> pictures) {
        this.pictures = pictures;
        return this;
    }

    public OysUser getUser() {
        return user;
    }

    public Feedback setUser(OysUser user) {
        this.user = user;
        return this;
    }

    public String getInstaller() {
        return installer;
    }

    public Feedback setInstaller(String installer) {
        this.installer = installer;
        return this;
    }
}
