package com.fynn.oyseckill.model.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Fynn on 2016/7/4.
 */
public class Product extends BmobObject {

    private Long productId;
    private String name;
    private String desc;
    private Double price;
    private Issue currentIssue; //当前期数
    private Boolean canBuy; //是否可以购买：除了当前期以外的最新期不再可以购买
    private Boolean needAddIssue; //不生成新的期号
    private BmobRelation pictures;
    private List<String> detailImages;
    private String image;
    private String topTips;
    private String bottomTips;
    private String type;
    private List<String> bannerImages;

    public Product() {
    }

    public Product(String objId) {
        setObjectId(objId);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Issue getCurrentIssue() {
        return currentIssue;
    }

    public void setCurrentIssue(Issue currentIssue) {
        this.currentIssue = currentIssue;
    }

    public Boolean getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(Boolean canBuy) {
        this.canBuy = canBuy;
    }

    public BmobRelation getPictures() {
        return pictures;
    }

    public void setPictures(BmobRelation pictures) {
        this.pictures = pictures;
    }

    public List<String> getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(List<String> detailImages) {
        this.detailImages = detailImages;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBottomTips() {
        return bottomTips;
    }

    public void setBottomTips(String bottomTips) {
        this.bottomTips = bottomTips;
    }

    public String getTopTips() {
        return topTips;
    }

    public void setTopTips(String topTips) {
        this.topTips = topTips;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getBannerImages() {
        return bannerImages;
    }

    public void setBannerImages(List<String> bannerImages) {
        this.bannerImages = bannerImages;
    }

    public Boolean isNeedAddIssue() {
        return needAddIssue;
    }

    public void setNeedAddIssue(Boolean needAddIssue) {
        this.needAddIssue = needAddIssue;
    }
}
