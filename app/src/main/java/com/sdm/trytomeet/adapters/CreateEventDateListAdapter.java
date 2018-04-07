package com.sdm.trytomeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sdm.trytomeet.R;

import java.util.ArrayList;

import com.sdm.trytomeet.POJO.Date;

/**
 * Created by adrymc96 on 30/03/18.
 */

public class CreateEventDateListAdapter extends ArrayAdapter<Date>{

    Context context;
    int resource;
    ArrayList<Date> data;

    public CreateEventDateListAdapter(Context context, int resource, ArrayList<Date> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.create_event_date_layout, null);
        }
        final Date date = data.get(position);
        ((TextView) convertView.findViewById(R.id.date_text)).setText(getDateText(date));
        ((Button) convertView.findViewById(R.id.delete_date_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(date);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private String getDateText(Date date){
        // TODO: POSIBLE INTERNACIOLIZACION DE LA FECHA
        String res = "";
        res += date.year + "-" + date.month + "-" + date.day;
        res += ", ";
        if(date.hour < 10) res += "0";
        res += date.hour + ":";
        if(date.minute < 10) res += "0";
        res += date.minute;
        return res;
    }
}

























