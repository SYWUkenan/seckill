package com.xxxx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.tools.json.JSONUtil;
import com.wf.captcha.ArithmeticCaptcha;
import com.xxxx.seckill.exception.GlobalException;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillGoods;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.service.*;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import com.xxxx.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yswu
 * @date 2021-02-12 22:46
 */

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    IUserService userService;

    @Autowired
    IGoodsService goodsService;

    @Autowired
    ISeckillOrderService seckillOrderService;

    @Autowired
    IOrderService orderService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MQSender mqSender;

    @Autowired
    RedisScript<Long> script;



    Map<Long,Boolean> EmptyStockMap = new HashMap<>();
//    Map<Long,Boolean> EmptyStockMap = new HashMap<>();



    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, Long goodsId,
                            HttpServletRequest request, HttpServletResponse response,
                            @CookieValue("userTicket") String ticket){


        if(StringUtils.isEmpty(ticket)){
            return "login";
        }
        User user = userService.getUserByCookie(ticket, request, response);

        if(user == null) {
            return "login";

        }

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());

            return "seckillFail";
        }

        //判断用户是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());

            return "seckillFail";

        }

        Order order = orderService.seckill(user,goods);

        model.addAttribute("goods",goods);

        model.addAttribute("order",order);

        model.addAttribute("user",user);

        return "orderDetail";

    }

    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, Model model, Long goodsId,
                              HttpServletRequest request, HttpServletResponse response,
                              @CookieValue("userTicket") String ticket){


        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        User user = userService.getUserByCookie(ticket, request, response);

        if(user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

        }

        /*GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());

            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断用户是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));

        SeckillOrder seckillOrder = ((SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));

        if(seckillOrder!=null){


            return RespBean.error(RespBeanEnum.REPEATE_ERROR);

        }


        Order order = orderService.seckill(user,goods);

        return RespBean.success(order);
        */

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean check =  orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }




        //判断是否重复抢购
        String  seckillOrderJson = ((String) valueOperations.get("order:" + user.getId() + ":" + goodsId));
        if(!StringUtils.isEmpty(seckillOrderJson)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }


        //内存标记，减少redis访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }


        //预减库存
       // Long stock = valueOperations.decrement("seckillGoods:" + goodsId);

        //采用lua脚本设置redis分布式锁，防止redis中库存自减出现异常
        Long stock = ((Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId),
                Collections.EMPTY_LIST));

        if(stock<0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //请求入队，立即返回排队中SeckillOrder
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return  RespBean.success(0);

    }
//        @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId,String captcha,HttpServletRequest request){
        if(user == null ){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        StringBuffer url = request.getRequestURL();

        //便于测试
        captcha = "0";
        Integer count = ((Integer) valueOperations.get(url + ":" + user.getId()));
        if(count == null){
            valueOperations.set(url+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count<5){
            valueOperations.increment(url+":"+user.getId());
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }


        Boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);

        }

        String str = orderService.createPath(user,goodsId);

        return RespBean.success(str);

    }



    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId,HttpServletResponse response){
            if(user == null || goodsId<0){
                throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
            }

            //设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);

        //生成验证码,将结果放入redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码失效",e.getMessage());
        }

    }

    /**
     * 获取秒杀经过
     * @param user
     * @param goodsId
     * @return orderId:成功， -1，秒杀失败，0，排队中
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){

        if(user == null ){

            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }







    /**
     * 系统初始化，把商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return ;

        }

        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+ goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });

    }
}
