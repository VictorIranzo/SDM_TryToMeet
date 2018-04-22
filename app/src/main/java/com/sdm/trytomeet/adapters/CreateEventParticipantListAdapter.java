package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.R;

import java.util.ArrayList;

import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Profile.View_external_user;

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
                    .inflate(R.layout.row_create_event_participant_added, null);
        }
        final User user = data.get(position);
        if(user.image != null){
            CircularImageView imageView = (CircularImageView) convertView.findViewById(R.id.user_image);
            Glide.with(context).load(user.image).into(imageView);
        }
        ((TextView) convertView.findViewById(R.id.username)).setText(user.username);
        ((ImageButton) convertView.findViewById(R.id.delete_participant_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(user);
                notifyDataSetChanged();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View_external_user external_user = View_external_user.newInstance(user, false);
                external_user.show(((FragmentActivity)context).getSupportFragmentManager(), "dialog");
            }
        });

        return convertView;
    }
}

























