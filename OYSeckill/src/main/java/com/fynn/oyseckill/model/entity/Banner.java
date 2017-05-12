package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/8/8.
 */
public class Banner extends BmobObject {
    private String picUrl;
    private Product product;
    private String h5Url;

    public String getPicUrl() {
        return picUrl;
    }

    public Banner setPicUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public Banner setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getH5Url() {
        return h5Url;
    }

    public Banner setH5Url(String h5Url) {
        this.h5Url = h5Url;
        return this;
    }
}
