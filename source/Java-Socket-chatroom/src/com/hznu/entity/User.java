package com.hznu.entity;

public class User {
    private String name;
    private String ip;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    public User() {

    }

    public User(String name, String ip) {
        super();
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
