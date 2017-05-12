package com.fynn.oyseckill.util.constants;

/**
 * Created by fynn on 16/6/8.
 */
public class Mob {

    /**
     * 短信验证码错误
     */
    public static final String SMS_CODE_VERIFY_ERROR = "468";

    /**
     * 每分钟发送短信的数量超过限制
     */
    public static final String SMS_CODE_SEND_FREQUENT = "462";

    /**
     * 手机号码在当前APP内每天发送短信的次数超出限制
     */
    public static final String SMS_CODE_SEND_OVERRUN_WITH_CURRENT_APP = "463";

    /**
     * 每台手机每天发送短信的次数超限
     */
    public static final String SMS_CODE_SEND_OVERRUN_WITH_DAY = "464";

    /**
     * 手机号码在APP中每天发送短信的数量超限
     */
    public static final String SMS_CODE_SEND_OVERRUN_WITH_ALL_APP = "465";

    /**
     * 5分钟内校验错误超过3次，验证码失效
     */
    public static final String SMS_CODE_VERIFY_FREQUENT = "467";

    /**
     * 当前手机号码在SMSSDK平台内每天最多可发送短信10条，包括客户端发送和WebApi发送
     */
    public static final String SMS_CODE_FREQUENT_WITH_SMS_SDK = "477";
}
