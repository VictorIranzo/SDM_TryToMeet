package com.sdm.trytomeet.fragments.Friends;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.LoginActivity;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.adapters.MemberListAdapter;
import com.sdm.trytomeet.fragments.Groups.AddMemberFragmentDialog;
import com.sdm.trytomeet.fragments.Groups.GroupsFragment;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static com.sdm.trytomeet.fragments.Friends.AddFriendFragmentDialog.ADD_FRIEND;

public class FriendsFragment extends Fragment {
    private View parent;
    private static String user_id;

    public static ArrayList<User> friends=new ArrayList<User>();

    public ListView listViewFriends;
    public static MemberListAdapter adapter;

    public static ProgressBar progressBar;

    public FriendsFragment() {
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
        parent = inflater.inflate(R.layout.fragment_members, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id","");

        listViewFriends = parent.findViewById(R.id.list_Members);
        adapter = new MemberListAdapter(getActivity(),R.id.list_groups,friends);
        listViewFriends.setAdapter(adapter);

        progressBar = ((ProgressBar)parent.findViewById(R.id.progressBarMembers));
        progressBar.setIndeterminate(true);

        reloadFriends();




        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.friends_menu, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_member:
                AddFriendFragmentDialog fragment = new AddFriendFragmentDialog();
                // Insert the arguments
                fragment.setCancelable(false);
                fragment.setTargetFragment(this, 0);
                fragment.show(getActivity().getSupportFragmentManager(), "dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FRIEND) {
            String friend_email = data.getStringExtra("friend_id");
            UserFirebaseService.addFriend(user_id, MainActivity.cleanEmail(friend_email), this);
        }
    }
    public void addedFriendSuccessfully(boolean res){
        String message;
        if(res) {
            message = getString(R.string.added_friend_successfull);
        }
        else message =getString(R.string.added_friend_fail);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    public static void reloadFriends() {
        if (friends != null) {
            friends.clear();
            adapter.notifyDataSetChanged();
            FirebaseDatabase.getInstance().getReference().child("friends").child(user_id)
                    .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Friends fri = dataSnapshot.getValue(Friends.class);
                            if (fri != null) {
                                for (String friend : fri.friends) { // For each one of my friends
                                    FirebaseDatabase.getInstance().getReference().child("users").child(friend)
                                            .addListenerForSingleValueEvent(new ValueEventListener() { // Get their name
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    User friend = dataSnapshot.getValue(User.class);

                                                    // Add that friend to the shown list
                                                    friends.add(friend);
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
                                    progressBar.setVisibility(GONE);
                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }
    }
}


