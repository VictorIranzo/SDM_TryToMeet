package com.sdm.trytomeet.fragments.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.components.CircularImageView;

public class View_external_user extends DialogFragment {

    private User user;
    private View view;

    public View_external_user() {
        // Required empty public constructor
    }

    public static View_external_user newInstance(User user) {
        View_external_user fragment = new View_external_user();

        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable("user");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String user_id = prefs.getString("account_id","");
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

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_view_external_user, null);
        builder.setView(view);

        if(user.image != null){
            CircularImageView userImage = view.findViewById(R.id.user_image);
            Glide.with(this).load(user.image).into(userImage);
        }

        ((TextView) view.findViewById(R.id.username)).setText(user.username);

        final Button add_as_a_friend = view.findViewById(R.id.add_as_a_friend);

        FirebaseDatabase.getInstance().getReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(!friends.friends.contains(user.id) && !user_id.equals(user.id)){
                    add_as_a_friend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        add_as_a_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("friends").child(user_id)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Friends friends = mutableData.getValue(Friends.class);
                                friends.friends.add(user.id);
                                mutableData.setValue(friends);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });

                dismiss();
            }
        });


        return builder.create();
    }
}
