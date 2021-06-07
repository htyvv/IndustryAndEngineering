package com.example.test3;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

public class Data {

    private String password; // 검색 시 매칭용

    private int id; // 게시물 번호
    private String name; // 게시자
    private String title; // 제목
    private String date; // 날짜

    private String image; // 이미지
    private String tag; // 태그

    private String content; // 내용

    private ArrayList<DataComment> comment; // 댓글
    private ArrayList<DataLike> like;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTag() { return tag; }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<DataComment> getComment() {
        return comment;
    }
    public void setComment(ArrayList<DataComment> comment) {
        this.comment = comment;
    }

    public int getCommentAmount() {
        if (comment == null) return 0;
        else return comment.size();
    }

    public ArrayList<DataLike> getLike() { return like; }
    public void setLike(ArrayList<DataLike> like) {
        this.like = like;
    }

    public int getLikeAmount() {
        if (like == null) return 0;
        else return like.size();
    }

}