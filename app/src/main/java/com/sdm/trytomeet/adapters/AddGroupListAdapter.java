package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
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

/**
 * Created by adrymc96 on 30/03/18.
 */

public class AddGroupListAdapter extends ArrayAdapter<Group> implements Filterable{

    Context context;
    int resource;
    ArrayList<Group> data;
    ArrayList<Group> to_show;
    private ArrayList<Group> to_add;

    public AddGroupListAdapter(Context context, int resource, ArrayList<Group> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
        to_show = data;
        this.to_add = new ArrayList<Group>();
    }

    @Override
    public int getCount() {
        return to_show.size();
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.row_create_event_add_group, null);
        }
        final Group group = to_show.get(position);
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

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values
                ArrayList<Group> res = new ArrayList<Group>();

                //If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                //else does the Filtering and returns FilteredArrList(Filtered)

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = data.size();
                    results.values = data;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < data.size(); i++) {
                        Group group = data.get(i);
                        if (group.name.toLowerCase().startsWith(constraint.toString())) {
                            res.add(group);
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
                to_show = (ArrayList<Group>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}

























