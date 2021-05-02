package com.example.demo.controller;

import com.example.demo.service.FliterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
public class FliterController {


    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    //add
    @PostMapping("/add")
    public HashMap<String, String> add( String url){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        HashMap<String, String> result=new HashMap<>();

        url=fliterService.doFliterUrl(url);
        Boolean success=redisTemplate.opsForValue().setIfAbsent(username+"::"+url,"1");
        //首次
        if(success){
            //全局统计加一
            redisTemplate.opsForHash().putIfAbsent("global::"+url,username,"1");
            result.put("code","0");
            result.put("message","网页无效操作成功");
            result.put("address",url);
            log.info("add success"+username+"->"+url);
        }
        else {
            result.put("code","1");
            result.put("message","已无效");
            result.put("address",url);
            log.info("add !success"+username+"->"+url);
        }

        return result;
    }

    @PostMapping("/delete")
    public HashMap<String, String> delete(String url){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        HashMap<String, String> result=new HashMap<>();

        url=fliterService.doFliterUrl(url);
        Boolean success = redisTemplate.delete(username + "::" + url);
        if(success){
            //全局统计减一
            redisTemplate.opsForHash().delete("global::"+url,username);
            result.put("code","0");
            result.put("message","网页还原成功");
            result.put("address",url);
            log.info("delete success "+username+"->"+url);
        }
        else {
            result.put("code","0");
            result.put("message","网页未被禁止");
            result.put("address",url);
            log.info("delete !success "+username+"->"+url);
        }

        return result;
    }

    @PostMapping("/check")
    public HashMap<String, String> check(String url){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        HashMap<String, String> result=new HashMap<>();
        log.info("check  url"+username+"->"+url);
        url=fliterService.doFliterUrl(url);
        String value= redisTemplate.opsForValue().get(username + "::" + url);

        if(value!=null){
            //https://blog.csdn.net/alice_tl/article/details/92227574
            result.put("code","1");
            result.put("message","此网页无效");
            result.put("address",url);
            log.info("check  此网页无效"+username+"->"+url);
        }
        else {
            result.put("code","0");
            result.put("message","此网页有效");
            result.put("address",url);
            log.info("check  此网页有效"+username+"->"+url);
        }

        return result;
    }

}
