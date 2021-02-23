package com.xxxx.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author yswu
 * @date 2021-02-18 09:48
 */

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){

            User user = getUser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit methodAnnotation = hm.getMethodAnnotation(AccessLimit.class);

            if(methodAnnotation==null){
                return true;
            }

            int second = methodAnnotation.second();
            int maxCount = methodAnnotation.maxCount();
            boolean needLogin = methodAnnotation.needLogin();

            String key = request.getRequestURL().toString();

            if(needLogin){
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key+=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = ((Integer) valueOperations.get(key));

            if(count == null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);

            }else if(count<maxCount){
                valueOperations.increment(key);
            }else{
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }

        }




        return true;
    }


    /**
    * @Description: 构建返回对象
    * @Param: 
    * @return: 
    * @Author: yswu
    * @Date: 2021-02-18
    */
    
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum)throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        RespBean bean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(bean));
        out.flush();
        out.close();
    }

   /**
   * @Description: 获取用户
   * @Param: 
   * @return: 
   * @Author: yswu
   * @Date: 2021-02-18
   */
   
    private User getUser(HttpServletRequest request, HttpServletResponse response) {


    return UserContext.getUser();
    }
}
