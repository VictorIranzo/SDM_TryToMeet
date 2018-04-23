package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Group {
    public List<String> members;
    public String name;
    public String uniqueIdentifier;

    public Group(){}

    public Group(List<String> members, String name){
        this.members = members;this.name=name;
    }

    public String toString(){
        return "Members: " + members;
    }
}
