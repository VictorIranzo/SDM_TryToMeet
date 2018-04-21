package com.sdm.trytomeet.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.R;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    public List<Event> events;

    public EventListAdapter(List<Event> events){
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        EventViewHolder holder = new EventViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int position) {
        eventViewHolder.eventName.setText(events.get(position).name);
        eventViewHolder.eventDescription.setText(events.get(position).description);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView eventName;
        TextView eventDescription;

        public EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            eventName = (TextView)itemView.findViewById(R.id.name);
            eventDescription = (TextView)itemView.findViewById(R.id.description);
        }
    }
}