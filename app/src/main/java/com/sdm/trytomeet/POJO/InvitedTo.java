package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class InvitedTo {

    public final static String PENDING = "PENDING";
    public final static String VOTED = "VOTED";


    public String state;

    public InvitedTo() {}

    public InvitedTo(String state) {
        this.state = state;
    }
}
