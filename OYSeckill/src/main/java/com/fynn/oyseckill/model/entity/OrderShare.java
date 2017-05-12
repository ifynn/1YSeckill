package com.fynn.oyseckill.model.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/7/17.
 */
public class OrderShare extends BmobObject {

    private Issue issue;
    private OysUser user;
    private String desc;
    private List<String> pictures;
    private Product product;

    /**
     * -1:审核中，0：审核未通过，1：审核通过
     */
    private Integer access;

    public Issue getIssue() {
        return issue;
    }

    public OrderShare setIssue(Issue issue) {
        this.issue = issue;
        return this;
    }

    public OysUser getUser() {
        return user;
    }

    public OrderShare setUser(OysUser user) {
        this.user = user;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public OrderShare setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public OrderShare setPictures(List<String> pictures) {
        this.pictures = pictures;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAccess() {
        return access;
    }

    public OrderShare setAccess(Integer access) {
        this.access = access;
        return this;
    }
}
