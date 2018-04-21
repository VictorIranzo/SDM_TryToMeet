package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {
    public int purpose;
    public String title;
    public String text;
    public String event_id;
    public String group_id;


    public Notification(){}

    public Notification(int purpose, String title, String text) {
        this.purpose = purpose;
        this.title = title;
        this.text = text;
    }
}
