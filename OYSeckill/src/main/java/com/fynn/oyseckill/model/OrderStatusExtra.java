package com.fynn.oyseckill.model;

import com.fynn.oyseckill.model.entity.OrderStatus;

/**
 * Created by Fynn on 2016/7/28.
 */
public class OrderStatusExtra {
    private OrderStatus orderStatus;
    private String mobile;
    private String receiver;
    private String address;
    private String addressId;

    public OrderStatusExtra() {

    }

    public OrderStatusExtra(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressId() {
        return addressId;
    }

    public OrderStatusExtra setAddressId(String addressId) {
        this.addressId = addressId;
        return this;
    }
}
