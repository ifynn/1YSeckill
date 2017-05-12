package com.fynn.oyseckill.app.module.home.detail;

/**
 * Created by Fynn on 2016/7/13.
 */
public enum SeckillState {

    SECKILLING(0), ANNOUNCING(1), ANNOUNCED(2);

    /**
     * 揭晓状态，0：秒杀中，1：揭晓中，2：已揭晓
     */
    private int value;

    SeckillState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
