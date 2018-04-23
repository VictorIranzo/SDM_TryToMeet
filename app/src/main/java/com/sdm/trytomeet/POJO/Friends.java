package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Friends {
    public List<String> friends;

    public Friends(){}

    public Friends(List<String> friends){
        this.friends = friends;
    }

    public String toString(){
        return "Friends: " + friends;
    }
}
