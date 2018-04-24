package com.sdm.trytomeet.persistence.server;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdm.trytomeet.POJO.Comment;
import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.InvitedTo;
import com.sdm.trytomeet.POJO.Notification;
import com.sdm.trytomeet.POJO.TakingPart;
import com.sdm.trytomeet.fragments.Events.EventFragment;
import com.sdm.trytomeet.fragments.Events.EventListFragment;
import com.sdm.trytomeet.fragments.Events.HistoricEventListFragment;
import com.sdm.trytomeet.fragments.Events.PendingVoteEventListFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventFirebaseService extends FirebaseService{

    private static ArrayList<Event> name = new ArrayList<Event>();
    final String names = new String();
    final java.util.Date d = new java.util.Date();


    public static String addEvent(final Event event, Uri image){
        final String key = getDatabaseReference().child("events").push().getKey();

        StorageReference image_key = getStorageReference().child("images").child(image.getLastPathSegment());
        image_key.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                event.image = taskSnapshot.getDownloadUrl().toString();
                getDatabaseReference().child("events").child(key).setValue(event);
            }
        });

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
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if(event == null) return Transaction.success(mutableData);
                for (Date d: event.possible_dates) {
                    if(d.equals(date)){
                        if(d.voted_users == null) d.voted_users = new ArrayList<String>();
                        d.voted_users.add(user_id);

                        EventFirebaseService.SetTakingPart(event_id,user_id,InvitedTo.VOTED);

                        break;
                    }
                }
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void SetTakingPart(String event_id, String user_id, final String state) {
        getDatabaseReference().child("taking_part").child(user_id).child("invitedTo").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                InvitedTo invitedTo = mutableData.getValue(InvitedTo.class);
                invitedTo.state = state;
                mutableData.setValue(invitedTo);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void removeVote(final String event_id, final String user_id, final Date date){
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if(event == null) return Transaction.success(mutableData);
                for (Date d: event.possible_dates) {
                    if(d.equals(date)){
                        d.voted_users.remove(user_id);
                        break;
                    }
                }
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void AddComment(Comment c, String event_id, String user_id, List<String> participants, String title, String text){
        String key = getDatabaseReference().child("events").child(event_id).child("comments").push().getKey();
        getDatabaseReference().child("events").child(event_id).child("comments").child(key).setValue(c);

        // Notify the other users
        for(String user : participants){
            if(!user.equals(user_id));
                Notification not = new Notification(Notification.COMMENT_ADDED,title, text);
                not.event_id = event_id;
                NotificationFirebaseService.addNotification(not, user);
        }
    }

    public static void confirmateEvent(final String event_id, final Date date, String user_id, List<String> participant, String title, String text){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                e.state=Event.CONFIRMED;
                e.confirmed_date= date;
                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });

        // Notify the others users
        for(String user : participant){
            if(!user.equals(user_id)){
                Notification not = new Notification(Notification.EVENT_CONFIRMATE, title, text);
                not.event_id = event_id;
                NotificationFirebaseService.addNotification(not, user);
            }
        }

    }

    public static void cancelEvent(final String event_id) {
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                e.state= Event.CANCELED;

                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void editEventDescription(final String event_id, final String description){
        getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                e.description= description;
                getDatabaseReference().child("events").child(event_id).setValue(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });

    }
    public static void setEventImage(final String event_id, final Uri image) {
        StorageReference path = getStorageReference().child("images").child("events").child(image.getLastPathSegment());
        path.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                getDatabaseReference().child("events").child(event_id).child("image").setValue(taskSnapshot.getDownloadUrl().toString());

            }
        });
    }


    public static void uploadImage(final String event_id, Uri selectedImage, ArrayList<String> participants, String user_id, String title, String text) {
        final StorageReference path = getStorageReference().child("images").child("event_images").child(selectedImage.getLastPathSegment());
        path.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String key = getDatabaseReference().child("events").child(event_id).child("images").push().getKey();
                getDatabaseReference().child("events").child(event_id).child("images").child(key).setValue(taskSnapshot.getDownloadUrl().toString());
            }
        });

        // Notify users
        for(String user : participants){
            if(!user_id.equals(user)){
                Notification not = new Notification(Notification.IMAGE_UPLOADED, title, text);
                not.event_id = event_id;
                NotificationFirebaseService.addNotification(not, user);
            }
        }
    }


    public static void addImageListener(String event_id, ChildEventListener listener) {
        FirebaseDatabase.getInstance().getReference().child("events").child(event_id)
                .child("images").addChildEventListener(listener);

    }

    public static void removeImageListener(String event_id, ChildEventListener listener) {
        FirebaseDatabase.getInstance().getReference().child("events").child(event_id)
                .child("images").removeEventListener(listener);

    }

    public static void getUserEvents(final String user_id, final EventListFragment eventListFragment) {
        getDatabaseReference().child("taking_part").child(user_id)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TakingPart takingPart = dataSnapshot.getValue(TakingPart.class);
                        if (takingPart != null && takingPart.invitedTo != null) {
                            for (String event_id : takingPart.invitedTo.keySet()) {
                                // We check that the event state is PENDING, the user has to vote there.
                                if (takingPart.invitedTo.get(event_id).state.equals(InvitedTo.VOTED)) {
                                    getDatabaseReference().child("events").
                                            child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Event e = dataSnapshot.getValue(Event.class);
                                            // In this way, deleted events are added to the list.
                                            if (e != null)
                                                eventListFragment.addEventToList(dataSnapshot.getKey(), e);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("Error", "Something bad");
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void getUserEventsHistoric(final String user_id, final HistoricEventListFragment eventListFragment) {
        getDatabaseReference().child("taking_part").child(user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TakingPart takingPart = dataSnapshot.getValue(TakingPart.class);
                        if(takingPart == null || takingPart.invitedTo == null) return;
                        for (String event_id : takingPart.invitedTo.keySet()) {
                            // We check that the event state is PENDING, the user has to vote there.
                            getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Event e = dataSnapshot.getValue(Event.class);

                                    // In this way, deleted events are added to the list.
                                    if (e != null)
                                        eventListFragment.addEventToList(dataSnapshot.getKey(), e);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Error", "Something bad");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void getUserEventsPendingVote(final String user_id, final PendingVoteEventListFragment eventListFragment) {
        getDatabaseReference().child("taking_part").child(user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TakingPart takingPart = dataSnapshot.getValue(TakingPart.class);
                        if(takingPart == null || takingPart.invitedTo == null) return;
                        for (String event_id : takingPart.invitedTo.keySet()) {
                            // We check that the event state is PENDING, the user has to vote there.
                            if(takingPart.invitedTo.get(event_id).state.equals(InvitedTo.PENDING)) {
                                getDatabaseReference().child("events").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Event e = dataSnapshot.getValue(Event.class);

                                        // In this way, deleted events are added to the list.
                                        if (e != null)
                                            eventListFragment.addEventToList(dataSnapshot.getKey(), e);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("Error", "Something bad");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void deleteTakingPart(String user_id, String event_id) {
        getDatabaseReference().child("taking_part").child(user_id).child("invitedTo").child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteParticipant(final String user_id, String event_id) {
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);

                if(event == null) return Transaction.success(mutableData);

                event.participants_id.remove(user_id);
                for (Date date: event.possible_dates) {
                    if(date == null) continue;
                    if(date.voted_users != null) date.voted_users.remove(user_id);
                }

                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void removePastVotedDate(String event_id, final Date date) {
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);

                if(event == null) return Transaction.success(mutableData);

                event.possible_dates.remove(date);

                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void passEventConfirmedToDone(String event_id) {
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);

                if(event == null) return Transaction.success(mutableData);

                event.state = Event.DONE;

                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void passEventPendingToCancel(String event_id) {
        getDatabaseReference().child("events").child(event_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);

                if(event == null) return Transaction.success(mutableData);

                event.state = Event.CANCELED;

                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
