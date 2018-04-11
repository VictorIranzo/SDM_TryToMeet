package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class User  implements Serializable{
    public String id;
    public String username;
    public String image;
    public List<Site> favouriteSites;

    public User(){}

    public User(String username, String image) {
        this.username = username;
        this.image = image;
    }

    public String toString(){
        return "Username: " + username;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((User) obj).id);
    }
}
