package com.xxxx.seckill.utils;

import java.util.UUID;

/**
 * @author yswu
 * @date 2021-02-11 15:09
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
