package com.example.demo.entity;

public class User {

    Integer id;
    String username;
    String password;
    public User(String username,String password){
        this.username=username;
        this.password=password;
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

    public static User build(String username,String password){
        User a=new User(username,password);
        return a;
    }
}
