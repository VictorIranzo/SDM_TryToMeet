package com.sdm.trytomeet.fragments.Events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.MemberListAdapter;
import com.sdm.trytomeet.adapters.VoteDateListAdapter;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {
    private View parent;
    private String user_id;
    private String event_id;

    private TextView event_name;
    private EditText event_name_edit;
    private TextView event_state;
    private TextView event_description;
    private EditText event_description_edit;
    private TextView event_site_name;
    private TextView event_site_description;
    private RecyclerView event_dates;
    private ListView event_participants;

    private MemberListAdapter participantsAdapter;
    private VoteDateListAdapter voteDateListAdapter;

    private Event shownEvent;
    private List<User> participants;

    public EventFragment() {
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
        parent = inflater.inflate(R.layout.fragment_event, container, false);
        user_id = getArguments().getString("user_id");
        event_id = getArguments().getString("event_id");

        event_name = parent.findViewById(R.id.event_name);
        event_name_edit = parent.findViewById(R.id.event_name_edit);
        event_state =  parent.findViewById(R.id.event_state);
        event_description = parent.findViewById(R.id.event_description);
        event_description_edit =  parent.findViewById(R.id.event_description_edit);
        event_site_name =  parent.findViewById(R.id.event_site_name);
        event_site_description =  parent.findViewById(R.id.event_site_description);
        event_dates = parent.findViewById(R.id.event_dates);
        event_participants = parent.findViewById(R.id.event_participants);

        event_dates.setLayoutManager(new GridLayoutManager(getActivity(),2));

        participants = new ArrayList<User>();

        EventFirebaseService.getEvent(event_id, this);

        return parent;
    }

    public void setUpEventView(Event event){
        shownEvent = event;

        event_name.setText(event.name);
        event_name_edit.setText(event.name);
        event_state.setText(event.state);
        event_description.setText(event.description);
        event_description_edit.setText(event.description);
        event_site_name.setText(event.site.name);
        event_site_description.setText(event.site.description);

        voteDateListAdapter = new VoteDateListAdapter(event.possible_dates,user_id,event_id);
        event_dates.setAdapter(voteDateListAdapter);

        participantsAdapter = new MemberListAdapter(getActivity(),R.id.event_participants,participants);
        event_participants.setAdapter(participantsAdapter);
        
        for (String user: event.participants_id) {
            UserFirebaseService.getUser(user,this);
        }
    }

    public void addUser(User user) {
        participants.add(user);
        participantsAdapter.notifyDataSetChanged();
    }
}
