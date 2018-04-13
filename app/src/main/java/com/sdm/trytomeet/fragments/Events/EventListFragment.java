package com.sdm.trytomeet.fragments.Events;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sdm.trytomeet.R;
import com.sdm.trytomeet.fragments.Events.CreateEventFragment;
import com.sdm.trytomeet.fragments.Sites.FavoriteSitesFragment;

public class EventListFragment extends Fragment {

    private View parent;
    private String user_id;


    // TODO: Remove this.
    private Button pruebas;


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
        parent = inflater.inflate(R.layout.fragment_event_list, container, false);
        user_id = getArguments().getString("user_id");

        FloatingActionButton createButton = parent.findViewById(R.id.floatingActionButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateEvent();
            }
        });

        CardView evento = parent.findViewById(R.id.ev);

        pruebas = parent.findViewById(R.id.pruebas);

        pruebas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEvent();
            }
        });

        return parent;

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
                nothing();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void goToCreateEvent() {
        CreateEventFragment fragment = new CreateEventFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    public void nothing() {
        Toast toast1 =
                Toast.makeText(getContext(),
                        "No hay notificaciones", Toast.LENGTH_SHORT);

        toast1.show();
    }

    private void goToEvent(){
        String event_id= "-L9W90Dm39W5yGI6sevJ";
        EventFragment fragment = new EventFragment();

        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putString("event_id", event_id);
        fragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }
}