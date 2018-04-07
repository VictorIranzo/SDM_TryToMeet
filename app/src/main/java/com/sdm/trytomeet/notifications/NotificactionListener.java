package com.sdm.trytomeet.notifications;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.R;

import com.sdm.trytomeet.POJO.Notification;

public class NotificactionListener implements ValueEventListener{

    public static final int ADDED_TO_AN_EVENT = 1;
    int id;
    Context context;

    public NotificactionListener(Context context){
        this.context = context;
        id = 0;
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
                            Notification notification = dataSnapshotNotification.getValue(Notification.class);
                            if(notification == null) return;

                            // Show the notification
                            switch (notification.purpose){
                                case 1:
                                    NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);
                                    mbuilder.setContentTitle(notification.title);
                                    mbuilder.setContentText(notification.text);
                                    mbuilder.setSmallIcon(R.drawable.cast_ic_notification_play);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(id++, mbuilder.build());


                                    // TODO: AÑADIR QUE TE LLEVE A LA VENTANA DE LAS NOTIFICACIONES
                                    break;
                            }

                            // Remove it
                            FirebaseDatabase.getInstance().getReference().child("notifications")
                              .child(dataSnapshot.getKey()).child(not.getKey()).removeValue();
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
