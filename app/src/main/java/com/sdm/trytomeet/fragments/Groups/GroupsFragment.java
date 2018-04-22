package com.sdm.trytomeet.fragments.Groups;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.adapters.GroupsListAdapter;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {
    private View parent;
    private String user_id;

    public ArrayList<Group> groupList;
    public ListView listViewGroups;
    public GroupsListAdapter adapter;

    public ProgressBar progressBar;

    public GroupsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.frg_groups, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

        groupList = new ArrayList<>();
        listViewGroups = parent.findViewById(R.id.list_groups);
        adapter = new GroupsListAdapter(getActivity(),R.id.list_groups,groupList);

        listViewGroups.setAdapter(adapter);

        // Updates the Site selected.
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = ((TextView)view.findViewById(R.id.group_name)).getText().toString();
                MembersFragment fragment = new MembersFragment();
                // Insert the arguments
                Bundle args = new Bundle();
                args.putString("user_id", user_id);
                args.putStringArrayList("group",(new ArrayList<String>(groupList.get(position).members)));
                args.putString("group_name", groupList.get(position).name);
                args.putString("group_identifier", groupList.get(position).uniqueIdentifier);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment).addToBackStack(null).commit();

            }
        });

        progressBar = ((ProgressBar)parent.findViewById(R.id.progressBarGroups));
        progressBar.setIndeterminate(true);

        UserFirebaseService.getGroups(user_id,this);

        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.Groups_title));

        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_event_header_confirm:
                CreateGroupFragment fragment = new CreateGroupFragment();
                // Insert the arguments
                Bundle args = new Bundle();
                args.putString("user_id", user_id);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addGroupsToList(List<Group> groups) {
        progressBar.setVisibility(View.GONE);

        for (Group group: groups) {
            groupList.add(group);
        }
        adapter.notifyDataSetChanged();

    }

}
