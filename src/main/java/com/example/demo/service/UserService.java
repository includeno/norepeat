package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.util.UserData;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService<T extends User> implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = UserData.data.get(username);
        if(password!=null){
            User user=User.build(username,password);
            //权限固定
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("user"));
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),  authorities);
        }
        //

        return null;
    }
}
