package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/6/19.
 */
public class Province extends BmobObject {

    private Integer provinceId;
    private String provinceName;

    public Integer getProvinceId() {
        return provinceId;
    }

    public Province setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public Province setProvinceName(String provinceName) {
        this.provinceName = provinceName;
        return this;
    }
}
