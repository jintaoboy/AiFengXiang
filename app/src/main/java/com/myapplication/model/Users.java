package com.myapplication.model;

import java.io.Serializable;

/**
 * Created by Lin on 2019/6/8.
 */

public class Users implements Serializable {
    private int id;
    private String UserName;
    private String UserPwd;
    private String sex;
    private String gxqm;

    public Users() {
    }

    public Users(String userName, String userPwd) {
        UserName = userName;
        UserPwd = userPwd;
    }

    public Users(String userName, String sex, String gxqm) {
        UserName = userName;
        this.sex = sex;
        this.gxqm = gxqm;
    }

    public Users(int id, String userName, String userPwd) {
        this.id = id;
        UserName = userName;
        UserPwd = userPwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPwd() {
        return UserPwd;
    }

    public void setUserPwd(String userPwd) {
        UserPwd = userPwd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGxqm() {
        return gxqm;
    }

    public void setGxqm(String gxqm) {
        this.gxqm = gxqm;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", UserName='" + UserName + '\'' +
                ", UserPwd='" + UserPwd + '\'' +
                ", sex='" + sex + '\'' +
                ", gxqm='" + gxqm + '\'' +
                '}';
    }
}
