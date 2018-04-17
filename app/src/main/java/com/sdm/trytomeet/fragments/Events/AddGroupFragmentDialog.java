package com.sdm.trytomeet.fragments.Events;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.AddGroupListAdapter;
import com.sdm.trytomeet.adapters.AddParticipantListAdapter;

import java.util.ArrayList;

public class AddGroupFragmentDialog extends DialogFragment {

    private View parent;
    private String user_id;
    private ArrayList<Group> my_groups;
    private ArrayList<String> current_participants;
    private ListView list_view;


    public AddGroupFragmentDialog() {
        // Required empty public constructor
    }

    public static AddGroupFragmentDialog newInstance(String user_id, ArrayList<String> current_participants){
        AddGroupFragmentDialog res = new AddGroupFragmentDialog();

        // Insert the argument
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putSerializable("current_participants", current_participants);
        res.setArguments(args);

        return res;
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        user_id = getArguments().getString("user_id");
        current_participants = (ArrayList<String>) getArguments().getSerializable("current_participants");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    // Since the dialog is set as not cancelable, but we still want the normal behaviour for the back button
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_group_dialog, null);

        // We configure the list view
        my_groups = new ArrayList<>();
        list_view = parent.findViewById(R.id.list_view);
        final AddGroupListAdapter adapter = new AddGroupListAdapter(getContext(), R.id.list_view, my_groups);
        list_view.setAdapter(adapter);

        EditText editText = parent.findViewById(R.id.editTextAddGroup);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        // We populate the group list
        FirebaseDatabase.getInstance().getReference().child("groups").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> it =  dataSnapshot.getChildren();
                        for(DataSnapshot data : it){
                            Group group = data.getValue(Group.class);
                            my_groups.add(group);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CreateEventFragment)getTargetFragment()).add_group(adapter.getToAdd());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
