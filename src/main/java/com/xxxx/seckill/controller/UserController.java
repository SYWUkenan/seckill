package com.xxxx.seckill.controller;


import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yswu
 * @since 2021-02-05
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MQSender mqSender;


    /**
     * 用户信息(测试) * @param user * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }


//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//
//        mqSender.send("hello");
//    }
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void fanout(){
//
//        mqSender.send("hello");
//    }
//
//
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void direct01(){
//
//        mqSender.send03("hello,red");
//    }
//
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void direct02(){
//
//        mqSender.send04("hello,green");
//    }
//
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void topic01(){
//
//        mqSender.send05("hello,red");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void topic02(){
//
//        mqSender.send06("hello,green");
//    }
//
//    @RequestMapping("/mq/header01")
//    @ResponseBody
//    public void header01(){
//
//        mqSender.send07("hello,header01");
//    }
//
//    @RequestMapping("/mq/header02")
//    @ResponseBody
//    public void header02(){
//
//        mqSender.send08("hello,header02");
//    }


}
