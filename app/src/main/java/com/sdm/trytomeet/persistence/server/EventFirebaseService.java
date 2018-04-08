package com.sdm.trytomeet.persistence.server;

import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.InvitedTo;

public class EventFirebaseService extends FirebaseService{
    public static String addEvent(Event event){
        String key = getDatabaseReference().child("events").push().getKey();
        getDatabaseReference().child("events").child(key).setValue(event);

        return key;
    }

    public static void addParticipantToEvent(InvitedTo invitation, String participant_id,  String event_id){
        getDatabaseReference().child("taking_part").child(participant_id).child("invitedTo").child(event_id).setValue(invitation);
    }
}
