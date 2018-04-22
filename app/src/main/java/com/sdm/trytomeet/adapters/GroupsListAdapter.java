package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.R;

import java.util.ArrayList;

/**
 * Created by adrymc96 on 30/03/18.
 */

public class GroupsListAdapter extends ArrayAdapter<Group>{

    Context context;
    int resource;
    ArrayList<Group> data;

    public GroupsListAdapter(Context context, int resource, ArrayList<Group> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.row_groups, null);
        }
        final Group group = data.get(position);
        ((TextView) convertView.findViewById(R.id.group_name)).setText(group.name);


        return convertView;
    }
}

























