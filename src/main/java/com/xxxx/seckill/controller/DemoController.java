package com.xxxx.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/demo")
public class DemoController {


    /**
     * 功能描述 页面跳转
     * @param model
     * @return
     */

    @RequestMapping("/hello")
    public String hello (Model model){
        model.addAttribute("name","xxxxx");
        return "hel";
    }
}
