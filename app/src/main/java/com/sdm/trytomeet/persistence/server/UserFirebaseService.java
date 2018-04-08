package com.sdm.trytomeet.persistence.server;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.sdm.trytomeet.POJO.User;

public class UserFirebaseService extends FirebaseService{

    // TODO: Revisar si esto se puede hacer con un push y guardando la key.
    public static void addGoogleUser(final GoogleSignInAccount account){
        /* TODO: PONERLO EN LA VENTANA DE LOGIN
        * Esto se deberá hacer solo cuando alguien se loguee por primera vez en la aplicacion, no siempre
        * Ahora va aqui ya que algunos de nosotros ya nos hemos logueado con la cuenta
        */
        // We store the current user in our DB (if they do not exist)
        getDatabaseReference().child("users").child(account.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User me = mutableData.getValue(User.class);
                if(me == null) {
                    me = new User();
                    me.username = account.getDisplayName();
                    me.id = account.getId();
                    mutableData.setValue(me);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Do nothing
            }
        });
    }
}
