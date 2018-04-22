package com.sdm.trytomeet.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.ArrayList;
import java.util.List;

public class VoteDateListAdapter extends RecyclerView.Adapter<VoteDateListAdapter.ViewHolder> {
    public List<Date> data;
    public String user_id;
    public String event_id;

    public VoteDateListAdapter(List<Date> data, String user_id, String event_id){
        this.data = data;
        this.user_id = user_id;
        this.event_id = event_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_date_vote,parent,false);
        VoteDateListAdapter.ViewHolder holder = new ViewHolder(view, this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        holder.dateButton.setText(data.get(position).toString());

        if(data.get(position).voted_users != null && data.get(position).voted_users.contains(user_id))
        {
            holder.dateButton.setBackgroundColor(Color.GREEN);
            holder.voted = true;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public Button dateButton;
        public boolean voted;
        public int position;

        private VoteDateListAdapter adapter;

        public ViewHolder(View itemView, VoteDateListAdapter adapter) {
            super(itemView);
            dateButton = itemView.findViewById(R.id.vote_button);
            voted = false;

            this.adapter = adapter;

            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setVote();
                }
            });
        }

        private void setVote() {
            if(voted)
            {
                EventFirebaseService.removeVote(adapter.event_id, adapter.user_id, adapter.data.get(position));
                voted = false;
                dateButton.setBackgroundResource(android.R.drawable.btn_default);
                adapter.data.get(position).voted_users.remove(adapter.user_id);
            }
            else
            {
                EventFirebaseService.addVote(adapter.event_id, adapter.user_id, adapter.data.get(position));
                voted = true;
                dateButton.setBackgroundColor(Color.GREEN);
                if(adapter.data.get(position).voted_users == null) adapter.data.get(position).voted_users = new ArrayList<String>();
                    adapter.data.get(position).voted_users.add(adapter.user_id);
            }

            dateButton.setText(adapter.data.get(position).toString());
        }
    }
}