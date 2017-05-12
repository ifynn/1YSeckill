package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Fynn on 2016/7/4.
 */
public class Category extends BmobObject {

    private Long categoryId;
    private BmobFile logo;
    private String name;
    private BmobRelation products;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BmobFile getLogo() {
        return logo;
    }

    public void setLogo(BmobFile logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobRelation getProducts() {
        return products;
    }

    public void setProducts(BmobRelation products) {
        this.products = products;
    }
}
