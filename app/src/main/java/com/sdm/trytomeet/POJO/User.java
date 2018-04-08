package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User {
    public String id;
    public String username;
    public String url;
    public List<Site> favouriteSites;

    public User(){}

    public String toString(){
        return "Username: " + username + ", url: " + url;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((User) obj).id);
    }
}
