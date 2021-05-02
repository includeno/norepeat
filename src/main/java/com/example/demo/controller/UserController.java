package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.MD5Util;
import com.example.demo.util.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@RestController
public class UserController {



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
