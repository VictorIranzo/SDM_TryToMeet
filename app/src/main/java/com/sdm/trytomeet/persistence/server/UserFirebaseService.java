package com.sdm.trytomeet.persistence.server;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdm.trytomeet.POJO.Friends;
import com.sdm.trytomeet.POJO.Group;
import com.sdm.trytomeet.POJO.Notification;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.fragments.Events.CreateEventFragment;
import com.sdm.trytomeet.fragments.Events.EventFragment;
import com.sdm.trytomeet.fragments.Sites.FavoriteSitesFragment;
import com.sdm.trytomeet.fragments.Groups.GroupsFragment;
import com.sdm.trytomeet.fragments.Groups.MembersFragment;
import com.sdm.trytomeet.fragments.Profile.ProfileFragment;
import com.sdm.trytomeet.notifications.NotificactionListener;
import com.sdm.trytomeet.fragments.Sites.FindPlaceFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserFirebaseService extends FirebaseService {
    // TODO: Revisar si esto se puede hacer con un push y guardando la key.
    public static void addUser(final String account_id, final String account_name, final MainActivity mainActivity) {
        /* TODO: PONERLO EN LA VENTANA DE LOGIN
        * Esto se deberá hacer solo cuando alguien se loguee por primera vez en la aplicacion, no siempre
        * Ahora va aqui ya que algunos de nosotros ya nos hemos logueado con la cuenta
        */
        // We store the current user in our DB (if they do not exist)
        getDatabaseReference().child("users").child(account_id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User me = mutableData.getValue(User.class);
                if (me == null) {
                    me = new User();
                    me.username = account_name;
                    me.id = account_id;
                    mutableData.setValue(me);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // TODO: Revisar esto con Adrián.
                UserFirebaseService.getUserFromDrawerHeader(account_id, mainActivity);
            }
        });
    }

    public static void getUser(String user_id, final EventFragment eventFragment)
    {
        getDatabaseReference().child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User u = dataSnapshot.getValue(User.class);
            eventFragment.addUser(u);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Error", "Something bad");
        }
    });
    }

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

    public static void getUserFromProfile(String user_id, final ProfileFragment profileFragment) {
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

    public static void getUserFromDrawerHeader(String user_id, final MainActivity mainActivity) {
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

    public static void setUserImage(final String user_id, final Uri image, final Activity activity) {
        StorageReference path = getStorageReference().child("images").child(image.getLastPathSegment());
        path.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDatabaseReference().child("users").child(user_id).child("image").setValue(taskSnapshot.getDownloadUrl().toString());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                getUserFromDrawerHeader(prefs.getString("account_id",""), (MainActivity) activity);
            }
        });
    }

    public static void setUserName(String user_id, String name) {
        getDatabaseReference().child("users").child(user_id).child("username").setValue(name);
    }

    public static void getUsersFromMembersOfGroup(List <String> ids, final MembersFragment fragment) {
        for (String s : ids) {
            getDatabaseReference().child("users").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    fragment.addMemberToList(user);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Error", "Something bad");
                }
            });
        }

    }
    public static void addFriend(final String user_id, final String friend_email, final ProfileFragment fragment) {

        getDatabaseReference().child("email").child(friend_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue(String.class);
                if(user!=null && user!=user_id) addFriendChecked(user_id,user,fragment);
                else fragment.addedFriendSuccessfully(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });

    }


    private static void addFriendChecked(final String user_id, final String friend,final ProfileFragment fragment) {

        getDatabaseReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String msg;
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        if (friends != null) {
                            if (friends.friends.contains(friend) == false) {
                                friends.friends.add(friend);
                                getDatabaseReference().child("friends").child(user_id).setValue(friends);
                            }
                        } else {
                            Friends f = new Friends(new ArrayList<String>());
                            f.friends.add(friend);
                            getDatabaseReference().child("friends").child(user_id).setValue(f);
                        }
                        fragment.addedFriendSuccessfully(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void removeFriend(final String user_id, final List<User> friends_remove, final ProfileFragment fragment) {

        getDatabaseReference().child("friends").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() { // Get my friends
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        if (friends != null) {
                            for (User u : friends_remove)
                                friends.friends.remove(u.id);
                            getDatabaseReference().child("friends").child(user_id).setValue(friends);
                        }
                        fragment.removedFriendSuccessfully(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        fragment.removedFriendSuccessfully(false);

                    }
                });
    }

    public static void createGroup(final Group group, String user_id, String notification_title, String notification_text) {

        String key = getDatabaseReference().child("groups").child(group.members.get(0)).push().getKey();
        group.uniqueIdentifier=key;
        for (String u : group.members) {
            getDatabaseReference().child("groups").child(u).child(key).setValue(group);

            // Create the notifications for the others users
            if(!user_id.equals(u)){
                Notification not = new Notification(Notification.ADDED_TO_AN_EVENT,
                        notification_title, notification_text);
                not.group_id = key;
                NotificationFirebaseService.addNotification(not, u);
            }
        }
    }

    public static void getGroups(final String user_id, final GroupsFragment groupFragment) {
        getDatabaseReference().child("groups").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Group> groups = new ArrayList<Group>();
                while (iterator.hasNext()) {
                    Group group = iterator.next().getValue(Group.class);
                    groups.add(group);
                }
                groupFragment.addGroupsToList(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Something bad");
            }
        });
    }

    public static void addFriendToGroup(final String friend,  final Group group, String title, String text) {


            getDatabaseReference().child("groups").child(friend).child(group.uniqueIdentifier).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Group groupToAdd = mutableData.getValue(Group.class);
                    if(groupToAdd!=null){

                        groupToAdd.members.add(friend);
                    }
                    else{
                        group.members.add(friend);
                        groupToAdd = group ;
                    }
                    mutableData.setValue(groupToAdd);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });

            for (String id: group.members){
                getDatabaseReference().child("groups").child(id).child(group.uniqueIdentifier).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Group groupToAdd = mutableData.getValue(Group.class);
                        if(groupToAdd!=null){

                            groupToAdd.members.add(friend);
                        }
                        else{
                            groupToAdd = group;
                        }
                        mutableData.setValue(groupToAdd);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }

            // Notify that user
            Notification not = new Notification(Notification.ADDED_TO_A_GROUP, title, text);
            not.group_id = group.uniqueIdentifier;
            NotificationFirebaseService.addNotification(not, friend);

    }
    public static void exitFromGroup(final String user_id,final Group group) {

        getDatabaseReference().child("groups").child(user_id).child(group.uniqueIdentifier).setValue(null);
        for (String id: group.members){
            getDatabaseReference().child("groups").child(id).child(group.uniqueIdentifier).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Group groupToAdd = mutableData.getValue(Group.class);
                    if (groupToAdd != null) {

                        groupToAdd.members.remove(user_id);
                        mutableData.setValue(groupToAdd);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }
    }

    public static void registerEmail(final String user_id, final String email) {

        getDatabaseReference().child("email").child(email).setValue(user_id);
    }
}

