package com.fynn.oyseckill.app.module.account.redpkg;

/**
 * Created by Fynn on 2016/6/29.
 */
public enum RedPkgType {

    AVAILABLE(0x01), UNAVAILABLE(0x02);

    private int value;

    RedPkgType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
