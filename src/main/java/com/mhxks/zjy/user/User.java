package com.mhxks.zjy.user;

public class User {
    public String userPass;
    public String userName;
    public String cookie;
    public User(String userName,String userPass,String cookie){
        this.userName = userName;
        this.userPass = userPass;
        this.cookie = cookie;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
