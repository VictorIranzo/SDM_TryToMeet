package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class Groups {
    public Map<String, Group> groups; //Group-name --> group

    public Groups(){}

    public Groups(Map<String, Group> groups){
        this.groups = groups;
    }

    public String toString(){
        return groups.toString();
    }
}
