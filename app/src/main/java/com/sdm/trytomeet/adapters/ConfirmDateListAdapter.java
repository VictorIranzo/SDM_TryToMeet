package com.sdm.trytomeet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.fragments.Events.ConfirmEventFragment;
import com.sdm.trytomeet.fragments.Events.EventListFragment;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.ArrayList;
import java.util.List;

public class ConfirmDateListAdapter extends ArrayAdapter<Date> {
    public List<Date> data;
    Context context;
    int resource;
    String event_id;
    FragmentManager fragmentManager;
    ConfirmEventFragment confirmFragment;
    String user_id;
    List<String> participants;
    String event_name;


    public ConfirmDateListAdapter(Context context, int resource, ArrayList<Date> data, String event , FragmentManager fragment,  ConfirmEventFragment confirmFragment, String user_id, List<String> participants, String event_name){
        super(context, resource, data);
        this.fragmentManager= fragment;
        this.confirmFragment=confirmFragment;
        this.data = data;
        this.event_id= event;
        this.resource = resource;
        this.context = context;
        this.user_id = user_id;
        this.participants = participants;
        this.event_name = event_name;

    }
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.confirmate_date_layout, null);
        }
        final Date date = data.get(position);

        ((TextView) convertView.findViewById(R.id.date)).setText(date.toString());
        ((Button) convertView.findViewById(R.id.confirm_date)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                EventFirebaseService.confirmateEvent(event_id,date, user_id, participants,
                        context.getResources().getString(R.string.event_confirmate_notification_title),
                        context.getResources().getString(R.string.event_confirmate_notification_text,event_name));
                EventListFragment fragment = new EventListFragment();
                // Insert the arguments
                Bundle args = new Bundle();
                args.putString("user_id", settings.getString("account_id",""));
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, fragment).commit();
                confirmFragment.finish();

            }
        });

        return convertView;
    }
    }
