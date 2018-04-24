package com.sdm.trytomeet.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Event {
    public String name;
    public String description;
    public Site site;
    public List<Date> possible_dates;
    public List<String> participants_id;
    public HashMap<String,Comment> comments;
    public String creator_id;
    public String state;
    public String image;
    public HashMap<String, String> images;
    public Date confirmed_date;

    // With this static properties the state of the event is set.
    // This is used to centralized the possible states of an event and
    // because Firebase doesn't works well with enums.
    public final static String PENDING = "PENDING";
    public final static String VOTED = "VOTED";
    public final static String CONFIRMED = "CONFIRMED";
    public final static String DONE = "DONE";
    public final static String CANCELED = "CANCELED";

    public Event(){}

    public Event(String name, String description, List<Date> possible_dates, String state, String image, Date confirmed_date) {
        this.name = name;
        this.description = description;
        this.possible_dates = possible_dates;
        this.state = state;
        this.image = image;
        this.confirmed_date = confirmed_date;
    }

    // This method should be properly adapted when using it
    public Event(String name, String description, List<Date> possible_dates,
                 List<String> participants_id, String creator_id, String state, Site site){
        this.name = name;
        this.description = description;
        this.possible_dates = possible_dates;
        this.participants_id = participants_id;
        this.creator_id = creator_id;
        this.state = state;
        this.site = site;
        this.comments = new HashMap<String,Comment>();
    }

    public Event(String name, String description, Site site, List<Date> possible_dates, List<String> participants_id, String creator_id, String state, Date confirmed_date) {
        this.name = name;
        this.description = description;
        this.site = site;
        this.possible_dates = possible_dates;
        this.participants_id = participants_id;
        this.creator_id = creator_id;
        this.state = state;
        this.confirmed_date = confirmed_date;
    }

    public boolean allVoted() {
        ArrayList<String> users = new ArrayList<String>();
        for (Date date : possible_dates) {
            if (date.voted_users != null) {
                for (String user : date.voted_users) {
                    if (!users.contains(user)) users.add(user);
                }
            }
        }

        if (users.size() == participants_id.size()) return true;
        else return false;

    }

    public String getPossibleDatesResume(){
        String result = "";

        if(possible_dates == null) return result;

        for (Date date: possible_dates) {
            result += date.toString() + '\n';
        }

        return result;
    }

    public int posPastDate(Date d){
        int size = 0;
        for (Date date: possible_dates){
            if (date.equals(d)){
                return size;
            }
            size ++;
        }
        return size;
    }
    public String toStringPos(int i){
        String res= ""+i;
        return  res;
    }
}
