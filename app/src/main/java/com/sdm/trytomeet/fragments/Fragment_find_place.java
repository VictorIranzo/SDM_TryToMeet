package com.sdm.trytomeet.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.trytomeet.R;

import java.util.ArrayList;

public class Fragment_find_place extends Fragment{
    private View parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public Fragment_find_place() {
        // Required empty public constructor
    }

    public static Fragment_find_place newInstance(){
        return new Fragment_find_place();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_add_place, container, false);

        return parent;
    }
}
