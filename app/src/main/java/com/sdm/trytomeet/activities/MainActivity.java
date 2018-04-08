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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.fragments.CreateEventFragment;
import com.sdm.trytomeet.fragments.FindPlaceFragment;

import com.sdm.trytomeet.notifications.NotificationService;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount account;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Intent service;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        service = new Intent(this, NotificationService.class);
        this.startService(service);

        UserFirebaseService.addGoogleUser(account);
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
                editor.apply();
                mGoogleSignInClient.signOut();
                finish();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.drawer_menu_create_event:
                CreateEventFragment fragment = new CreateEventFragment();
                // Insert the arguments
                Bundle args = new Bundle();
                args.putString("user_id", account.getId());
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout, fragment).commit();
                break;
            case R.id.profile:
                FindPlaceFragment fragment2 = new FindPlaceFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment2).commit();
        }
        drawerLayout.closeDrawers();
        return true;
    }
}
