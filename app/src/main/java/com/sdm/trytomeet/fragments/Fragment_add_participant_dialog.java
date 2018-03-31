package com.sdm.trytomeet.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.add_participant_list_adapter;

import java.util.ArrayList;
import java.util.List;

import POJO.Friends;
import POJO.User;

public class Fragment_add_participant_dialog extends DialogFragment {

    private View parent;
    private String user_id;
    private ArrayList<User> my_friends;
    private ArrayList<String> current_participants;
    private ListView list_view;


    public Fragment_add_participant_dialog() {
        // Required empty public constructor
    }

    static Fragment_add_participant_dialog newInstance(String user_id, ArrayList<String> current_participants){
        Fragment_add_participant_dialog res = new Fragment_add_participant_dialog();

        // Insert the argument
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putSerializable("current_participants", current_participants);
        res.setArguments(args);

        return res;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        user_id = getArguments().getString("user_id");
        current_participants = (ArrayList<String>) getArguments().getSerializable("current_participants");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    // Since the dialog is set as not cancelable, but we still want the normal behaviour for the back button
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_participant_dialog, null);

        // We configure the list view
        my_friends = new ArrayList<>();
        list_view = parent.findViewById(R.id.list_view);
        final add_participant_list_adapter adapter = new add_participant_list_adapter(getContext(), R.id.list_view, my_friends);
        list_view.setAdapter(adapter);

        // TODO: FILTER BY FRIENDS NAME


        // We populate the friends list
        FirebaseDatabase.getInstance().getReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        if(friends != null){
                            for(String friend : friends.friends){ // For each one of my friends
                                if(current_participants.contains(friend)) continue; // If that friend has been added skip it
                                FirebaseDatabase.getInstance().getReference().child("users").child(friend)
                                        .addListenerForSingleValueEvent(new ValueEventListener() { // Get their name
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User friend = dataSnapshot.getValue(User.class);
                                                // Add that friend to the shown list
                                                my_friends.add(friend);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Fragment_create_event)getTargetFragment()).add_participants(adapter.getToAdd());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
