package com.myapplication.model;

/**
 * Created by Lin on 2019/6/18.
 */

public class Zan {
    private int id;
    private String userName;
    private String articleId;

    public Zan() {
    }

    public Zan(String userName, String articleId) {
        this.userName = userName;
        this.articleId = articleId;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "Zan{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", articleId='" + articleId + '\'' +
                '}';
    }
}
