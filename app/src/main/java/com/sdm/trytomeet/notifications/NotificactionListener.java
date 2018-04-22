package com.sdm.trytomeet.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.facebook.login.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.LoginActivity;
import com.sdm.trytomeet.activities.MainActivity;

public class NotificactionListener implements ValueEventListener{

    Context context;

    public NotificactionListener(Context context){
        this.context = context;
    }

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> it = dataSnapshot.getChildren();
        for(final DataSnapshot not : it){
            FirebaseDatabase.getInstance().getReference().child("notifications")
                    .child(dataSnapshot.getKey()).child(not.getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotNotification) {
                            com.sdm.trytomeet.POJO.Notification notification = dataSnapshotNotification.getValue(com.sdm.trytomeet.POJO.Notification.class);
                            if(notification == null) return;

                            // Show the notification
                            NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);
                            mbuilder.setContentTitle(notification.title);
                            mbuilder.setContentText(notification.text);
                            mbuilder.setSmallIcon(R.drawable.ic_launcher_foreground);

                            Intent intent = null;

                            // Set up the behaviour
                            switch (notification.purpose){
                                case com.sdm.trytomeet.POJO.Notification.ADDED_TO_AN_EVENT:
                                case com.sdm.trytomeet.POJO.Notification.EVENT_CONFIRMATE:
                                case com.sdm.trytomeet.POJO.Notification.COMMENT_ADDED:
                                case com.sdm.trytomeet.POJO.Notification.IMAGE_UPLOADED:
                                    intent = new Intent(context, LoginActivity.class);
                                    intent.putExtra("action",notification.purpose);
                                    intent.putExtra("event_id", notification.event_id);
                                    break;
                                case com.sdm.trytomeet.POJO.Notification.ADDED_TO_A_GROUP: // ADDED TO A GROUP
                                    intent = new Intent(context, LoginActivity.class);
                                    intent.putExtra("action",notification.purpose);
                                    intent.putExtra("group_id", notification.group_id);
                                    break;
                            }

                            intent.putExtra("id", notification.id);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            mbuilder.setContentIntent(pendingIntent);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(notification.id, 0, mbuilder.build());

                            // Remove it
                            //FirebaseDatabase.getInstance().getReference().child("notifications")
                              //.child(dataSnapshot.getKey()).child(not.getKey()).removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
