package com.fynn.oyseckill.model;

/**
 * Created by Fynn on 2016/9/6.
 */
public class Link {

    private String name;
    private String url;
    private Boolean isHot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isHot() {
        return isHot;
    }

    public void setHot(Boolean hot) {
        isHot = hot;
    }
}
