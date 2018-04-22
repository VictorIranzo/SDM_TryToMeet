package com.sdm.trytomeet.fragments.Events;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sdm.trytomeet.POJO.Comment;
import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.CommentListAdapter;
import com.sdm.trytomeet.adapters.MemberListAdapter;
import com.sdm.trytomeet.adapters.VoteDateListAdapter;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EventFragment extends Fragment implements OnMapReadyCallback {
    private View parent;
    private String user_id;
    private String event_id;

    private TextView event_name;
    private EditText event_name_edit;
    private TextView event_state;
    private TextView event_description;
    private EditText event_description_edit;
    private TextView event_site_name;
    private TextView event_site_description;
    private Button event_showMap_button;
    private LinearLayout event_map;

    private RecyclerView event_dates;
    private TextView confirmed_date;

    private ArrayList<Date> dates;
    private ListView event_participants;
    private ListView event_comments;
    private EditText comment_write;
    private Button edit_description;
    private Button add_comment;
    private Button confirm_event;
    private CircularImageView image;

    private Button cancel_event;
    private Button delete_event;

    private MemberListAdapter participantsAdapter;
    private VoteDateListAdapter voteDateListAdapter;
    private CommentListAdapter commentListAdapter;
    public static final int GET_FROM_GALLERY = 1;


    public List<Comment> comments;


    private Event shownEvent;
    private List<User> participants;

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
        parent = inflater.inflate(R.layout.frg_event, container, false);
        user_id = getArguments().getString("user_id");
        event_id = getArguments().getString("event_id");

        event_name = parent.findViewById(R.id.event_name);
        event_name_edit = parent.findViewById(R.id.event_name_edit);
        event_state = parent.findViewById(R.id.event_state);
        event_description = parent.findViewById(R.id.event_description);
        event_description_edit = parent.findViewById(R.id.event_description_edit);

        event_site_name = parent.findViewById(R.id.event_site_name);
        event_site_description = parent.findViewById(R.id.event_site_description);
        event_map = parent.findViewById(R.id.event_map);
        event_showMap_button = parent.findViewById(R.id.event_showMap_button);

        event_dates = parent.findViewById(R.id.event_dates);
        confirmed_date = parent.findViewById(R.id.confirmed_date);
        event_participants = parent.findViewById(R.id.event_participants);

        event_comments = parent.findViewById(R.id.event_comments);
        comment_write = parent.findViewById(R.id.comment_write);
        add_comment = parent.findViewById(R.id.add_comment);
        confirm_event = parent.findViewById(R.id.confirm_event);
        cancel_event = parent.findViewById(R.id.cancel_event);
        delete_event = parent.findViewById(R.id.delete_event);
        edit_description = parent.findViewById(R.id.edit_description_button);
        image = parent.findViewById(R.id.image);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_google_map);
        mapFragment.getMapAsync(this);

        comment_write.addTextChangedListener(textWatcher);

        add_comment.setEnabled(false);

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        confirm_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmate_event();
            }
        });

        cancel_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEventClick();
            }
        });

        edit_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_description();
            }
        });

        event_showMap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });

        comments = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(getActivity(),R.id.event_comments,comments);
        event_comments.setAdapter(commentListAdapter);

        event_dates.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        participants = new ArrayList<User>();

        EventFirebaseService.getEvent(event_id, this);

        return parent;
    }

    private void cancelEventClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_cancel_event));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            EventFirebaseService.cancelEvent(event_id);
            }
        });

        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                add_comment.setEnabled(false);
            } else {
                add_comment.setEnabled(true);
            }
        }
    };

    private void addComment() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        java.util.Date today = Calendar.getInstance().getTime();
        String dateString = df.format(today);

        if (currentUser != null) {
            Comment c = new Comment(currentUser.username, comment_write.getText().toString(), dateString, currentUser.image);
            EventFirebaseService.AddComment(c,event_id, user_id, shownEvent.participants_id,
                    getResources().getString(R.string.comment_added_notification_title),
                    getResources().getString(R.string.comment_added_notification_text, currentUser.username, shownEvent.name));

            putEventComment(c);

            comment_write.setText("");
        }
    }

    public void setUpEventView(final Event event) {
        shownEvent = event;

        event_name.setText(event.name);
        event_name_edit.setText(event.name);
        event_state.setText(event.state);
        event_description.setText(event.description);
        event_description_edit.setText(event.description);
        event_site_name.setText(event.site.name);
        event_site_description.setText(event.site.description);

        enableVoting();

        voteDateListAdapter = new VoteDateListAdapter(event.possible_dates, user_id, event_id);
        event_dates.setAdapter(voteDateListAdapter);

        dates= new ArrayList<Date>(event.possible_dates);

        participantsAdapter = new MemberListAdapter(getActivity(), R.id.event_participants, participants, false);
        event_participants.setAdapter(participantsAdapter);

        setUpDeleteEvent();

        if(event.image!=null) Glide.with(this).load(event.image).into(image);

        // Adds the creator to the participants list.
        UserFirebaseService.getUser(event.creator_id, this);

        if (event.participants_id != null)
            for (String user : event.participants_id) {
                UserFirebaseService.getUser(user, this);
            }

        if (event.comments != null)
            for (Comment c : event.comments.values()) {
                putEventComment(c);
            }

        if (event.creator_id.equals(user_id) && event.state.equals(Event.PENDING)) {
            confirm_event.setVisibility(VISIBLE);
        }

        if (event.creator_id.equals(user_id) && !event.state.equals(Event.DONE) && !event.state.equals(Event.CANCELED)) {
            cancel_event.setVisibility(VISIBLE);
        }

        event_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.creator_id.equals(user_id)) edit_description();

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.creator_id.equals(user_id)) setImage();
            }
        });

        enable_show_images();
    }

    private void setUpDeleteEvent() {
        if(shownEvent.state.equals(Event.PENDING)|| shownEvent.state.equals(Event.CONFIRMED))
        {
            if(shownEvent.creator_id.equals(user_id)){
                delete_event.setVisibility(GONE);
                return;
            }

            delete_event.setText(getString(R.string.leave_event));
            delete_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.sure_Leave_Event));

                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EventFirebaseService.deleteTakingPart(user_id,event_id);
                            EventFirebaseService.deleteParticipant(user_id,event_id);

                            goToEventList();
                        }
                    });

                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();
                }
            });
        }
        if(shownEvent.state.equals(Event.DONE)|| shownEvent.state.equals(Event.CANCELED))
        {
            delete_event.setText(getString(R.string.delete_history));
            delete_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.sure_Delete_History));

                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EventFirebaseService.deleteTakingPart(user_id,event_id);

                            goToEventList();
                        }
                    });

                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();
                }
            });
        }
    }

    private void goToEventList() {
        EventListFragment fragment = new EventListFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    private void enableVoting() {
        if(!shownEvent.state.equals(Event.PENDING)){
            event_dates.setVisibility(GONE);
            confirmed_date.setVisibility(VISIBLE);
            if(shownEvent.confirmed_date!=null)
                confirmed_date.setText(shownEvent.confirmed_date.toString());

        }

        if(user_id.equals(shownEvent.creator_id)) {
            event_dates.setVisibility(GONE);

            // Shows the resume of date voting.
            confirmed_date.setVisibility(VISIBLE);

           if(shownEvent.confirmed_date==null)
               confirmed_date.setText(shownEvent.getPossibleDatesResume());
        }
    }

    private void putEventComment(Comment c) {
        comments.add(c);
        commentListAdapter.notifyDataSetChanged();
    }

    private void enable_show_images(){
        if(shownEvent.state.equals(Event.DONE)){
            final Button show_images = parent.findViewById(R.id.show_images);
            show_images.setVisibility(View.VISIBLE);
            show_images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new EventImageGallery();
                    Bundle bundle = new Bundle();
                    bundle.putString("event_id", event_id);
                    bundle.putString("event_name", shownEvent.name);
                    bundle.putString("username", currentUser.username);
                    bundle.putStringArrayList("participants", (ArrayList<String>) shownEvent.participants_id);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.frameLayout,
                            fragment).addToBackStack("gallery").commit();
                }
            });
        }
    }

    User currentUser;

    public void addUser(User user) {
        if (user.id.equals(user_id)) currentUser = user;
        participants.add(user);
        participantsAdapter.notifyDataSetChanged();
    }

    public void confirmate_event() {
        /*EventFirebaseService.confirmateEvent(event_id);
        EventListFragment fragment = new EventListFragment();
        Toast.makeText(getActivity(), getResources().getString(R.string.event_confimed), Toast.LENGTH_LONG).show();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();*/
        ConfirmEventFragment fragment =ConfirmEventFragment.newInstance(dates,event_id, user_id, (ArrayList<String>) shownEvent.participants_id, shownEvent.name);
        fragment.setCancelable(false);
        // In order that the Dialog is able to use methods from this class
        fragment.setTargetFragment(this,0);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void edit_description() {
        event_description.setVisibility(GONE);
        edit_description.setVisibility(VISIBLE);
        event_description_edit.setVisibility(VISIBLE);
    }

    private void change_description() {
        event_description.setVisibility(VISIBLE);
        edit_description.setVisibility(GONE);
        event_description_edit.setVisibility(GONE);
        String newDescription = event_description_edit.getText().toString();
        event_description.setText(newDescription);
        event_description_edit.setText("");
        EventFirebaseService.editEventDescription(event_id, newDescription);
    }

    private void setImage() {

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                image.setImageBitmap(bitmap);
                EventFirebaseService.setEventImage(event_id,selectedImage);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static final int DEFAULT_ZOOM = 15;
    private static GoogleMap map = null;
    private static Marker marker = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    private static boolean shownMap = false;
    private void showMap(){
        if(map != null && shownEvent != null && shownEvent.site != null){

            if(shownMap){
                event_map.setVisibility(GONE);
                event_showMap_button.setText(getString(R.string.event_show_map));
            } else {
                event_map.setVisibility(VISIBLE);
                event_showMap_button.setText(getString(R.string.event_hide_map));
            }
            shownMap = !shownMap;

            if(marker == null){
                marker = map.addMarker(new MarkerOptions()
                        .title(shownEvent.site.name)
                        .position(new LatLng(shownEvent.site.latitude, shownEvent.site.longitude))
                        .snippet(shownEvent.site.description));
            }

            // Position the map's camera at the location of the marker.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),
                    DEFAULT_ZOOM));
            map.getUiSettings().setScrollGesturesEnabled(false);
        }
    }
}
