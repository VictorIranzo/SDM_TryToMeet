package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class User {
    public String id;
    public String username;
    public String url;

    public User(){}

    public String toString(){
        return "Username: " + username + ", url: " + url;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((User) obj).id);
    }
}
