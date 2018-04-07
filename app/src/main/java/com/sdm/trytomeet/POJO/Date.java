package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

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

    public Date(){}

    public Date(int year, int month, int day, int hour, int minute){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public boolean equals(Object obj) {
        Date aux = (Date) obj;
        return year == aux.year && month == aux.month && day == aux.day
                && hour == aux.hour && minute == aux.minute;
    }
}
