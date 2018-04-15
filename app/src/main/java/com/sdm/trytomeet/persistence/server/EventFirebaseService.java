package com.sdm.trytomeet.persistence.server;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Comment;
import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.InvitedTo;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.fragments.Events.EventFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventFirebaseService extends FirebaseService{
    public static String addEvent(Event event){
        String key = getDatabaseReference().child("events").push().getKey();
        getDatabaseReference().child("events").child(key).setValue(event);

        return key;
    }

    public static void addParticipantToEvent(InvitedTo invitation, String participant_id,  String event_id){
        getDatabaseReference().child("taking_part").child(participant_id).child("invitedTo").child(event_id).setValue(invitation);
    }

    public static void getEvent(String event_id, final EventFragment eventFragment){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Event e = dataSnapshot.getValue(Event.class);
                    eventFragment.setUpEventView(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void addVote(final String event_id, final String user_id, final Date date){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                for (Date d: e.possible_dates) {
                    if(d.equals(date)){
                        if(d.voted_users == null) d.voted_users = new ArrayList<String>();
                        d.voted_users.add(user_id);
                        break;
                    }
                }
                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void removeVote(final String event_id, final String user_id, final Date date){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                for (Date d: e.possible_dates) {
                    if(d.equals(date)){
                        d.voted_users.remove(user_id);
                        break;
                    }
                }
                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void AddComment(Comment c, String event_id){
        String key = getDatabaseReference().child("events").child(event_id).child("comments").push().getKey();
        getDatabaseReference().child("events").child(event_id).child("comments").child(key).setValue(c);
    }

    public static void confirmateEvent(final String event_id){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                e.state="CONFIRMED";
                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }
}
