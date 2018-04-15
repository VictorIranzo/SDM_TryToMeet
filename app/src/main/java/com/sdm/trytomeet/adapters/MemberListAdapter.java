package com.sdm.trytomeet.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Profile.View_external_user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrymc96 on 30/03/18.
 */

public class MemberListAdapter extends ArrayAdapter<User>{

    Context context;
    int resource;
    private List<User> data;

    public MemberListAdapter(Context context, int resource, List<User> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.members_layout, null);
        }
        final User user = data.get(position);
        ((TextView) convertView.findViewById(R.id.memberName)).setText(user.username);

        if(user.image!=null){
            byte[] decodedString = Base64.decode(user.image, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            CircularImageView profileImage =convertView.findViewById(R.id.circleImage);
            profileImage.setImageBitmap(image);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View_external_user external_user = View_external_user.newInstance(user);
                external_user.show(((FragmentActivity)context).getSupportFragmentManager(), "dialog");
            }
        });

        return convertView;

    }
}

























