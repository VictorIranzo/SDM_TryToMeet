package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.POJO.Comment;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Profile.View_external_user;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    Context context;
    int resource;
    private List<Comment> data;

    public CommentListAdapter(Context context, int resource, List<Comment> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.fragment_event_comment, null);
        }

        Comment comment = data.get(position);
        ((TextView) convertView.findViewById(R.id.user_comment_author)).setText(comment.author);
        ((TextView) convertView.findViewById(R.id.comment_description)).setText(comment.text);

        if(comment.date != null)
        ((TextView) convertView.findViewById(R.id.comment_date)).setText(comment.date);

        final View view = convertView;
        if(comment.image!=null){
            CircularImageView profileImage = view.findViewById(R.id.user_comment_image);
            Glide.with(context).load(comment.image).into(profileImage);
        }

        return convertView;

    }
}
