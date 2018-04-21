package com.sdm.trytomeet.fragments.Events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.GalleryAdapter;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.ArrayList;
import java.util.HashMap;

public class EventImageGallery extends Fragment {

    String event_id;
    ArrayList<String> images;
    View parent;
    RecyclerView gallery;
    GalleryAdapter adapter;
    private final int GET_FROM_GALLERY = 1;
    ChildEventListener listener;

    public EventImageGallery() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_event_image_gallery, container, false);
        event_id = getArguments().getString("event_id");
        //images = getArguments().getStringArrayList("images");

        // Set up the gallery
        images = new ArrayList<String>();
        gallery = parent.findViewById(R.id.gallery);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setAutoMeasureEnabled(true);
        gallery.setLayoutManager(manager);
        adapter = new GalleryAdapter(images, getActivity());
        gallery.setAdapter(adapter);
        getImages();

        // Button to upload an image
        ((Button) parent.findViewById(R.id.add_images_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        return parent;
    }

    private void getImages(){
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String image = (String)dataSnapshot.getValue();
                images.add(image);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String image = (String)dataSnapshot.getValue();
                images.remove(image);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        EventFirebaseService.addImageListener(event_id, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventFirebaseService.removeImageListener(event_id,listener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            EventFirebaseService.uploadImage(event_id, selectedImage);
        }
    }

}
