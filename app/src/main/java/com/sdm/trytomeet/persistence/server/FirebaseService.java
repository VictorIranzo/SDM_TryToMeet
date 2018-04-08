package com.sdm.trytomeet.persistence.server;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private static DatabaseReference databaseReference;

    protected static DatabaseReference getDatabaseReference(){
        if(databaseReference == null) databaseReference = FirebaseDatabase.getInstance().getReference();

        return databaseReference;
    }
}
