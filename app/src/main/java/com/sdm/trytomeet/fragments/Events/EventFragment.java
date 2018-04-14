package com.sdm.trytomeet.fragments.Events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Comment;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.MemberListAdapter;
import com.sdm.trytomeet.adapters.VoteDateListAdapter;
import com.sdm.trytomeet.components.ListViewInScrollView;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventFragment extends Fragment {
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
    private RecyclerView event_dates;
    private ListView event_participants;
    private ListView event_comments;
    private EditText comment_write;
    private Button add_comment;

    private MemberListAdapter participantsAdapter;
    private VoteDateListAdapter voteDateListAdapter;

    public ArrayList<HashMap<String,String>> comments = new ArrayList<>();
    public SimpleAdapter commentsAdapter;


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
        parent = inflater.inflate(R.layout.fragment_event, container, false);
        user_id = getArguments().getString("user_id");
        event_id = getArguments().getString("event_id");

        event_name = parent.findViewById(R.id.event_name);
        event_name_edit = parent.findViewById(R.id.event_name_edit);
        event_state =  parent.findViewById(R.id.event_state);
        event_description = parent.findViewById(R.id.event_description);
        event_description_edit =  parent.findViewById(R.id.event_description_edit);
        event_site_name =  parent.findViewById(R.id.event_site_name);
        event_site_description =  parent.findViewById(R.id.event_site_description);
        event_dates = parent.findViewById(R.id.event_dates);
        event_participants = parent.findViewById(R.id.event_participants);
        event_comments = parent.findViewById(R.id.event_comments);
        comment_write = parent.findViewById(R.id.comment_write);
        add_comment = parent.findViewById(R.id.add_comment);

        comment_write.addTextChangedListener(textWatcher);

        add_comment.setEnabled(false);

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        // Reuse the favorite sites list.
        commentsAdapter = new SimpleAdapter(getActivity(), comments, R.layout.site_list_row,
                new String[]{"user","comment"}, new int[]{R.id.siteName, R.id.siteDescription});
        event_comments.setAdapter(commentsAdapter);

        event_dates.setLayoutManager(new GridLayoutManager(getActivity(),2));

        participants = new ArrayList<User>();

        EventFirebaseService.getEvent(event_id, this);

        /*
        ListViewInScrollView.setListViewHeightBasedOnChildren(event_participants);
        event_participants.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ListViewInScrollView.setListViewHeightBasedOnChildren(event_comments);
        event_comments.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        */

        return parent;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                add_comment.setEnabled(false);
            } else {
                add_comment.setEnabled(true);
            }
        }
    };

    private void addComment() {
        if(currentUser != null) {
            EventFirebaseService.AddComment(
                    new Comment(currentUser.username, comment_write.getText().toString()),
                    event_id);

            comment_write.setText("");
        }
    }

    public void setUpEventView(Event event){
        shownEvent = event;

        event_name.setText(event.name);
        event_name_edit.setText(event.name);
        event_state.setText(event.state);
        event_description.setText(event.description);
        event_description_edit.setText(event.description);
        event_site_name.setText(event.site.name);
        event_site_description.setText(event.site.description);

        voteDateListAdapter = new VoteDateListAdapter(event.possible_dates,user_id,event_id);
        event_dates.setAdapter(voteDateListAdapter);

        participantsAdapter = new MemberListAdapter(getActivity(),R.id.event_participants,participants);
        event_participants.setAdapter(participantsAdapter);

        if(event.participants_id != null)
        for (String user: event.participants_id) {
            UserFirebaseService.getUser(user,this);
        }

        if(event.comments != null)
        for (Comment c : event.comments.values()){
            getEventComment(c);
        }
    }

    private void getEventComment(Comment c) {
        comments.add(getCommentHashMap(c.author,c.text));
        commentsAdapter.notifyDataSetChanged();
    }

    private HashMap<String, String> getCommentHashMap(String user, String comment) {
        HashMap<String,String> item = new HashMap<String,String>();
        item.put("user", user);
        item.put("comment", comment);
        return item;
    }

    User currentUser;
    public void addUser(User user) {
        if(user.id.equals(user_id)) currentUser = user;
        participants.add(user);
        participantsAdapter.notifyDataSetChanged();
    }
}
