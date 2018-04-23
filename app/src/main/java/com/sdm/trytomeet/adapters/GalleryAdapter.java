package com.sdm.trytomeet.adapters;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.fragments.Events.GalleyImageDialog;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    FragmentActivity activity;
    ArrayList<String> data;

    public GalleryAdapter(ArrayList<String> data, FragmentActivity activity){
        this.data = data;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event_image_gallery, parent, false);
        GalleryAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(holder.itemView).load(data.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleyImageDialog fragment = GalleyImageDialog.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("image", data.get(position));
                fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;

        public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.image_gallery);
        }
    }
}

























