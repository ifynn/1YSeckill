package com.fynn.oyseckill.model.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/7/26.
 */
public class LuckOrder extends BmobObject {

    private Issue issue;
    private OysUser user;
    private String trackingNumber;
    private String logisticsCompany;
    private String receiver;
    private String mobile;
    private String address;
    /**
     * 订单状态
     * 0：获得商品，1：待确认收货地址，2：收货地址确认过期，3：已确认收货地址（等待商品派发），
     * 4：已发货（待签收），5：已签收, 6：已晒单。
     */
    private Integer state;
    private List<String> extras;

    public LuckOrder() {
    }

    public LuckOrder(String objectId) {
        setObjectId(objectId);
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }
}
