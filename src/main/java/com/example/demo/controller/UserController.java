package com.example.demo.controller;

import com.example.demo.service.UserService2;
import com.example.demo.util.MD5Util;
import com.example.demo.util.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;


@RestController
public class UserController {
    Logger log= LoggerFactory.getLogger("UserController");

    @Autowired
    UserService2 userService;

    @PostMapping("/checkuser")
    public HashMap<String, String> checkuser(){
        //获取当前用户信息
        //https://docs.spring.io/spring-security/site/docs/5.4.6/reference/html5/#servlet-authentication-securitycontextholder
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        HashMap<String, String> result=new HashMap<>();
        result.put("code","200");
        log.info("登陆 "+username);
        return result;
    }

    @PostMapping("/register")
    public HashMap<String, String> register(String username,String password){
        HashMap<String, String> result=new HashMap<>();
        Boolean registerResult=userService.register(username,password,"user");
        if(registerResult.equals(Boolean.FALSE)){
            result.put("code","403");
            log.info("注册失败 账户已存在 "+username+"->"+password);
        }
        else{
            result.put("code","200");
            log.info("注册成功 "+username+"->"+password);
        }
        return result;
    }

    //旧版本内置注册
    @PostMapping("/register2")
    public HashMap<String, String> register2(String username,String password){
        HashMap<String, String> result=new HashMap<>();
        if(UserData.data.get(username)!=null){
            result.put("code","403");
            log.info("注册失败 账户已存在 "+username+"->"+password);
        }
        else{
            UserData.data.put(username, MD5Util.encode(password));
            result.put("code","200");
            log.info("注册成功 "+username+"->"+password);
        }
        return result;
    }

}
