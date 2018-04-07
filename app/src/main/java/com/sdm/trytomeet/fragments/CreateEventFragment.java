package com.sdm.trytomeet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.CreateEventDateListAdapter;
import com.sdm.trytomeet.adapters.CreateEventParticipantListAdapter;

import java.util.ArrayList;
import java.util.List;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.InvitedTo;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;

public class CreateEventFragment extends Fragment {

    private View parent;
    private String user_id;

    private ArrayList<User> participants;
    private CreateEventParticipantListAdapter participant_adapter;

    private ArrayList<Date> dates;
    private CreateEventDateListAdapter date_adapter;

    private Site site;

    public CreateEventFragment() {
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

        // TODO: Move to the layout XML the definition of the onClick listener.
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

        final Button find_place = parent.findViewById(R.id.button_find_place);
        find_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find_place(v);
            }
        });

        // We configure the list view for dates
        dates = new ArrayList<>();
        final ListView list_view_date = parent.findViewById(R.id.date_list);
        date_adapter = new CreateEventDateListAdapter(getContext(), R.id.date_list, dates);
        list_view_date.setAdapter(date_adapter);

        // We configure the list view for participants
        participants = new ArrayList<>();
        final ListView list_view_participant = parent.findViewById(R.id.participant_list);
        participant_adapter = new CreateEventParticipantListAdapter(getContext(), R.id.participant_list, participants);
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
                Event event = new Event(name, description, possible_dates, participants_id, creator_id, state, site);

                // We store the event in the DB
                final String key = FirebaseDatabase.getInstance().getReference().child("events").push().getKey();
                FirebaseDatabase.getInstance().getReference().child("events").child(key).setValue(event);

                // We link each participant (and the creator) with the new event
                List<String> to_invite = new ArrayList<>(participants_id);
                to_invite.add(user_id);
                for(String id : to_invite){
                    InvitedTo inv = new InvitedTo();
                    inv.state = "PENDING";
                    FirebaseDatabase.getInstance().getReference().child("taking_part")
                            .child(id).child("invitedTo").child(key).setValue(inv);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void add_participant(View view){
        // Show a pop-up to select among your friends
        ArrayList<String> current_id_participants = new ArrayList<>();
        for(User participant : participants) current_id_participants.add(participant.id);
        AddParticipantFragmentDialog fragment = AddParticipantFragmentDialog.newInstance(user_id, current_id_participants);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void add_date(View view){
        // Show a pop-up to select a date
        AddDateFragmentDialog fragment = AddDateFragmentDialog.newInstance();
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void find_place(View v) {
        FindPlaceFragment fragment = new FindPlaceFragment();
        fragment.setTargetFragment(this,0);

        // TODO: Revisar esta solución. Ahora, cuando se abre el fragment de elegir sitio, este se oculta. Una vez elegido,
        // el mapa se elimina desde el gestor de fragments de create event y se vuelve a mostrar este.
        getView().setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    // Method to be called from the AddParticipantFragmentDialog
    public void add_participants(ArrayList<User> to_add){
        participants.addAll(to_add);
        participant_adapter.notifyDataSetChanged();
    }

    // Method to be called from AddDateFragmentDialog
    public void add_date(Date date){
        if(!dates.contains(date)) dates.add(date);
        date_adapter.notifyDataSetChanged();
    }

    public void add_site(Site site){
        this.site = site;
        //TODO: Change UI when the site is selected.
        ((TextView) parent.findViewById(R.id.selectedPlace)).setText(site.name);
        ((LinearLayout) parent.findViewById(R.id.layoutSelectedPlace)).setVisibility(View.VISIBLE);

        getView().setVisibility(View.VISIBLE);
    }
}
