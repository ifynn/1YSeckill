package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/8/8.
 */
public class Expense extends BmobObject {
    private String orderCode;
    private String method;
    private String desc;
    private Double amount;
    private String state;
    private OysUser user;
    private Issue issue;

    public String getOrderCode() {
        return orderCode;
    }

    public Expense setOrderCode(String orderCode) {
        this.orderCode = orderCode;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Expense setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public Expense setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public Expense setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public String getState() {
        return state;
    }

    public Expense setState(String state) {
        this.state = state;
        return this;
    }

    public OysUser getUser() {
        return user;
    }

    public Expense setUser(OysUser user) {
        this.user = user;
        return this;
    }

    public Issue getIssue() {
        return issue;
    }

    public Expense setIssue(Issue issue) {
        this.issue = issue;
        return this;
    }
}
