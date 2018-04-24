package com.sdm.trytomeet.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sdm.trytomeet.persistence.server.NotificationFirebaseService;

public class DismissNotification extends AppCompatActivity {

    // Launched when the user swipe away a notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user_id = prefs.getString("account_id","");
        NotificationFirebaseService.removeNotification(user_id,
                getIntent().getStringExtra("id"));
        finish();
    }
}
