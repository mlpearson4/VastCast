package com.vastcast.vastcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_KEY = "LOGIN_KEY";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if (pref.getBoolean(LOGIN_KEY, false)) {
            //has login, so start main activity
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));

            //must finish this activity (the login activity will not be shown when click back in main activity)
            finish();
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                pref.edit().putBoolean(LOGIN_KEY, true).apply();
                finish();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}
