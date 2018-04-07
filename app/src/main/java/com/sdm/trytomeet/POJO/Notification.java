package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {
    public int purpose;
    public String title;
    public String text;
    public String event_id;


    public Notification(){}
}
