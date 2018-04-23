package com.sdm.trytomeet.persistence.server;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.fragments.Events.CreateEventFragment;
import com.sdm.trytomeet.fragments.Sites.FavoriteSitesFragment;
import com.sdm.trytomeet.fragments.Sites.FindPlaceFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SitesFirebaseService extends FirebaseService{

    public static void getUserFavoriteSites(String user_id, final FavoriteSitesFragment favoriteSitesFragment) {
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Site> favouriteSites = new ArrayList<Site>();
                while (iterator.hasNext()) {
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

    public static void getUserFavoriteSites(String user_id, final FindPlaceFragment findPlaceFragment) {
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Site> favouriteSites = new ArrayList<Site>();
                while (iterator.hasNext()) {
                    Site s = iterator.next().getValue(Site.class);
                    favouriteSites.add(s);
                }
                findPlaceFragment.addSitesToList(favouriteSites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void checkIfFavouriteSite(final Site site, String user_id, final CreateEventFragment createEventFragment) {
        getDatabaseReference().child("users").child(user_id).child("favorite_sites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Site> favouriteSites = new ArrayList<Site>();
                while (iterator.hasNext()) {
                    Site s = iterator.next().getValue(Site.class);
                    if(s.equals(site)) return;
                }
                createEventFragment.showAddFavourite();
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
                while (iterator.hasNext()) {
                    DataSnapshot data = iterator.next();
                    Site s = data.getValue(Site.class);
                    if (s.equals(selectedSite)) {
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
}
