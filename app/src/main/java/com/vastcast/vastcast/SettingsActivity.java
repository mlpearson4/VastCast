package com.vastcast.vastcast;

import android.content.SharedPreferences;
import 	android.preference.PreferenceManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends PreferenceActivity {

    private FirebaseUser user;
    private DatabaseReference userData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("SettingsActivity", key);
                Boolean test = (key.equals("autoplay_preference"));
                Log.d("SettingsActivity", test.toString());
                if(key.equals("autoplay_preference")) {
                    Boolean preference_autoplay = sp.getBoolean("autoplay_preference", false);
                    Log.d("SettingsActivity", "in autoplay_preference");
                    if(user != null) {
                        userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userData.child("Settings").child("autoplay").setValue(preference_autoplay);
                        Log.d("SettingsActivity", preference_autoplay.toString());
                    }
                }
                else if(key.equals("skipper_preference")) {
                    String preference_skipper = sp.getString("skipper_preference", "30");
                    Log.d("SettingsActivity", preference_skipper);
                }
                /*else if(key.equals("skipper_preference")) {
                    String preference_skipper = sp.getString("skipper_preference", "30");

                    final int timeSkip;
                    switch (preference_skipper) {
                        case "5":
                            timeSkip = 5;
                            break;
                        case "10":
                            timeSkip = 10;
                            break;
                        case "30":
                            timeSkip = 30;
                            break;
                        default:
                            timeSkip = 30;
                            break;
                    }

                    if(user != null) {
                        userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userData.child("Settings").child("timeSkip").setValue(timeSkip);
                    }
                }*/
            }
        };

        sp.registerOnSharedPreferenceChangeListener(listener);

    }

}