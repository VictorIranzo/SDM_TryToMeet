package com.sdm.trytomeet.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.fragments.Fragment_create_event;

import java.util.ArrayList;
import java.util.HashMap;

import POJO.Event;
import POJO.Friends;
import POJO.Group;
import POJO.Groups;
import POJO.User;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount account;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private DatabaseReference mDatabase;

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
        drawerLayout.openDrawer(GravityCompat.START);

        // Getting access to DB
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* TODO: PONERLO EN LA VENTANA DE LOGIN
        * Esto se deberá hacer solo cuando alguien se loguee por primera vez en la aplicacion, no siempre
        * Ahora va aqui ya que algunos de nosotros ya nos hemos logueado con la cuenta
        */
        // We store the current user in our DB (if they do not exist)
        mDatabase.child("users").child(account.getId()).runTransaction(new Transaction.Handler() {
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
            // TODO: Review if here must be added more code.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                // Log out and return to the login activity
                mGoogleSignInClient.signOut();
                finish();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.drawer_menu_create_event:
                Fragment_create_event fragment = new Fragment_create_event();
                // Insert the arguments
                Bundle args = new Bundle();
                args.putString("user_id", account.getId());
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout, fragment).commit();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }
}
