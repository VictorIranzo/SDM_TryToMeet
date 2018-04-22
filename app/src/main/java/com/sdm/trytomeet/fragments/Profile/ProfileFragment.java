package com.sdm.trytomeet.fragments.Profile;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ProfileFragment extends Fragment {

    private View parent;
    private String user_id;

    public static final int GET_FROM_GALLERY = 1;
    public static final int ADD_FRIEND= 2;
    public static final int REMOVE_FRIEND= 3;


    private CircularImageView profileImage;
    private TextView userName;
    private Button editButton;
    private LinearLayout panelChangeName;
    private Button cancelButton;
    private Button okButton;
    private EditText editName;
    ToggleButton toggle;

    private String currentImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.frg_profile, container, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

        profileImage = parent.findViewById(R.id.circleImage);
        toggle= (ToggleButton) parent.findViewById(R.id.toggleButton);

        if(prefs.getBoolean("notifications",true))
            toggle.setChecked(true);
        else toggle.setChecked(false);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage();
            }
        });

        userName = parent.findViewById(R.id.username);
        editName = parent.findViewById(R.id.editUserName);

        editButton = parent.findViewById(R.id.button_edit_name);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName();
            }
        });

        okButton = parent.findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okButtonClick();
            }
        });

        cancelButton = parent.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonClick();
            }
        });

        panelChangeName = parent.findViewById(R.id.editNamePanel);

        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.Profile_title));

        UserFirebaseService.getUserFromProfile(user_id,this);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("notifications",true);
                    edit.commit();
                    getActivity().startService(MainActivity.service);
                } else {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("notifications",false);
                    edit.commit();
                    getActivity().stopService(MainActivity.service);
                }
            }
        });
        return parent;
    }

    private void cancelButtonClick() {
        editButton.setVisibility(View.VISIBLE);
        panelChangeName.setVisibility(View.GONE);
        userName.setVisibility(View.VISIBLE);
    }

    private void okButtonClick() {
        String newName = editName.getText().toString();
        UserFirebaseService.setUserName(user_id, newName);
        cancelButtonClick();
        ((MainActivity) getActivity()).setHeaderDrawer(new User(newName,currentImage));
        userName.setText(newName);
    }

    private void editName() {
        editButton.setVisibility(View.GONE);
        panelChangeName.setVisibility(View.VISIBLE);
        userName.setVisibility(View.GONE);
    }

    private void setImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                profileImage.setImageBitmap(bitmap);
                CircularImageView userImage = getActivity().findViewById(R.id.drawer_user_image);

                UserFirebaseService.setUserImage(user_id, selectedImage, getActivity());

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public void updateImageAndName(User user) {
        userName.setText(user.username);
        editName.setText(user.username);

        if( user.image != null){
            Glide.with(this).load(user.image).into(profileImage);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("account_name", user.username);
        editor.apply();
    }



}
