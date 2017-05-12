package com.fynn.oyseckill.util.pay;

public enum PayMethod {

    WECHAT("WECHATPAY"), ALIPAY("ALIPAY"), REST_COIN("REST_COIN"), RETURN("RETURN");

    private String value;

    PayMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}