package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/6/19.
 */
public class County extends BmobObject {

    private Integer countyId;
    private String countyName;
    private City city;

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public County setCountyName(String countyName) {
        this.countyName = countyName;
        return this;
    }

    public City getCity() {
        return city;
    }

    public County setCity(City city) {
        this.city = city;
        return this;
    }
}
