package com.sdm.trytomeet.POJO;

public class Comment {
    public String author;
    public String text;
    public String date;
    public String image;

    public Comment() {
    }

    public Comment(String author, String text, String date, String image) {
        this.author = author;
        this.text = text;
        this.date = date;
        this.image = image;
    }
}
