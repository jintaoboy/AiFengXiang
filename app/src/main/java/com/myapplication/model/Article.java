package com.myapplication.model;

import java.util.Arrays;

/**
 * Created by Lin on 2019/6/9.
 */

public class Article {
    private int id;
    private String userName;
    private String content;
    private String date;
    private String pic;

    public Article() {
    }

    public Article(String userName, String content, String date, String pic) {
        this.userName = userName;
        this.content = content;
        this.date = date;
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userId) {
        this.userName = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
