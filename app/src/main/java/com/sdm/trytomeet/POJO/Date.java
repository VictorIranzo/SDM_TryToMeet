package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class Date {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public int votes;
    public List<String> voted_users;


    public Date(){}

    public Date(int year, int month, int day, int hour, int minute){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.voted_users = new ArrayList<String>();
    }

    @Override
    public boolean equals(Object obj) {
        Date aux = (Date) obj;
        return year == aux.year && month == aux.month && day == aux.day
                && hour == aux.hour && minute == aux.minute;
    }

    // TODO: Internacionalización de toString.
    @Override
    public String toString(){
        int votes = 0;
        if(voted_users != null) votes = voted_users.size();
        return day + "/" + month + "/" + year + " " + hour +":" + minute + "\n" + "Votes: " + votes;
    }
}
