package com.myapplication.model;

/**
 * Created by Lin on 2019/6/11.
 */

public class Comments {
    private int id;
    private String userName;
    private String articleId;
    private String date;
    private String content;

    public Comments() {
    }

    public Comments(String userName, String articleId, String date, String content) {
        this.userName = userName;
        this.articleId = articleId;
        this.date = date;
        this.content = content;
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

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", articleId='" + articleId + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
