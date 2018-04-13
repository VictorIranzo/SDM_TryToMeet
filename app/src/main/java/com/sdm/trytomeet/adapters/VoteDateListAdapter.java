package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.R;

import java.util.ArrayList;
import java.util.List;

public class VoteDateListAdapter extends RecyclerView.Adapter<VoteDateListAdapter.ViewHolder> {
    private List<Date> data;
    private String user_id;

    public VoteDateListAdapter(List<Date> data, String user_id){
        this.data = data;
        this.user_id = user_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_date_layout,parent,false);
        VoteDateListAdapter.ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dateButton.setText(data.get(position).toString());
        if(data.get(position).voted_users.contains(user_id)){
            holder.dateButton.setBackgroundColor(Color.GREEN);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public Button dateButton;


        public ViewHolder(View itemView) {
            super(itemView);
            dateButton = itemView.findViewById(R.id.vote_button);
        }
    }
}