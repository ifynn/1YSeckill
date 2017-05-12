package com.fynn.oyseckill.model;

/**
 * Created by Fynn on 2016/7/26.
 */
public class OrderState {

    /**
     * 订单状态
     * 0：获得商品，1：待确认收货地址，2：收货地址确认过期，3：已确认收货地址（等待商品派发），
     * 4：已发货（待签收），5：已签收，6：已晒单。
     */

    public static final int STATE_GAIN_PRODUCT = 0;
    public static final int STATE_WAIT_FOR_CONFIRM_ADDRESS = 1;
    public static final int STATE_CONFIRM_ADDRESS_LIMIT = 2;
    public static final int STATE_WAIT_FOR_DELIVER = 3;
    public static final int STATE_DELIVERED = 4;
    public static final int STATE_RECEIVED = 5;
    public static final int STATE_SHARED = 6;
}
