package com.sdm.trytomeet.adapters;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.fragments.Events.EventFragment;
import com.sdm.trytomeet.fragments.Events.EventListFragment;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    public List<EventWithKey> events;
    private EventListFragment eventListFragment;

    public EventListAdapter(List<EventWithKey> events, EventListFragment eventListFragment){
        this.events = events;
        this.eventListFragment = eventListFragment;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        EventViewHolder holder = new EventViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder eventViewHolder, final int position) {
        eventViewHolder.eventName.setText(events.get(position).name);
        eventViewHolder.eventDescription.setText(events.get(position).description);

        eventViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventListFragment.goToEvent(events.get(position).event_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView eventName;
        TextView eventDescription;
        View itemView;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.cv = (CardView)itemView.findViewById(R.id.cv);
            this.eventName = (TextView)itemView.findViewById(R.id.name);
            this.eventDescription = (TextView)itemView.findViewById(R.id.description);
        }
    }
}