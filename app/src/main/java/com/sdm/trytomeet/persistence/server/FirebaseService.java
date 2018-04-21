package com.sdm.trytomeet.persistence.server;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseService {
    private static DatabaseReference databaseReference;
    private static StorageReference storageReference;

    protected static DatabaseReference getDatabaseReference(){
        if(databaseReference == null){
            FirebaseDatabase aux = FirebaseDatabase.getInstance();
            //aux.setPersistenceEnabled(false);
            databaseReference = aux.getReference();
        }

        return databaseReference;
    }

    protected  static StorageReference getStorageReference(){
        if(storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }

        return storageReference;
    }
}
