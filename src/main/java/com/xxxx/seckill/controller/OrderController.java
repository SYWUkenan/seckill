package com.xxxx.seckill.controller;


import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.OrderDetailVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yswu
 * @since 2021-02-12
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {


    @Autowired
    IUserService userService;

    @Autowired
    IOrderService orderService;

    /**
     * 订单详情
     *
     * @param model
     * @param orderId
     * @param request
     * @param response
     * @param ticket
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(Model model, Long orderId,
                           HttpServletRequest request, HttpServletResponse response,
                           @CookieValue("userTicket") String ticket) {

        User user = userService.getUserByCookie(ticket, request, response);

        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

        }

        log.info("{}",orderId);
        OrderDetailVo orderDetailVo = orderService.detail(orderId);


        return RespBean.success(orderDetailVo);
    }


}
