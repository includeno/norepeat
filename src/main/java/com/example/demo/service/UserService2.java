package com.example.demo.service;

import java.util.HashMap;

import javax.annotation.Resource;

import com.example.demo.entity.User;
import com.example.demo.util.MD5Util;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService2 {
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    // @Resource(name="redisTemplate")
    // RedisTemplate<String, String> redisTemplate;

    public String getKey(){
        return "users";
    }

    public Boolean register(String username,String password,String authority){
        HashMap<String,String> map=new HashMap<>();
        map.put("password",MD5Util.encode(password));
        map.put("authority",authority);
        Gson gson=new Gson();
        String str=gson.toJson(map,HashMap.class);
        Boolean putResult = redisTemplate.opsForHash().putIfAbsent(getKey(), username,str);
        return putResult;
    }

    public User getUserByUsername(String username){
        String str= (String) redisTemplate.opsForHash().get(getKey(), username);
        if(str==null){
            User user=new User(username,null,"");
            return user;
        }
        else{
            Gson gson=new Gson();
            HashMap<String,String> map=gson.fromJson(str,HashMap.class);
            User user=new User(username,map.get("password"),map.get("authority"));
            return user;
        }
    }
}
