package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class TakingPart {
    public Map<String,InvitedTo> invitedTo;

    public TakingPart(){}
}