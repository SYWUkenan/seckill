package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.DetailVo;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.service.impl.UserServiceImpl;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;

import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yswu
 * @date 2021-02-11 15:34
 */

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    IUserService userService;

    @Autowired
    IGoodsService goodsService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value="/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request,HttpServletResponse response,
                         Model model,@CookieValue("userTicket") String ticket){

        ValueOperations valueOperations = redisTemplate.opsForValue();

        //Redis中获取页面，如果不为空，直接返回页面
        String html = ((String) valueOperations.get("goodsList"));
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        if(StringUtils.isEmpty(ticket)){
            return "login";
        }
        User user = userService.getUserByCookie(ticket, request, response);
        if(user == null){
            return "login";

        }
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();

        model.addAttribute("user",user);
        model.addAttribute("goodsList", goodsVo);

        //return "goodsList"
        //如果为空，手动渲染，存入redisl并返回
        WebContext context = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);

        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }


        return html;
    }

    /**
     * 商品详情
     * @param model
     * @param goodsId
     * @param request
     * @param response
     * @param ticket
     * @return
     */
    @RequestMapping(value = "/toDetail2/{goodsId}",produces="text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model,@PathVariable Long goodsId,
                           HttpServletRequest request,HttpServletResponse response,
                           @CookieValue("userTicket") String ticket){

        //从redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String  html = ((String) valueOperations.get("goodsDetail:" + goodsId));
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        if(StringUtils.isEmpty(ticket)){
            return "login";
        }
        User user = userService.getUserByCookie(ticket, request, response);

        if(user == null) {
            return "login";

        }

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int seckillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        

        if(nowDate.before(startDate)){
          remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);

        }else if(nowDate.after(endDate)){
            
            seckillStatus = 2;
            remainSeconds = -1;
        }else {
            seckillStatus = 1;
            remainSeconds = 0;
        }


        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("user",user);
        model.addAttribute("goods",goodsVo);

        //如果为空，进行手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);

        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60, TimeUnit.SECONDS);
        }
        return html;



    }

    /**
     * 商品详情
     * @param model
     * @param goodsId
     * @param request
     * @param response
     * @param ticket
     * @return
     */
    @RequestMapping(value = "/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, @PathVariable Long goodsId,
                             HttpServletRequest request, HttpServletResponse response,
                             @CookieValue("userTicket") String ticket){


        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        User user = userService.getUserByCookie(ticket, request, response);

        if(user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);

        }

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int seckillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;


        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);

        }else if(nowDate.after(endDate)){

            seckillStatus = 2;
            remainSeconds = -1;
        }else {
            seckillStatus = 1;
            remainSeconds = 0;
        }


        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(seckillStatus);
        detailVo.setRemainSeconds(remainSeconds);


        return RespBean.success(detailVo);



    }
}
