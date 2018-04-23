package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.R;

import java.util.ArrayList;

import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Profile.View_external_user;

public class AddParticipantListAdapter extends ArrayAdapter<User> implements Filterable{

    Context context;
    int resource;
    ArrayList<User> data;
    ArrayList<User> to_show;
    private ArrayList<User> to_add;

    public AddParticipantListAdapter(Context context, int resource, ArrayList<User> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
        this.to_add = new ArrayList<User>();
        to_show = data;
    }

    @Override
    public int getCount() {
        return to_show.size();
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.row_create_event_add_participant, null);
        }
        final User user = to_show.get(position);
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
                View_external_user external_user = View_external_user.newInstance(user, false);
                external_user.show(((FragmentActivity)context).getSupportFragmentManager(), "dialog");
            }
        });

        return convertView;
    }

    public ArrayList<User> getToAdd(){
        return to_add;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values
                ArrayList<User> res = new ArrayList<User>();

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = data.size();
                    results.values = data;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < data.size(); i++) {
                        User user = data.get(i);
                        if (user.username.toLowerCase().startsWith(constraint.toString())) {
                            res.add(user);
                        }
                    }
                    // set the Filtered result to return
                    results.count = res.size();
                    results.values = res;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                to_show = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}

























