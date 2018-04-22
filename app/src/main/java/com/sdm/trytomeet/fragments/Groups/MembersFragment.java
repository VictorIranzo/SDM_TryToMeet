package com.sdm.trytomeet.fragments.Groups;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.MemberListAdapter;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MembersFragment extends Fragment {
    private View parent;
    private String user_id;

    public ArrayList<User> members = new ArrayList<>();
    private ArrayList<String> ids;
    private String group_name;
    private String group_identifier;
    public ListView listViewMembers;
    public MemberListAdapter adapter;
    public static final int ADD_MEMBER=1;

    public ProgressBar progressBar;

    public MembersFragment() {
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
        parent = inflater.inflate(R.layout.frg_members, container, false);
        user_id = getArguments().getString("user_id");
        group_name = getArguments().getString("group_name");
        group_identifier = getArguments().getString("group_identifier");


        listViewMembers = parent.findViewById(R.id.list_Members);
        adapter = new MemberListAdapter(getActivity(),R.id.list_groups,members);


        ids= getArguments().getStringArrayList("group");
        UserFirebaseService.getUsersFromMembersOfGroup(ids,this);

        listViewMembers.setAdapter(adapter);

        progressBar = ((ProgressBar)parent.findViewById(R.id.progressBarMembers));
        progressBar.setIndeterminate(true);

        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.members_menu, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_member:
                AddMemberFragmentDialog fragment = new AddMemberFragmentDialog();
                // Insert the arguments
                fragment.setCancelable(false);
                fragment.setTargetFragment(this,0);
                Bundle args = new Bundle();
                args.putString("user_id", user_id);
                args.putStringArrayList("group", ids);
                args.putString("group_name",group_name);
                args.putString("group_identifier",group_identifier);
                fragment.setArguments(args);
                fragment.show(getActivity().getSupportFragmentManager(), "dialog");
                break;

            case R.id.exit_group:
                AlertDialog ad = new AlertDialog.Builder(getActivity())
                        .create();
                ad.setCancelable(false);
                ad.setTitle(getString(R.string.exit_group_title));
                ad.setMessage(getString(R.string.exit_group_message));
                ad.setButton(Dialog.BUTTON_POSITIVE,getString(R.string.ok),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Group group= new Group(ids,group_name);
                        group.uniqueIdentifier=group_identifier;
                        UserFirebaseService.exitFromGroup(user_id,group);

                        GroupsFragment fragment = new GroupsFragment();
                        // Insert the arguments
                        Bundle args = new Bundle();
                        args.putString("user_id", user_id);
                        fragment.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, fragment).commit();
                    }
                });
                ad.setButton(Dialog.BUTTON_NEGATIVE,getString(R.string.cancel),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                ad.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addMemberToList(User member){
        members.add(member);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(GONE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ADD_MEMBER:

                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(getActivity(), getResources().getString(R.string.member_added), Toast.LENGTH_SHORT).show();
                    ArrayList<User> newMembers = new ArrayList<User>((List <User>)data.getSerializableExtra("new"));
                    members.addAll(newMembers);
                    adapter.notifyDataSetChanged();

                }

                 else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }

                break;
        }
    }
}


