package com.xxxx.seckill.config;

import com.xxxx.seckill.pojo.User;

/**
 * @author yswu
 * @date 2021-02-18 09:58
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

     static void setUser(User user){
        userHolder.set(user);
    }

     static User getUser(){
        return userHolder.get();
    }
}
