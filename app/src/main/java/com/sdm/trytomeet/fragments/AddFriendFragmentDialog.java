package com.sdm.trytomeet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.persistence.server.FirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

/**
 * Created by Adrian on 09/04/2018.
 */

public class AddFriendFragmentDialog  extends DialogFragment {

    private View parent;
    private static String user_id;
    public static final int ADD_FRIEND= 2;


    public AddFriendFragmentDialog() {
        // Required empty public constructor
    }

    static AddFriendFragmentDialog newInstance(String user){
        user_id= user;
        AddFriendFragmentDialog res = new AddFriendFragmentDialog();
        return res;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_friend_dialog, null);
        final EditText editText= parent.findViewById(R.id.editTextAddFriend);
        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getIntent().putExtra("friend_id",editText.getText().toString());
                getTargetFragment().onActivityResult(ADD_FRIEND, Activity.RESULT_OK, getActivity().getIntent());

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
