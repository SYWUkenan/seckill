package com.xxxx.seckill.rabbitmq;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

/**
 * @author yswu
 * @date 2021-02-15 11:00
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    IGoodsService goodsService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    IOrderService orderService;


//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("queue01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("queue02接收消息:" + msg);
//    }
//
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("queue01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("queue02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("queue01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("queue02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header01")
//    public void receive07(Message message){
//        log.info("QUEUE01接收Message对象:" + message);
//        log.info("QUEUE01接收消息:" + new String(message.getBody()));
//
//    }
//
//    @RabbitListener(queues = "queue_header02")
//    public void receive08(Message message){
//        log.info("QUEUE02接收Message对象:" + message);
//        log.info("QUEUE02接收消息:" + new String(message.getBody()));
//
//    }

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg){
        log.info("QUEUE接收消息:" + msg);

        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            return ;
        }

        String seckillOrderJson = ((String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));

        if(!StringUtils.isEmpty(seckillOrderJson)){
            return;
        }

        orderService.seckill(user,goods);



    }


}
