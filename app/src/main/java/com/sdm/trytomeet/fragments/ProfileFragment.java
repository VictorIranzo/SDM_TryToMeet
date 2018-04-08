package com.sdm.trytomeet.fragments;

import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private View parent;
    private String user_id;

    public static final int GET_FROM_GALLERY = 1;

    private CircularImageView profileImage;
    private TextView userName;
    private Button editButton;
    private LinearLayout panelChangeName;
    private Button cancelButton;
    private Button okButton;
    private EditText editName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_profile, container, false);
        user_id = getArguments().getString("user_id");

        profileImage = parent.findViewById(R.id.circleImage);

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

        UserFirebaseService.getUserFromProfile(user_id,this);

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

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                String image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                UserFirebaseService.setUserImage(user_id,image);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateImageAndName(User user) {
        userName.setText(user.username);
        editName.setText(user.username);
        byte[] decodedString = Base64.decode(user.image, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profileImage.setImageBitmap(image);
    }
}
