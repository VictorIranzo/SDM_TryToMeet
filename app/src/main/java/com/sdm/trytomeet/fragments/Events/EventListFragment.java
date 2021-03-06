package com.sdm.trytomeet.fragments.Events;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.adapters.EventListAdapter;
import com.sdm.trytomeet.helpers.EventTransactionConfirmedToDone;
import com.sdm.trytomeet.helpers.EventTransactionPendingToCanceled;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.services.CheckInternet;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private View parent;
    protected String user_id;

    protected List<EventWithKey> events;

    private RecyclerView rv;
    protected EventListAdapter adapter;
    protected ProgressBar progressBar;
    protected TextView no_events_text;
    private RecyclerView.LayoutManager llm;

    public EventListFragment() {
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
        parent = inflater.inflate(R.layout.frg_event_list, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

        FloatingActionButton createButton = parent.findViewById(R.id.floatingActionButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateEvent();
            }
        });
        rv = parent.findViewById(R.id.rv);
        no_events_text = parent.findViewById(R.id.no_events_text);

        llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        progressBar = ((ProgressBar)parent.findViewById(R.id.progressBarMembers));
        progressBar.setIndeterminate(true);

        events = new ArrayList<EventWithKey>();

        initializeAdapter();

        setActionBarTitle();

        getUserEvents();

        // Used when coming from a notification
        if(getArguments() != null){
            goToEvent(getArguments().getString("goto"));
            setArguments(null);
        }
        return parent;

    }

    protected void setActionBarTitle() {
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.Events_title));
    }

    protected void getUserEvents() {
        CheckInternet.isNetworkConnected(getContext());
        EventFirebaseService.getUserEvents(user_id,this, progressBar, no_events_text);
    }

    public void goToPendingEvents() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("peding_votes");
        if(fragment == null) fragment = new PendingVoteEventListFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, "peding_votes").addToBackStack(null).commit();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.alertTitle:
                goToPendingEvents();
                break;
        }
        return true;
    }


    private void goToCreateEvent() {
        CreateEventFragment fragment = new CreateEventFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment, "create_events").addToBackStack(null).commit();
    }

    public void goToEvent(String event_id){
        EventFragment fragment = new EventFragment();

        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putString("event_id", event_id);
        fragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    private void initializeAdapter(){
        adapter = new EventListAdapter(events, this);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(adapter);
    }

    public void addEventToList(String event_id, Event e){
        if(e.state.equals(Event.CONFIRMED) || e.state.equals(Event.VOTED) || e.state.equals(Event.PENDING)) {
            EventWithKey eventWithKey = new EventWithKey(event_id,e);
            no_events_text.setVisibility(View.GONE);

            boolean notAdd = EventTransactionConfirmedToDone.checkConfirmedDate(eventWithKey);
            notAdd = notAdd || EventTransactionPendingToCanceled.checkIfPassedDate(eventWithKey);

            // Check if not added
            if(!notAdd){
                for(EventWithKey current_e : events){
                    if(current_e.event_id.equals(event_id)){
                        notAdd = true;
                        break;
                    }
                }
            }

            if(notAdd) return;

            events.add(eventWithKey);
            adapter.notifyDataSetChanged();
        }
    }
}
