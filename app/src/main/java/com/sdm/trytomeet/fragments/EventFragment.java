package com.sdm.trytomeet.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.R;

public class EventFragment extends Fragment {
    private View parent;
    private String user_id;
    private String event_id;

    private Event shownEvent;

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
        parent = inflater.inflate(R.layout.fragment_favorite_sites, container, false);
        user_id = getArguments().getString("user_id");
        event_id = getArguments().getString("event_id");

        return parent;
    }
}
