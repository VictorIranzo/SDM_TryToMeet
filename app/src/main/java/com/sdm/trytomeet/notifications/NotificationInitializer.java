package com.sdm.trytomeet.notifications;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationInitializer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // We start the notification reading service
        Intent service = new Intent(context, NotificationService.class);
        context.startService(service);
    }
}
