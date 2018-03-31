package com.sdm.trytomeet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.add_participant_list_adapter;
import com.sdm.trytomeet.adapters.create_event_date_list_adapter;
import com.sdm.trytomeet.adapters.create_event_participant_list_adapter;

import java.util.ArrayList;
import java.util.List;

import POJO.Date;
import POJO.Event;
import POJO.InvitedTo;
import POJO.TakingPart;
import POJO.User;

public class Fragment_create_event extends Fragment {

    private View parent;
    private String user_id;

    private ArrayList<User> participants;
    private create_event_participant_list_adapter participant_adapter;

    private ArrayList<Date> dates;
    private create_event_date_list_adapter date_adapter;

    public Fragment_create_event() {
        // Required empty public constructor
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
        parent = inflater.inflate(R.layout.fragment_create_event, container, false);
        user_id = getArguments().getString("user_id");
        Button add_partcipant = parent.findViewById(R.id.button_add_participant);
        add_partcipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_participant(v);
            }
        });
        Button add_date = parent.findViewById(R.id.button_add_date);
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_date(v);
            }
        });

        // We configure the list view for dates
        dates = new ArrayList<>();
        final ListView list_view_date = parent.findViewById(R.id.date_list);
        date_adapter = new create_event_date_list_adapter(getContext(), R.id.date_list, dates);
        list_view_date.setAdapter(date_adapter);

        // We configure the list view for participants
        participants = new ArrayList<>();
        final ListView list_view_participant = parent.findViewById(R.id.participant_list);
        participant_adapter = new create_event_participant_list_adapter(getContext(), R.id.participant_list, participants);
        list_view_participant.setAdapter(participant_adapter);

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
                // We create the Event object
                String name = ((TextView) parent.findViewById(R.id.create_event_title)).getText().toString();
                String description = ((TextView) parent.findViewById(R.id.create_event_description)).getText().toString();
                List<Date> possible_dates = new ArrayList<>(); for(Date date : dates) possible_dates.add(date);
                List<String> participants_id = new ArrayList<>(); for(User user : participants) participants_id.add(user.id);
                String creator_id = user_id;
                String state = "PENDING";
                Event event = new Event(name, description, possible_dates, participants_id, creator_id, state);

                // We store the event in the DB
                final String key = FirebaseDatabase.getInstance().getReference().child("events").push().getKey();
                FirebaseDatabase.getInstance().getReference().child("events").child(key).setValue(event);

                // We link each participant (and the creator) with the new event
                List<String> to_invite = new ArrayList<>(participants_id);
                to_invite.add(user_id);
                for(String id : to_invite){
                    FirebaseDatabase.getInstance().getReference().child("taking_part").child(id).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            TakingPart takingPart = mutableData.getValue(TakingPart.class);
                            if(takingPart == null){
                                takingPart = new TakingPart();
                                takingPart.invitedTo = new ArrayList<>();
                            }
                            takingPart.invitedTo.add(new InvitedTo(key));
                            mutableData.setValue(takingPart);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            // Do nothing
                        }
                    });
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void add_participant(View view){
        // Show a pop-up to select among your friends
        ArrayList<String> current_id_participants = new ArrayList<>();
        for(User participant : participants) current_id_participants.add(participant.id);
        Fragment_add_participant_dialog fragment = Fragment_add_participant_dialog.newInstance(user_id, current_id_participants);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    public void add_date(View view){
        // Show a pop-up to select a date
        Fragment_add_date_dialog fragment = Fragment_add_date_dialog.newInstance();
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    // Method to be called from the Fragment_add_participant_dialog
    public void add_participants(ArrayList<User> to_add){
        participants.addAll(to_add);
        participant_adapter.notifyDataSetChanged();
    }

    // Method to be called from Fragment_add_date_dialog
    public void add_date(Date date){
        if(!dates.contains(date)) dates.add(date);
        date_adapter.notifyDataSetChanged();
    }


}
