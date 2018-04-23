package com.sdm.trytomeet.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdm.trytomeet.R;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private int GOOGLE_SIGN_IN_RESULT_CODE = 1;
    CallbackManager callbackManager;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configuration of how to log with Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        MainActivity.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Adding the callback for the Google Sign_in
        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = MainActivity.mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RESULT_CODE);
            }
        });
        Button signInFacebook= findViewById(R.id.sign_in_facebook);
        signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFBLoginForProfile();
            }
        });
        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();

        int action = getIntent().getIntExtra("action", 0); // Used when coming from a notification

        //Checking if the user has already started session with our app
        MainActivity.accountGoogle = GoogleSignIn.getLastSignedInAccount(this);
        if(MainActivity.accountGoogle != null){//He/she did it
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("account_id", MainActivity.accountGoogle.getId());
            editor.putString("account_email",MainActivity.accountGoogle.getEmail());
            editor.putString("account_name", MainActivity.accountGoogle.getDisplayName());
            editor.apply();
            if(action == 0) go_to_main_screen();
        }
        else if (  mAuth.getCurrentUser()!=null){

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("account_id",  mAuth.getCurrentUser().getUid());
            editor.putString("account_email", mAuth.getCurrentUser().getEmail());
            editor.putString("account_name",  mAuth.getCurrentUser().getDisplayName());
            editor.apply();
            MainActivity.accountFacebook=mAuth.getCurrentUser();
            if(action == 0) go_to_main_screen();
        }
        if(action != 0) { // Pass the intent with the arguments from the notification
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            if(action == 2){
                intent.putExtra("action",  getIntent().getIntExtra("action",0));
                intent.putExtra("group_id",  getIntent().getStringExtra("group_id"));
            }
            else{
                intent.putExtra("action", getIntent().getIntExtra("action",0));
                intent.putExtra("event_id", getIntent().getStringExtra("event_id"));
            }
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        }
    }

    protected void doFBLoginForProfile() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        loginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                System.out.println(error.getStackTrace());

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("account_id", user.getUid());
                            editor.putString("account_email",user.getEmail());
                            editor.putString("account_name", user.getDisplayName());
                            editor.apply();
                            MainActivity.accountFacebook= user;
                            go_to_main_screen();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



    private void go_to_main_screen(){
        //We detroy the current activity and launch the main one
        finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Manage the results of an activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The result is from the google sign in activity
        if (requestCode == GOOGLE_SIGN_IN_RESULT_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                MainActivity.accountGoogle = task.getResult(ApiException.class);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("account_id", MainActivity.accountGoogle.getId());
                editor.putString("account_email", MainActivity.accountGoogle.getDisplayName());
                editor.putString("account_name", MainActivity.accountGoogle.getEmail());
                editor.apply();
                //User has properly logged in with google
                go_to_main_screen();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
