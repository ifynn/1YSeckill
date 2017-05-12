package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/6/19.
 */
public class City extends BmobObject {

    private Integer cityId;
    private String cityName;
    private Province province;

    public Integer getCityId() {
        return cityId;
    }

    public City setCityId(Integer cityId) {
        this.cityId = cityId;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public City setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public Province getProvince() {
        return province;
    }

    public City setProvince(Province province) {
        this.province = province;
        return this;
    }
}
