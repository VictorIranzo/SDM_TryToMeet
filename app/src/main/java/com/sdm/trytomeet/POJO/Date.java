package com.sdm.trytomeet.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Date{
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

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

    // <0 if date1 < date2
    // =0 if date1 = date2
    // >0 if date1 > date2
    public int compareTo(Date date){
        if (this.year != date.year)
            return (this.year - date.year);
        if (this.month != date.month)
            return (this.month - date.month);
        if (this.day != date.day)
            return (this.day - date.day);
        if (this.hour != date.hour)
            return (this.hour - date.hour);
        return (this.minute - date.minute);
    }

    @Override
    public String toString(){
        int votes = 0;
        if(voted_users != null) votes = voted_users.size();
        String res = day + "/" + month + "/" + year + " " + hour +":";
        if(minute < 10) res += "0";
        res+= minute + "\n" + "Votes: " + votes;
        return res;
    }
}
