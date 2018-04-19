package com.sdm.trytomeet.fragments.Events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.adapters.CreateEventDateListAdapter;
import com.sdm.trytomeet.adapters.CreateEventParticipantListAdapter;
import com.sdm.trytomeet.fragments.Sites.FindPlaceFragment;
import com.sdm.trytomeet.notifications.NotificactionListener;

import java.util.ArrayList;
import java.util.List;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.InvitedTo;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.POJO.Notification;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.persistence.server.NotificationFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

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

        Button add_partcipant = parent.findViewById(R.id.button_add_participant);
        add_partcipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_participant(v);
            }
        });

        Button add_group = parent.findViewById(R.id.button_add_group);
        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_group(v);
            }
        });

        Button add_date = parent.findViewById(R.id.button_add_date);
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_date(v);
            }
        });

        Button find_place = parent.findViewById(R.id.button_find_place);
        find_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find_place(v);
            }
        });

        ImageButton add_favorite = parent.findViewById(R.id.button_favorite_site);
        add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavoriteSite();
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
                confirmEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmEvent() {
        // We create the Event object
        String name = ((TextView) parent.findViewById(R.id.create_event_title)).getText().toString();
        String description = ((TextView) parent.findViewById(R.id.create_event_description)).getText().toString();
        List<Date> possible_dates = new ArrayList<>();
        for(Date date : dates) possible_dates.add(date);
        List<String> participants_id = new ArrayList<>();
        for(User user : participants) participants_id.add(user.id);
        String creator_id = user_id;
        String state = Event.PENDING;
        Event event = new Event(name, description, possible_dates, participants_id, creator_id, state, site);

        // We check that all the required fields are fulfilled
        if(event.name.equals("")) Toast.makeText(getContext(),R.string.create_event_missing_title, Toast.LENGTH_SHORT).show();
        else if(event.description.equals("")) Toast.makeText(getContext(),R.string.create_event_missing_description, Toast.LENGTH_SHORT).show();
        else if(event.site == null) Toast.makeText(getContext(),R.string.create_event_missing_place, Toast.LENGTH_SHORT).show();
        else if(event.possible_dates.isEmpty()) Toast.makeText(getContext(),R.string.create_event_missing_dates, Toast.LENGTH_SHORT).show();
        else if(event.participants_id.isEmpty()) Toast.makeText(getContext(),R.string.create_event_missing_participants, Toast.LENGTH_SHORT).show();

        else { //Everything is fine

            // We store the event in the DB
            String event_id = EventFirebaseService.addEvent(event);

            // We link each participant (and the creator) with the new event
            List<String> to_invite = new ArrayList<>(participants_id);
            to_invite.add(user_id);
            InvitedTo inv = new InvitedTo("PENDING");
            for (String participant_id : to_invite) {
                EventFirebaseService.addParticipantToEvent(inv, participant_id, event_id);
            }

            // We notify each user
            to_invite.remove(user_id); // Not me
            Notification notification = new Notification(
                    NotificactionListener.ADDED_TO_AN_EVENT,
                    getResources().getString(R.string.create_event_notification_title),
                    getResources().getString(R.string.create_event_notification_text, MainActivity.accountGoogle.getDisplayName(), event.name),
                    event_id);

            for (String participant_id : to_invite) {
                NotificationFirebaseService.addNotification(notification, participant_id);
            }

            goToEventList();
        }
    }

    private void goToEventList() {
        EventListFragment fragment = new EventListFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
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

    private void add_group(View view){
        //Show a pop-up to select among your groups
        ArrayList<String> current_id_participants = new ArrayList<>();
        for(User participant : participants) current_id_participants.add(participant.id);
        AddGroupFragmentDialog fragment = AddGroupFragmentDialog.newInstance(user_id, current_id_participants);
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

    private boolean firstime = true;
    private void find_place(View v) {
        FindPlaceFragment fragment = new FindPlaceFragment();
        fragment.setTargetFragment(this,0);

        // TODO: Revisar esta solución. Ahora, cuando se abre el fragment de elegir sitio, este se oculta. Una vez elegido,
        // el mapa se elimina desde el gestor de fragments de create event y se vuelve a mostrar este.
        getView().setVisibility(View.GONE);

        if(firstime) {
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment, "Find_Place").addToBackStack(null).commit();
            firstime = false;
        } else {
            fragment = (FindPlaceFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Find_Place");
            fragment.make_visible();
        }
    }

    // Method to be called from the AddParticipantFragmentDialog
    public void add_participants(ArrayList<User> to_add){
        participants.addAll(to_add);
        participant_adapter.notifyDataSetChanged();
    }

    // Method to be called from the AddGroupFragmentDialog
    public void add_group(ArrayList<Group> toAdd) {
        for(Group group : toAdd){
            for(String member : group.members){
                if(member.equals(user_id)) continue;

                boolean add = true;
                for(User participant : participants){
                    if(participant.id.equals(member)){
                        add = false;
                        Log.e("Got","it");
                        break;
                    }
                }

                if(add){
                    FirebaseDatabase.getInstance().getReference().child("users").child(member)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    participants.add(user);
                                    participant_adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        }
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

        make_visible();

        Button findPlaceButton = (Button)parent.findViewById(R.id.button_find_place);
        findPlaceButton.setText(getString(R.string.create_event_change_place_button));

        // TODO: Allow add favorite place after check that it's not one of the favorite places.
    }

    public void make_visible(){
        getView().setVisibility(View.VISIBLE);
    }

    private void addFavoriteSite() {
        UserFirebaseService.addUserFavoriteSite(user_id,site);

        Toast.makeText(getActivity(),"Anyadido sITIO favorito",Toast.LENGTH_LONG).show();
    }
}
