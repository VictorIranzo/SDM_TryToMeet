package com.sdm.trytomeet.notifications;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.activities.MainActivity;

public class NotificationService extends Service {

    NotificactionListener listener;
    String account_id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        listener = new NotificactionListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        account_id = prefs.getString("account_id", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(account_id.equals("")){
            stopSelf();
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("notifications")
                    .child(account_id).addValueEventListener(listener);
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(!account_id.equals("")){
            FirebaseDatabase.getInstance().getReference().child("notifications")
                    .child(account_id).removeEventListener(listener);
        }
        super.onDestroy();
    }
}
