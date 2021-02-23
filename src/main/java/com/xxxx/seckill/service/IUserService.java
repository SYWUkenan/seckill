package com.xxxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.LoginVo;
import com.xxxx.seckill.vo.RespBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yswu
 * @since 2021-02-05
 */

public interface IUserService extends IService<User> {


    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);



    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     *
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket,String password, HttpServletRequest request, HttpServletResponse response);

}
