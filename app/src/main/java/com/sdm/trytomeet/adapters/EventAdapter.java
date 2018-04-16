package com.sdm.trytomeet.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView eventName;
        TextView eventDescription;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            eventName = (TextView)itemView.findViewById(R.id.name);
            eventDescription = (TextView)itemView.findViewById(R.id.description);
        }
    }

    List<Event> events;

    public EventAdapter(List<Event> events){
        this.events = events;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.eventName.setText(events.get(i).name);
        personViewHolder.eventDescription.setText(events.get(i).description);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}