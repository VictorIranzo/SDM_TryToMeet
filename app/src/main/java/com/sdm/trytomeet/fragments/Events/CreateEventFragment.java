package com.sdm.trytomeet.fragments.Events;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
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
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Sites.FindPlaceFragment;

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
import com.sdm.trytomeet.persistence.server.SitesFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

public class CreateEventFragment extends Fragment {

    private View parent;
    private String user_id;

    private ArrayList<User> participants;
    private CreateEventParticipantListAdapter participant_adapter;

    private ArrayList<Date> dates;
    private CreateEventDateListAdapter date_adapter;

    private Site site;

    public static final int GET_FROM_GALLERY = 1;

    Uri image_path;
    CircularImageView image;

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
        parent = inflater.inflate(R.layout.frg_create_event, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

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

        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.Create_an_event_title));

        TabHost host = parent.findViewById(R.id.tabHost_Event);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec(getResources().getString(R.string.event_description));
        spec1.setIndicator(getResources().getString(R.string.event_description));
        spec1.setContent(R.id.tab1);
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec(getResources().getString(R.string.event_participants));
        spec2.setIndicator(getResources().getString(R.string.event_dates));
        spec2.setContent(R.id.tab2);
        host.addTab(spec2);

        TabHost.TabSpec spec3 = host.newTabSpec(getResources().getString(R.string.event_comments));
        spec3.setIndicator(getResources().getString(R.string.event_participants));
        spec3.setContent(R.id.tab3);
        host.addTab(spec3);

        host.setCurrentTab(0);

        image_path = null;
        image = parent.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });

        if(savedInstanceState != null){
            image_path = savedInstanceState.getParcelable("image");
            image.setImageURI(image_path);
            dates.addAll((ArrayList<Date>) savedInstanceState.getSerializable("dates"));
            date_adapter.notifyDataSetChanged();
            participants.addAll((ArrayList<User>) savedInstanceState.getSerializable("participants"));
            participant_adapter.notifyDataSetChanged();
        }

        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        for (Date date : dates) possible_dates.add(date);
        List<String> participants_id = new ArrayList<>();
        for (User user : participants) participants_id.add(user.id);
        String creator_id = user_id;
        String state = Event.PENDING;
        Event event = new Event(name, description, possible_dates, participants_id, creator_id, state, site);

        // We check that all the required fields are fulfilled
        if (event.name.equals(""))
            Toast.makeText(getContext(), R.string.create_event_missing_title, Toast.LENGTH_SHORT).show();
        else if (event.description.equals(""))
            Toast.makeText(getContext(), R.string.create_event_missing_description, Toast.LENGTH_SHORT).show();
        else if (event.site == null)
            Toast.makeText(getContext(), R.string.create_event_missing_place, Toast.LENGTH_SHORT).show();
        else if (event.possible_dates.isEmpty())
            Toast.makeText(getContext(), R.string.create_event_missing_dates, Toast.LENGTH_SHORT).show();
        else if (event.participants_id.isEmpty())
            Toast.makeText(getContext(), R.string.create_event_missing_participants, Toast.LENGTH_SHORT).show();

        if(image_path == null){ //Set one by default
            image_path = Uri.parse("android.resource://"+getContext().getPackageName()+"/drawable/pajaro");
        }
        else { //Everything is fine

            // We store the event in the DB
            String event_id = EventFirebaseService.addEvent(event, image_path);

            // We link each participant (and the creator) with the new event
            List<String> to_invite = new ArrayList<>(participants_id);
            InvitedTo inv = new InvitedTo(InvitedTo.PENDING);
            for (String participant_id : to_invite) {
                EventFirebaseService.addParticipantToEvent(inv, participant_id, event_id);
            }

            // The creator is added with VOTED state.
            EventFirebaseService.addParticipantToEvent(new InvitedTo(InvitedTo.VOTED), user_id, event_id);

            // We notify each user
            to_invite.remove(user_id); // Not me
            Notification notification = new Notification(
                    Notification.ADDED_TO_AN_EVENT,
                    getResources().getString(R.string.create_event_notification_title),
                    getResources().getString(R.string.create_event_notification_text, MainActivity.accountGoogle.getDisplayName(), event.name));
            notification.event_id = event_id;
            for (String participant_id : to_invite) {
                NotificationFirebaseService.addNotification(notification, participant_id);
            }

            goToEventList();
        }
    }

    private void goToEventList() {
        EventListFragment fragment = new EventListFragment();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this)
                .replace(R.id.frameLayout, fragment).commit();
        site = null;
    }

    private void add_participant(View view) {
        // Show a pop-up to select among your friends
        ArrayList<String> current_id_participants = new ArrayList<>();
        for (User participant : participants) current_id_participants.add(participant.id);
        AddParticipantFragmentDialog fragment = AddParticipantFragmentDialog.newInstance(user_id, current_id_participants);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this, 0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void add_group(View view) {
        //Show a pop-up to select among your groups
        ArrayList<String> current_id_participants = new ArrayList<>();
        for (User participant : participants) current_id_participants.add(participant.id);
        AddGroupFragmentDialog fragment = AddGroupFragmentDialog.newInstance(user_id, current_id_participants);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this, 0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void add_date(View view) {
        // Show a pop-up to select a date
        AddDateFragmentDialog fragment = AddDateFragmentDialog.newInstance();
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this, 0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private boolean firstime = true;

    private void find_place(View v) {
        FindPlaceFragment fragment;

        fragment = new FindPlaceFragment();
        fragment.setTargetFragment(this, 0);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack("Find_Place").commit();
    }

    // Method to be called from the AddParticipantFragmentDialog
    public void add_participants(ArrayList<User> to_add) {
        participants.addAll(to_add);
        participant_adapter.notifyDataSetChanged();
    }

    // Method to be called from the AddGroupFragmentDialog
    public void add_group(ArrayList<Group> toAdd) {
        for (Group group : toAdd) {
            for (String member : group.members) {
                if (member.equals(user_id)) continue;

                boolean add = true;
                for (User participant : participants) {
                    if (participant.id.equals(member)) {
                        add = false;
                        Log.e("Got", "it");
                        break;
                    }
                }

                if (add) {
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
    public void add_date(Date date) {
        if (!dates.contains(date)) dates.add(date);
        date_adapter.notifyDataSetChanged();
    }

    public void add_site(Site site) {
        this.site = site;
    }

    private void addFavoriteSite() {
        SitesFirebaseService.addUserFavoriteSite(user_id, site);

        ((ImageButton) parent.findViewById(R.id.button_favorite_site)).setVisibility(View.GONE);
        Toast.makeText(getActivity(), getString(R.string.added_favorite), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.site != null) {
            ((TextView) parent.findViewById(R.id.selectedPlace)).setText(site.name);
            ((LinearLayout) parent.findViewById(R.id.layoutSelectedPlace)).setVisibility(View.VISIBLE);

            Button findPlaceButton = (Button) parent.findViewById(R.id.button_find_place);
            findPlaceButton.setText(getString(R.string.create_event_change_place_button));

            SitesFirebaseService.checkIfFavouriteSite(this.site, user_id, this);
        }
    }

    public void showAddFavourite() {
        ((ImageButton) parent.findViewById(R.id.button_favorite_site)).setVisibility(View.VISIBLE);
    }

    public void setImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GET_FROM_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            image_path = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image_path);
                image.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", image_path);
        outState.putSerializable("dates", dates);
        outState.putSerializable("participants", participants);
    }
}
