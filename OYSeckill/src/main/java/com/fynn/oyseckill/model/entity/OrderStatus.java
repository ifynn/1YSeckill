package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/7/28.
 */
public class OrderStatus extends BmobObject {
    private LuckOrder order;
    private Integer status;
    private String title;

    public LuckOrder getOrder() {
        return order;
    }

    public void setOrder(LuckOrder order) {
        this.order = order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
