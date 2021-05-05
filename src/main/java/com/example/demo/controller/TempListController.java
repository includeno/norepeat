package com.example.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.example.demo.service.FliterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


//用户临时的列表 当天清除
@Slf4j
@RestController
public class TempListController {

    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    //add
    @PostMapping("/addtemplist")
    public HashMap<String, String> add(String url){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        HashMap<String, String> result=new HashMap<>();

        url=fliterService.doFliterUrl(url);
        //DateUtil.now().toString();

        Boolean success=redisTemplate.opsForHash().putIfAbsent(username+"::templist",url,DateUtil.now().toString());
        //首次
        if(success){
            result.put("code","0");
            result.put("message","临时列表添加成功");
            result.put("address",url);
            log.info("addtolist success"+username+"->"+url);
        }
        else {
            result.put("code","1");
            result.put("message","临时列表中已存在记录");
            result.put("address",url);
            log.info("addtolist !success"+username+"->"+url);
        }

        return result;
    }

    @GetMapping("/gettemplist")
    public ArrayList<HashMap<String, String>> getTempList(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        ArrayList<HashMap<String, String>> result=new ArrayList<>();
        Set<String> keys = redisTemplate.opsForHash().getOperations().keys(username + "::templist");
        for(String key:keys){
            HashMap<String, String> map=new HashMap<>();
            map.put(key,(String)redisTemplate.opsForHash().get(username + "::templist",key));
            result.add(map);
        }
        return result;
    }
}
