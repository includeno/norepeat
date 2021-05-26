package com.example.demo.controller;

import com.example.demo.service.FliterService;
import com.example.demo.service.TempListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


//用户临时的列表 当天清除
@RestController
public class TempListController {
    Logger log= LoggerFactory.getLogger("TempListController");

    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    TempListService tempListService;

    //add
    @PostMapping("/addtemplist")
    public HashMap<String, String> add(String url, String time) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        HashMap<String, String> result = new HashMap<>();

        Boolean addResult=tempListService.addRecord(username,url,time);
        if (addResult) {
            result.put("code", "0");
            result.put("message", "临时列表添加成功");
            result.put("address", url);
            log.info("addtolist success" + username + "->" + url);
        } else {
            result.put("code", "1");
            result.put("message", "临时列表中已存在记录");
            result.put("address", url);
            log.info("addtolist !success" + username + "->" + url);
        }

        return result;
    }


    @GetMapping("/gettemplist")
    public ArrayList<HashMap<String, String>> getTempList() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        ArrayList<HashMap<String, String>> result = tempListService.getList(username);
        return result;
    }

    @GetMapping("/gettemplisthashmap")
    public ArrayList<HashMap<String, String>> getTempListHashMap() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        ArrayList<HashMap<String, String>> result = tempListService.getListHashMap(username);
        return result;
    }


    @PostMapping("/deletetemplist")
    public HashMap<String,String> deleteTempList(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        Boolean deleteResult =tempListService.deleteList(username);
        HashMap<String, String> result = new HashMap<>();
        if(deleteResult==true){
            result.put("code", "0");
            result.put("message", "临时列表清空成功");
            log.info("deletetemplist success username:" + username );
        }
        else{
            result.put("code", "1");
            result.put("message", "临时列表清空失败");
            log.info("deletetemplist success username:" + username );
        }
        return result;
    }


    @PostMapping("/deletetemplistitem")
    public HashMap<String,String> deleteTempListItem(String url){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        System.out.println("url:"+url);
        Boolean deleteResult =tempListService.deleteRecord(username,url);
        HashMap<String, String> result = new HashMap<>();
        if(deleteResult==true){
            result.put("code", "0");
            result.put("message", "临时列表记录删除成功");
            log.info("deleteTempListItem success username:" + username );
        }
        else{
            result.put("code", "1");
            result.put("message", "临时列表记录删除失败");
            log.info("deleteTempListItem success username:" + username );
        }
        return result;
    }
}
