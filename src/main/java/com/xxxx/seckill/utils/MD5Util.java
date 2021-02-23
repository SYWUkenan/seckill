package com.xxxx.seckill.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //准备一个盐
    private static final String salt = "1a2b3c4d";


    public static String inputPassToFormPass(String inputPass){

        String str = ""+salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);

    }
    public static String formPassToDBPass(String formPass,String salt){
        String str = ""+salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public  static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(fromPass, salt);
        return dbPass;


    }


//    public static  void main(String[] args){
//        //d3b1294a61a07da9b49b6e22b2cbd7f9
//        System.out.println(inputPassToFormPass("123456"));
//        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
//        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
//
//    }

//    d3b1294a61a07da9b49b6e22b2cbd7f9
//            b7797cce01b4b131b433b6acf4add449
//    b7797cce01b4b131b433b6acf4add449

}
