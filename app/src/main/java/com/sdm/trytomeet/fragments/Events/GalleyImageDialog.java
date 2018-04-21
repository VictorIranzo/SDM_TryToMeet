package com.sdm.trytomeet.fragments.Events;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.R;

public class GalleyImageDialog extends DialogFragment {

    private View parent;


    public GalleyImageDialog() {
        // Required empty public constructor
    }

    public static GalleyImageDialog newInstance(){
        GalleyImageDialog res = new GalleyImageDialog();

        return res;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_gallery_image, null);
        ImageView imageView = parent.findViewById(R.id.image_gallery);
        Glide.with(parent).load(getArguments().getString("image")).into(imageView);

        builder.setView(parent);


        return builder.create();
    }
}
