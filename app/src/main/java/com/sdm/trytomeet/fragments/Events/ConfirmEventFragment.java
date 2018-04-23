package com.sdm.trytomeet.fragments.Events;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.ConfirmDateListAdapter;

import java.util.ArrayList;

public class ConfirmEventFragment extends DialogFragment {

    private View parent;
    private ArrayList<Date> dates;
    private String event_id;
    private String user_id;
    private ArrayList<String> participants;
    private String event_name;


    public ConfirmEventFragment() {
        // Required empty public constructor
    }

    static ConfirmEventFragment newInstance(ArrayList<Date> possible_dates, String event, String user_id, ArrayList<String> participants, String event_name){
        ConfirmEventFragment res = new ConfirmEventFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dates", possible_dates);
        bundle.putString("event_id", event);
        bundle.putString("user_id", user_id);
        bundle.putStringArrayList("participants", participants);
        bundle.putString("event_name", event_name);
        res.setArguments(bundle);
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
        dates = (ArrayList<Date>) getArguments().getSerializable("dates");
        event_id = getArguments().getString("event_id");
        user_id = getArguments().getString("user_id");
        participants = getArguments().getStringArrayList("participants");
        event_name = getArguments().getString("event_name");

        parent = getActivity().getLayoutInflater().inflate(R.layout.dlg_event_confirm_date, null);
        final ConfirmDateListAdapter adapter = new ConfirmDateListAdapter(getContext(), R.id.listDates, dates,event_id,getFragmentManager(),this, user_id, participants, event_name);
        ListView list= parent.findViewById(R.id.listDates);
        list.setAdapter(adapter);
        builder.setView(parent);

        return builder.create();
    }

    public  void finish(){
        getDialog().dismiss();
    }

}
