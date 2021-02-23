package com.xxxx.seckill.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yswu
 * @date 2021-02-11 10:19
 */
public class ValidatorUtil {


    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");

    public static boolean isMObile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;

        }

        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();

    }



}
