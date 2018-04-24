package com.sdm.trytomeet.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.sdm.trytomeet.R;

public class CheckInternet {

    public static void isNetworkConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info == null || !info.isConnected()){
            Toast.makeText(context, R.string.error_network, Toast.LENGTH_SHORT).show();
        }
    }
}
