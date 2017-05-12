package com.fynn.oyseckill.app.core;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * Created by Fynn on 2016/9/13.
 */
public class MyAuthorizeAdapter extends AuthorizeAdapter {

    @Override
    public void onCreate() {
        hideShareSDKLogo();
        disablePopUpAnimation();
    }
}
