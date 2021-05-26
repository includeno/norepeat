package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.util.MD5Util;
import com.example.demo.util.UserData;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService<T extends User> implements UserDetailsService {

    @Autowired
    UserService2 userService2;

    //redis存储方案
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userService2.getUserByUsername(username);
        String password = user.getPassword();
        if(password!=null){
            //权限固定
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            String authority=user.getAuthority();
            if(authority!=null &&authority!=""){
                for(String au:authority.split(",")){
                    authorities.add(new SimpleGrantedAuthority(au));
                }
            }
            else{
                authorities.add(new SimpleGrantedAuthority("user"));
            }

            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),  authorities);
        }
        return null;
    }

//    //本地concurrenthashmap方案
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        String password = UserData.data.get(username);
//        if(password!=null){
//            User user=User.build(username,password);
//            //权限固定
//            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority("user"));
//            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),  authorities);
//        }
//        return null;
//    }

    


}
