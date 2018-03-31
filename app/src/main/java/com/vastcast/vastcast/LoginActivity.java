package com.vastcast.vastcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_KEY = "LOGIN_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if (pref.getBoolean(LOGIN_KEY, false)) {
            //has login, so start main activity
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));

            //must finish this activity (the login activity will not be shown when click back in main activity)
            finish();
        }

        Button button = (Button) findViewById(R.id.btnLogin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                pref.edit().putBoolean(LOGIN_KEY, true).apply();
                finish();
            }
        });

    }
}
