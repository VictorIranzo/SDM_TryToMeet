package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrymc96 on 30/03/18.
 */

public class AddGroupListAdapter extends ArrayAdapter<Group>{

    Context context;
    int resource;
    ArrayList<Group> data;
    private ArrayList<Group> to_add;

    public AddGroupListAdapter(Context context, int resource, ArrayList<Group> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
        this.to_add = new ArrayList<Group>();
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.add_group_layout, null);
        }
        final Group group = data.get(position);
        ((TextView) convertView.findViewById(R.id.group_name)).setText(group.name);
        ((CheckBox) convertView.findViewById(R.id.checkBox_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    to_add.add(group);
                }
                else{
                    to_add.remove(group);
                }
            }
        });
        final ListView listView = ((ListView) convertView.findViewById(R.id.list_view));

        // Populate the group's member list
        final ArrayList<User> members = new ArrayList<>();
        final MemberListAdapter adapter = new MemberListAdapter(getContext(), R.id.list_view, members);
        listView.setAdapter(adapter);
        for(String member : group.members){
            FirebaseDatabase.getInstance().getReference().child("users").child(member)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    members.add(user);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        ((ImageButton) convertView.findViewById(R.id.dropdown_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listView.getVisibility() == View.GONE) listView.setVisibility(View.VISIBLE);
                else listView.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    public ArrayList<Group> getToAdd(){
        return to_add;
    }
}

























