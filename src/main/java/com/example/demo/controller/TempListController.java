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
        log.info("转化后的url:"+url);
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
        class TempListRecord{
            String url;
            String time;
            public TempListRecord(String time,String url){
                this.time=time;
                this.url=url;
            }
        }
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        ArrayList<HashMap<String, String>> result=new ArrayList<>();
        Set<Object> keys = redisTemplate.opsForHash().keys(username + "::templist");
        ArrayList<TempListRecord> records=new ArrayList();
        for(Object key:keys){
            String keystr = String.valueOf(key);
            String timestr=String.valueOf(redisTemplate.opsForHash().get(username + "::templist",key));
            records.add(new TempListRecord(timestr,keystr));

        }
        records.sort((a1,a2)->a1.time.compareTo(a2.time));
        for(TempListRecord record:records){
            HashMap<String, String> map=new HashMap<>();
            log.info("key: "+record.time+" value:"+record.url);
            map.put("url",record.url);
            map.put("time",record.time);
            result.add(map);
        }
        return result;
    }
}
