package com.sdm.trytomeet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.sdm.trytomeet.POJO.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdm.trytomeet.POJO.User;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.adapters.EventListAdapter;
import com.sdm.trytomeet.components.CircularImageView;
import com.sdm.trytomeet.fragments.Events.CreateEventFragment;
import com.sdm.trytomeet.fragments.Events.EventListFragment;
import com.sdm.trytomeet.fragments.Sites.FavoriteSitesFragment;

import com.sdm.trytomeet.fragments.Groups.GroupsFragment;
import com.sdm.trytomeet.fragments.Profile.ProfileFragment;
import com.sdm.trytomeet.notifications.NotificationService;
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
    private Intent service;

    private User actualUser;

    private List<Event> events;
    private List<Event> evento;

    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameLayout, new EventListFragment());
        transaction.commit();*/


        /*rv = (RecyclerView)findViewById(R.id.rv);

        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);*/
        //rv.setHasFixedSize(true);


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

        /*initializeData();
        initializeAdapter();*/
    }




        /*private void initializeData(){
        events = new ArrayList<>();
        evento = EventFirebaseService.nameEvent(account.getId());
        Iterator<Event> iterator = evento.iterator();
        while(iterator.hasNext()){
            Event eve = iterator.next();
            if (eve != null){
                events.add(new Event(eve.name, eve.description));
            }
        }
        //if (!iterator.hasNext()){initializeData();}
        //events.add(new Event(EventFirebaseService.nameEvent(account.getId()), "23 years old"));
        //events.add(new Event("Lavery Maiss", "25 years old"));
        //events.add(new Event("Lillie Watts", "35 years old"));
    }*/

    private void initializeAdapter(){
        adapter = new EventListAdapter(events);
        rv.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        service = new Intent(this, NotificationService.class);
        this.startService(service);
        if(accountFacebook!=null) { id=accountFacebook.getUid(); name=accountFacebook.getDisplayName(); email=accountFacebook.getEmail();}
        else if(accountGoogle!=null) {id= accountGoogle.getId(); name=accountGoogle.getDisplayName(); email=accountGoogle.getEmail();}

        UserFirebaseService.addUser(id,name, this);
        UserFirebaseService.registerEmail(id, cleanEmail(email));


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
            // TODO: Review if here must be added more code, for example, the notifications.
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

            case R.id.drawer_menu_profile:
                goToProfile();
                break;


            case R.id.drawer_menu_events:
                goToEventList();
                break;

            case R.id.drawer_menu_groups:
                goToGroups();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void goToGroups() {
        GroupsFragment fragment = new GroupsFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    private void goToCreateEvent() {
        CreateEventFragment fragment = new CreateEventFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    private void goToFavoriteSites() {
        FavoriteSitesFragment fragment = new FavoriteSitesFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id",id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    private void goToProfile() {
        ProfileFragment fragment = new ProfileFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    private void goToEventList() {
        EventListFragment fragment = new EventListFragment();
        // Insert the arguments
        Bundle args = new Bundle();
        args.putString("user_id", id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    public static String cleanEmail(String string) {
        return string.replace(".", ",");
    }
}
