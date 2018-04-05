package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sdm.trytomeet.R;

import java.util.ArrayList;

import POJO.User;

/**
 * Created by adrymc96 on 30/03/18.
 */

public class CreateEventParticipantListAdapter extends ArrayAdapter<User>{

    Context context;
    int resource;
    ArrayList<User> data;

    public CreateEventParticipantListAdapter(Context context, int resource, ArrayList<User> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.create_event_participant_layout, null);
        }
        final User user = data.get(position);
        ((TextView) convertView.findViewById(R.id.username)).setText(user.username);
        ((Button) convertView.findViewById(R.id.delete_participant_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(user);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}

























