package com.fynn.oyseckill.util.constants;

/**
 * Created by Fynn on 2016/6/6.
 */
public class Access {

    public static class Mob { // mob.com 移动后端服务

        public static class SMS { // 注册时需要发送短信验证码
            public static final String APP_KEY = "你自己的key";
            public static final String APP_SECRET = "你自己的secret";
        }

        public static class Share { // mob集成分享：qq、微信、微博等
            public static final String APP_KEY = "你自己的key";
        }
    }

    public static class Bmob {
        public static final String APP_ID = "96f8ea0212da0befa6753fbd787dbf02";
    }

    public static class Weibo { // 微博，用于分享
        public static final String APP_KEY = "你自己的key";
        public static final String APP_SECRET = "你自己的secret";
    }

    public static class Tencent {
        public static class MTA {
            public static final String APP_KEY = "你自己的key"; //腾讯移动统计需要
        }

        public static class QQ { // 用于分享
            public static final String APP_KEY = "你自己的key";
            public static final String APP_ID = "你自己的id";
        }

        public static class Wechat { // 用于微信分享
            public static final String APP_ID = "你自己的id";
            public static final String APP_SECRET = "你自己的secret";
        }
    }
}
