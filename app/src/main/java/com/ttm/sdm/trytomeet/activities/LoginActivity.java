package com.ttm.sdm.trytomeet.activities;

import android.content.res.Configuration;
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
import com.ttm.sdm.trytomeet.R;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private int GOOGLE_SIGN_IN_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configuramos como sera el login de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //El callback del boton se tiene que añadir mediante un listener
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Vemos si el usuario ya ha iniciado sesion en la app
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){//Lo ha hecho
            go_to_main_screen();
        }
    }

    private void go_to_main_screen(){
        Log.e("GOOGLE", "cargar la pantalla principal");
        //destruir y eliminar de la pila esta actividad

        finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Gestionamos los resultados de la actividad
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado de la actividad de login con Google
        if (requestCode == GOOGLE_SIGN_IN_RESULT_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                //Nos hemos logeado correctmente con Google
                go_to_main_screen();
            } catch (ApiException e) {
                e.printStackTrace();
                //VER CUANDO PUEDE FALLAR
                //GESTIONAR ERRORES
            }
        }
    }
}
