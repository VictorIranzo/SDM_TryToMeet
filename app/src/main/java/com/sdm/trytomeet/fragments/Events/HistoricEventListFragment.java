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

public class HistoricEventListFragment extends EventListFragment{

    public void addEventToList(String event_id, Event e){
        if (e.state.equals(Event.CANCELED) || e.state.equals(Event.VOTED)){
            events.add(new EventWithKey(event_id,e));
            adapter.notifyDataSetChanged();
        }
    }
}
