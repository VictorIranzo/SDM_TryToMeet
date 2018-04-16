package com.sdm.trytomeet.components;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdm.trytomeet.R;

public class CardView extends Activity {

    TextView eventName;
    TextView eventDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.card_view);
        eventName = (TextView)findViewById(R.id.name);
        eventDescription = (TextView)findViewById(R.id.description);

        /*eventName.setText("Prueba");
        eventDescription.setText("¿Funciona?");*/
    }
}