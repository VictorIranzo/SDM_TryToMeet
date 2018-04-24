package com.sdm.trytomeet.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.sdm.trytomeet.POJO.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdm.trytomeet.POJO.Notification;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Events.CreateEventFragment;
import com.sdm.trytomeet.fragments.Events.EventListFragment;
import com.sdm.trytomeet.fragments.Events.HistoricEventListFragment;
import com.sdm.trytomeet.fragments.Friends.FriendsFragment;
import com.sdm.trytomeet.fragments.Sites.FavoriteSitesFragment;

import com.sdm.trytomeet.fragments.Groups.GroupsFragment;
import com.sdm.trytomeet.fragments.Profile.ProfileFragment;
import com.sdm.trytomeet.notifications.NotificationService;
import com.sdm.trytomeet.persistence.server.NotificationFirebaseService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount accountGoogle;
    public static FirebaseUser accountFacebook;

    private String id;
    private String email;
    private String name;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    public static Intent service;

    private User actualUser;
    private int action;

    private List<Event> events;
    private List<Event> evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // As we're using a Toolbar, we should retrieve it and set it
        // to be our ActionBar
        Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        // Shows the home button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Creates the toggle associated to the drawer.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        NavigationView navigationView =
                (NavigationView) findViewById(R.id.navView);

        // Associates the toogle to receive events from the drawer.
        navigationView.setNavigationItemSelectedListener(this);

        action = getIntent().getIntExtra("action", 0); // Used to behave properly to the notifications

        if(action == 0 && getSupportFragmentManager().getBackStackEntryCount() == 0){
            // Default fragment
            Fragment fragment = new EventListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout, fragment, "event_list").commit();
        }
    }

    private void answer_to_notification(int action) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("event_list");
        switch (action){
            case Notification.ADDED_TO_AN_EVENT:
            case Notification.EVENT_CONFIRMATE:
            case Notification.COMMENT_ADDED:
            case Notification.IMAGE_UPLOADED:{
                if(fragment == null) fragment = new EventListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("goto", getIntent().getStringExtra("event_id"));
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment).commit();
                break;
            }
            case Notification.ADDED_TO_A_GROUP:{
                if(fragment == null){
                    fragment = new EventListFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, fragment, "event_list").commit();
                    fragment = new EventListFragment();
                }
                GroupsFragment fragment2 = new GroupsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment2).addToBackStack(null).commit();
            }
        }

        // Remove the notification
        String notification_id = getIntent().getStringExtra("id");
        NotificationManagerCompat.from(this).cancel(notification_id, 0);
        NotificationFirebaseService.removeNotification(id, notification_id);
    }

    public void setHeaderDrawer(final User user) {
        actualUser = user;
        TextView userName =  findViewById(R.id.drawer_user_name);
        userName.setText(user.username);


        if(user.image != null) {
            CircularImageView userImage = findViewById(R.id.drawer_user_image);
            Glide.with(this).load(user.image).into(userImage);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        service = new Intent(this, NotificationService.class);

        if(prefs.getBoolean("notificactions",true)){
        this.startService(service);}

        if(accountFacebook!=null) { id=accountFacebook.getUid(); name=accountFacebook.getDisplayName(); email=accountFacebook.getEmail();}
        else if(accountGoogle!=null) {id= accountGoogle.getId(); name=accountGoogle.getDisplayName(); email=accountGoogle.getEmail();}


        UserFirebaseService.addUser(id,name, this);
        UserFirebaseService.registerEmail(id, cleanEmail(email));


        if(action != 0) answer_to_notification(action); // If coming from notification
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Synchronize the toggle with the drawer state.
        toggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Synchronize the toggle with the drawer state.
        toggle.syncState();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                // Log out and return to the login activity
                this.stopService(service); // stop listenint notifications for the current account
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("account_id", "");
                editor.putString("account_email", "");
                editor.putString("account_name", "");
                editor.apply();
                if (accountGoogle!=null) {mGoogleSignInClient.signOut(); accountGoogle=null;}
                else if (accountFacebook!=null){ FirebaseAuth.getInstance().signOut(); accountFacebook=null;}
                finish();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.drawer_menu_create_event:
                goToCreateEvent();
                break;

            case R.id.drawer_menu_favorite_sites:
                goToFavoriteSites();
                break;

            case R.id.drawer_menu_friends:
                goToFriends();
                break;

            case R.id.drawer_menu_profile:
                goToProfile();
                break;


            case R.id.drawer_menu_events:
                goToEventList();
                break;

            case R.id.drawer_menu_groups:
                goToGroups();
                break;

            case R.id.drawer_menu_hystoric:
                goToHistoric();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void goToGroups() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("groups");
        if(fragment == null) fragment = new GroupsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("groups").commit();
    }
    private void goToFriends() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("friends");
        if(fragment == null) fragment = new FriendsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("friends").commit();
    }

    private void goToCreateEvent() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("create_events");
        if(fragment == null) fragment = new CreateEventFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("create_events").commit();
    }

    private void goToFavoriteSites() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("favorite");
        if(fragment == null) fragment = new FavoriteSitesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("favorite").commit();
    }

    private void goToProfile() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("profile");
        if(fragment == null) fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("profile").commit();
    }

    private void goToEventList() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("event_list");
        if(fragment == null) fragment = new EventListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment, "event_list").addToBackStack(null).commit();
    }

    private void goToHistoric() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("historic");
        if(fragment == null) fragment = new HistoricEventListFragment();
        // Insert the arguments
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).addToBackStack("historic").commit();
    }

    public static String cleanEmail(String string) {
        return string.replace(".", ",");
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
