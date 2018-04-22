package com.sdm.trytomeet.fragments.Events;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.EventListAdapter;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.ArrayList;
import java.util.List;

// TODO: Redireccionar a evento en el click.

public class HistoricEvents extends EventListFragment{

    private View parent;
    private String user_id;

    private List<EventWithKey> events;

    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private RecyclerView.LayoutManager llm;


    // TODO: Remove this.
    private Button pruebas;


    public HistoricEvents() {
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
        parent = inflater.inflate(R.layout.frg_event_list_history, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

        recyclerView = parent.findViewById(R.id.recyclerView);

        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        events = new ArrayList<EventWithKey>();

        //CardView evento = parent.findViewById(R.id.ev);

        //pruebas = parent.findViewById(R.id.pruebas);

        /*pruebas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEvent();
            }
        });*/
        initializeAdapter();

        EventFirebaseService.getUserEvents(user_id,this);

        return parent;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }






    private void initializeAdapter(){
        adapter = new EventListAdapter(events, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }


    public void addEventToList(String event_id, Event e){
        if (e.state.equals("CANCELLED") || e.state.equals("DONE")){
            events.add(new EventWithKey(event_id,e));
            adapter.notifyDataSetChanged();
        }
    }
}
