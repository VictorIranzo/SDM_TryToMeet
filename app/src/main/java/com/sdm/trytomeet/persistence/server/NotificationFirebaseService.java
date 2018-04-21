package com.sdm.trytomeet.persistence.server;

import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.POJO.Notification;

public class NotificationFirebaseService extends FirebaseService {

    public static String addNotification(Notification notification, String user_id){
        String notification_key = getDatabaseReference().child("notifications").child(user_id).push().getKey();
        getDatabaseReference().child("notifications").child(user_id).child(notification_key).setValue(notification);

        return notification_key;
    }


}
