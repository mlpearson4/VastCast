package com.vastcast.vastcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference();
    private StorageReference mStorageRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.pagerMain);
        viewPager.addOnPageChangeListener(new CircularOnPageChangeListener(viewPager)); //comment out this line to remove circular tabs
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getContext(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Intent i = getIntent();
        //when started with the intent to play an episode
        if(i.hasExtra("toPlay")) {
            Episode e = (Episode) i.getSerializableExtra("currentEpisode");
            Collection c = (Collection) i.getSerializableExtra("currentPlaylist");
            adapter.setPlayArguments(e, c);
            viewPager.setCurrentItem(1);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //launch settings with an intent
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class CircularOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private ViewPager viewPager;
        private int lastState;

        public CircularOnPageChangeListener(final ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            int currentPosition = viewPager.getCurrentItem();
            int lastPosition = viewPager.getAdapter().getCount() - 1;
            if(currentPosition == 0 || currentPosition == lastPosition) {
                if(state == ViewPager.SCROLL_STATE_IDLE && lastState == ViewPager.SCROLL_STATE_DRAGGING) {
                    if(currentPosition == 0) {
                        viewPager.setCurrentItem(lastPosition, false);
                    }
                    else {
                        viewPager.setCurrentItem(0, false);
                    }
                }
                lastState = state;
            }
        }
    }
}
