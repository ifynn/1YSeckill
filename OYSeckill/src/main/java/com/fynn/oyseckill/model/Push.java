package com.fynn.oyseckill.model;

/**
 * Created by Fynn on 2016/8/25.
 */
public class Push {

    /**
     * 中奖消息
     */
    public static final int TYPE_GAIN_REWARD = 0x01;
    /**
     * 通知栏普通消息
     */
    public static final int TYPE_NOTIFICATION = 0x02;
    private String title;
    private String message;
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String push = "[title:" + title + ", message:" + message + ", type:" + type + "]";
        return push;
    }
}
