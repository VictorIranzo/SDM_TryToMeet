package com.sdm.trytomeet.activities;

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.sdm.trytomeet.R;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private int GOOGLE_SIGN_IN_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configuration of how to log with Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        MainActivity.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Adding the callback fot the Google Sign_in
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = MainActivity.mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Checking if the user has already started session with our app
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){//He/she did it
            go_to_main_screen();
        }
    }

    private void go_to_main_screen(){
        //We detroy the current activity and launch the main one
        finish();

        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        startActivity(intent);
    }

    //Manage the results of an activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The result is from the google sign in activity
        if (requestCode == GOOGLE_SIGN_IN_RESULT_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                //User has properly logged in with google
                go_to_main_screen();
            } catch (ApiException e) {
                e.printStackTrace();
                //VER CUANDO PUEDE FALLAR
                //GESTIONAR ERRORES
            }
        }
    }
}
