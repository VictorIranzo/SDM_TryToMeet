package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class AddParticipantListAdapter extends ArrayAdapter<User>{

    Context context;
    int resource;
    ArrayList<User> data;
    private ArrayList<User> to_add;

    public AddParticipantListAdapter(Context context, int resource, ArrayList<User> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
        this.to_add = new ArrayList<User>();
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.add_participant_friend_layout, null);
        }
        final User user = data.get(position);
        if(user.image != null){
            CircularImageView imageView = (CircularImageView) convertView.findViewById(R.id.user_image);
            Glide.with(context).load(user.image).into(imageView);
        }
        ((TextView) convertView.findViewById(R.id.username)).setText(user.username);
        ((CheckBox) convertView.findViewById(R.id.checkBox_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    to_add.add(user);
                }
                else{
                    to_add.remove(user);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View_external_user external_user = View_external_user.newInstance(user);
                external_user.show(((FragmentActivity)context).getSupportFragmentManager(), "dialog");
            }
        });

        return convertView;
    }

    public ArrayList<User> getToAdd(){
        return to_add;
    }
}

























