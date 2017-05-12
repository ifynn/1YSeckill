package com.fynn.oyseckill.util.pay;

public interface PayListener {

    void onSuccess(String orderId);

    void onFailure(int code, String msg, String orderId);
}