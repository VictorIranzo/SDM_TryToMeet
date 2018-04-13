package com.sdm.trytomeet.fragments.Groups;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.AddParticipantListAdapter;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;

import static android.view.View.GONE;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class AddMemberFragmentDialog extends DialogFragment {

    private View parent;
    private String user_id;
    private ArrayList<User> my_friends;
    private ArrayList<String> already_on_group;
    private String group_name;
    private String group_identifier;
    private ListView list_view;
    private TextView noFriends;
    private ProgressBar progressBar;


    public AddMemberFragmentDialog() {
        // Required empty public constructor
    }

    public static AddMemberFragmentDialog newInstance(String user_id){
        AddMemberFragmentDialog res = new AddMemberFragmentDialog();

        // Insert the argument
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
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
        already_on_group= getArguments().getStringArrayList("group");
        group_identifier= getArguments().getString("group_identifier");
        group_name= getArguments().getString("group_name");


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
        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_remove_friend_dialog, null);

        // We configure the list view
        my_friends = new ArrayList<>();
        list_view = parent.findViewById(R.id.list_view_removeFriend);
        noFriends = parent.findViewById(R.id.textViewRemoveFriend);
        progressBar = parent.findViewById(R.id.remove_friend_progress_bar);
        final AddParticipantListAdapter adapter = new AddParticipantListAdapter(getContext(), R.id.list_view, my_friends);
        list_view.setAdapter(adapter);

        // TODO: FILTER BY FRIENDS NAME


        // We populate the friends list
        FirebaseDatabase.getInstance().getReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Friends friends = dataSnapshot.getValue(Friends.class);
                        if(friends != null){
                            for(String friend : friends.friends){
                                if(already_on_group.contains(friend)) continue; // If that friend has been added skip it// For each one of my friends
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(already_on_group.containsAll(friends.friends)){
                                     noFriends.setText(getResources().getString(R.string.noFriendsToAddToGroup));}
                                    progressBar.setVisibility(GONE);
                                }
                            });



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!my_friends.isEmpty()) {

                    Group group = new Group(already_on_group, group_name);
                    group.uniqueIdentifier = group_identifier;
                    for (User f : my_friends) {
                        UserFirebaseService.addFriendToGroup(f.id, group);

                    }
                    getActivity().getIntent().putExtra("new",my_friends);
                    getTargetFragment().onActivityResult(1, Activity.RESULT_OK, getActivity().getIntent());
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());

            }
        });
        return builder.create();
    }
}
