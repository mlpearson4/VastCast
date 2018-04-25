package com.vastcast.vastcast;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}