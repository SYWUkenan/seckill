//package com.xxxx.seckill.utils;
//
///**
// * @author yswu
// * @date 2021-02-16 18:13
// */
//
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//
//@Component
//public class RedisUtils {
//
//    RedisTemplate<Object,Object> redisTemplate = new RedisTemplate<>(); //key序列器
//
//
//    @PostConstruct
//    private void init() {
//        redisTemplate.setKeySerializer(RedisSerializer.string());
//        redisTemplate.setHashKeySerializer(RedisSerializer.string());
//        redisTemplate.setValueSerializer(RedisSerializer.json());
//    }
//
//    public RedisUtils(RedisTemplate<Object, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//
//
//
//
//}
