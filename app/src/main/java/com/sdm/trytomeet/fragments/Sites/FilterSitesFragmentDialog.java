package com.sdm.trytomeet.fragments.Sites;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.sdm.trytomeet.R;
import com.sdm.trytomeet.services.FilterPlacesService;

public class FilterSitesFragmentDialog extends DialogFragment {

    private View parent;
    private double latitude;
    private double longitude;

    public FilterSitesFragmentDialog() {
    }

    public static FilterSitesFragmentDialog newInstance(double latitude, double longitude){
        FilterSitesFragmentDialog res = new FilterSitesFragmentDialog();

        // Insert the argument
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        res.setArguments(args);

        return res;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance)
    {
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");

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

        parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_filter_site_dialog, null);

        builder.setView(parent);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onContinueClick();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    private void onContinueClick() {
        RadioGroup radioGroup = (RadioGroup) parent.findViewById(R.id.radioGroup_Type);
        String selectedRadio = ((RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
        String type = "";
        if(selectedRadio.equals(getString(R.string.bar))) type = "bar";
        if(selectedRadio.equals(getString(R.string.park))) type = "park";
        if(selectedRadio.equals(getString(R.string.shops))) type = "shopping_mall";

        Spinner spinner = (Spinner) parent.findViewById(R.id.spinner_radio);
        int radio = Integer.parseInt(spinner.getSelectedItem().toString());

        new FilterPlacesService((FindPlaceFragment) getTargetFragment()).executeService(latitude,longitude,radio,type);
    }
}
