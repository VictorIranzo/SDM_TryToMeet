package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {
    public int purpose;
    public String title;
    public String text;
    public String event_id;
    public String group_id;

    public static final int ADDED_TO_AN_EVENT = 1;
    public static final int ADDED_TO_A_GROUP = 2;
    public static final int EVENT_CONFIRMATE = 3;
    public static final int COMMENT_ADDED = 4;
    public static final int IMAGE_UPLOADED = 5;


    public Notification(){}

    public Notification(int purpose, String title, String text) {
        this.purpose = purpose;
        this.title = title;
        this.text = text;
    }
}
