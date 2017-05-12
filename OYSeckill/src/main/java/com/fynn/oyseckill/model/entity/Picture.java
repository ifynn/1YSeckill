package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Fynn on 2016/7/4.
 */
public class Picture extends BmobObject {

    private BmobFile picture;

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }
}
