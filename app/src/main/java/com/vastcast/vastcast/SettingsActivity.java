package com.vastcast.vastcast;

import android.content.Intent;
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
import android.preference.Preference;

public class SettingsActivity extends PreferenceActivity {

    private FirebaseUser user;
    private DatabaseReference userData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Preference pref = findPreference("btnLogout");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO Auto-generated method stub
                FirebaseAuth.getInstance().signOut();
                Log.d("SettingsActivity", "Logout button pressed");
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SettingsActivity.this.startActivity(i);
                return false;
            }
        });

        OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                /*Boolean test = (key.equals("autoplay_preference"));
                if(key.equals("autoplay_preference")) {
                    Boolean preference_autoplay = sp.getBoolean("autoplay_preference", false);
                    if(user != null) {
                        userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userData.child("Settings").child("autoplay").setValue(preference_autoplay);
                    }
                }
                else*/
                if(key.equals("skipper_preference")) {
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
                }
            }
        };

        sp.registerOnSharedPreferenceChangeListener(listener);

    }

}