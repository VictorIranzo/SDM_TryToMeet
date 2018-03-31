package com.sdm.trytomeet.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.add_participant_list_adapter;

import java.util.ArrayList;

import POJO.Date;
import POJO.Friends;
import POJO.User;

public class Fragment_add_date_dialog extends DialogFragment {

    private View parent;


    public Fragment_add_date_dialog() {
        // Required empty public constructor
    }

    static Fragment_add_date_dialog newInstance(){
        Fragment_add_date_dialog res = new Fragment_add_date_dialog();

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

        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_date_dialog, null);

        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker date = parent.findViewById(R.id.datePicker);
                TimePicker time = parent.findViewById(R.id.timePicker);
                Date date_to_add = new Date(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                        time.getCurrentHour(), time.getCurrentMinute());
                ((Fragment_create_event)getTargetFragment()).add_date(date_to_add);
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
