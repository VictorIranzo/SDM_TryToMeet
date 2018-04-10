package com.sdm.trytomeet.persistence.server;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.fragments.AddFriendFragmentDialog;
import com.sdm.trytomeet.fragments.FavoriteSitesFragment;
import com.sdm.trytomeet.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class UserFirebaseService extends FirebaseService{
    // TODO: Revisar si esto se puede hacer con un push y guardando la key.
    public static void addGoogleUser(final GoogleSignInAccount account, final MainActivity mainActivity){
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
                // TODO: Revisar esto con Adrián.
                UserFirebaseService.getUserFromDrawerHeader(account.getId(), mainActivity);
            }
        });
    }

    public static void getUserFavoriteSites(String user_id, final FavoriteSitesFragment favoriteSitesFragment){
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Site> favouriteSites = new ArrayList<Site>();
                while (iterator.hasNext()){
                    Site s = iterator.next().getValue(Site.class);
                    favouriteSites.add(s);
                }
                favoriteSitesFragment.addSitesToList(favouriteSites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void removeFavoriteSite(String user_id, final Site selectedSite) {
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot data = iterator.next();
                    Site s = data.getValue(Site.class);
                    if(s.equals(selectedSite)){
                        data.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static String addUserFavoriteSite(String user_id, Site site) {
        String key = getDatabaseReference().child("users").child(user_id).child("favorite_sites").push().getKey();
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").child(key).setValue(site);

        return key;
    }

    public static void getUserFromProfile(String user_id, final ProfileFragment profileFragment){
        getDatabaseReference().child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                profileFragment.updateImageAndName(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getUserFromDrawerHeader(String user_id, final MainActivity mainActivity){
        getDatabaseReference().child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mainActivity.setHeaderDrawer(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setUserImage(String user_id, String image)
    {
        getDatabaseReference().child("users").child(user_id).child("image").setValue(image);
    }

    public static void setUserName(String user_id, String name) {
        getDatabaseReference().child("users").child(user_id).child("username").setValue(name);
    }

    public static void addFriend(final String user_id, final String friend_name){
        getDatabaseReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User u= new User();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot data = iterator.next();
                    final User s = data.getValue(User.class);
                    if((s.username).equals(friend_name)){
                        u.username=s.username;
                        u.id= s.id;
                        addFriendChecked(user_id,u);

                    }
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });



        }


    private static void addFriendChecked(final String user_id,final User u) {

        getDatabaseReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String msg;
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        if (friends != null) {
                            if (friends.friends.contains(u.id) == false) {
                                friends.friends.add(u.id);
                                getDatabaseReference().child("friends").child(user_id).setValue(friends);
                            }
                        } else {
                            Friends f = new Friends(new ArrayList<String>());
                            f.friends.add(u.id);
                            getDatabaseReference().child("friends").child(user_id).setValue(f);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void removeFriend(final String user_id, final List<User> friends_remove){

        getDatabaseReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        if (friends != null) {
                                for ( User u : friends_remove)
                                friends.friends.remove(u.id);
                                getDatabaseReference().child("friends").child(user_id).setValue(friends);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });







    }


}

