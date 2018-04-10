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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.InvitedTo;
import com.sdm.trytomeet.POJO.Notification;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.adapters.CreateEventDateListAdapter;
import com.sdm.trytomeet.adapters.CreateEventParticipantListAdapter;
import com.sdm.trytomeet.notifications.NotificactionListener;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.persistence.server.NotificationFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupFragment extends Fragment {

    private View parent;
    private String user_id;

    private ArrayList<User> participants;
    private CreateEventParticipantListAdapter participant_adapter;

    public CreateGroupFragment() {
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
        parent = inflater.inflate(R.layout.fragment_create_group, container, false);
        user_id = getArguments().getString("user_id");

        Button add_partcipant = parent.findViewById(R.id.button_add_participant);
        add_partcipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_participant(v);
            }
        });

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
        String name = ((EditText) parent.findViewById(R.id.groupName)).getText().toString();
        List<String> participants_id = new ArrayList<>();
        participants_id.add(user_id);
        for(User user : participants) participants_id.add(user.id);
        Group group = new Group(participants_id, name);

        // We store the event in the DB
        UserFirebaseService.createGroup(group);

        Toast.makeText(getActivity(),"Group added", Toast.LENGTH_SHORT).show();

        GroupsFragment fragment = new GroupsFragment();
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
        AddParticipantGroupFragmentDialog fragment = AddParticipantGroupFragmentDialog.newInstance(user_id, current_id_participants);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
    // Method to be called from the AddParticipantFragmentDialog
    public void add_participants(ArrayList<User> to_add){
        participants.addAll(to_add);
        participant_adapter.notifyDataSetChanged();
    }

    public void make_visible(){
        getView().setVisibility(View.VISIBLE);
    }

}
