package com.xxxx.seckill.vo;


import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public enum RespBeanEnum {

    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),


    //登录模块
    LOGIN_ERROR(500210,"用户名或者密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    SESSION_ERROR(500211,"用户信息不存在"),

    //异常处理模块
    BIND_ERROR(500211,"参数校验异常"),
    MOBILE_NOZt_EXIST(500213,"手机号码不存在"),
    MOBILE_UPDATE_ERROR(500214,"更新密码失败"),

    //秒杀模块5005XX

    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"商品每人限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法"),
    ERROR_CAPTCHA(500502,"验证码错误，请重新输入"),
    ACCESS_LIMIT_REACHED(500503,"访问过于频繁"),

    //订单模块5003XX
    ORDER_NOT_EXIST(500301,"订单不存在")

    ;



    private final Integer code;
    private  final String message;
}
