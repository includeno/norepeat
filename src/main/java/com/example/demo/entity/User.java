package com.example.demo.entity;

public class User {

    Integer id;
    String username;
    String password;
    String authority;

    public User(String username,String password){
        this.username=username;
        this.password=password;
        this.authority="";
    }
    public User(String username,String password,String authority){
        this.username=username;
        this.password=password;
        this.authority=authority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
    public static User build(String username,String password){
        User a=new User(username,password);
        return a;
    }
}
