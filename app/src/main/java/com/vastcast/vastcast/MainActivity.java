package com.vastcast.vastcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tbMain = findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);

        TabLayout tlMain = findViewById(R.id.tlMain);
        final ViewPager vpMain = findViewById(R.id.vpMain);
        vpMain.addOnPageChangeListener(new CircularOnPageChangeListener(vpMain));
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tlMain.getContext(), tlMain.getTabCount());
        vpMain.setAdapter(adapter);
        tlMain.setupWithViewPager(vpMain);

        /*TODO: Determine the best way to direct MainActivity to the desired tab (Use Database?)*/

        Log.d("MainActivity", "Start");
        Intent i = getIntent();
        if(i.hasExtra("toPlay")) {
            Episode e = (Episode) i.getSerializableExtra("currentEpisode");
            Collection c = (Collection) i.getSerializableExtra("currentPlaylist");
            int episodeNumber = i.getIntExtra("currentEpisodeNumber", 0);
            boolean reversed = i.getBooleanExtra("reversed", false);
            adapter.setPlayArguments(e, c, episodeNumber, reversed);
            vpMain.setCurrentItem(1);
        }
        else {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("currentPage");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer value = dataSnapshot.getValue(Integer.class);
                        if(value != null) {
                            vpMain.setCurrentItem(value);
                        }
                    }
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }



    }

    /*TODO: Add Search to Menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSettings:
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

        CircularOnPageChangeListener(final ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        public void onPageScrollStateChanged(final int state) {
            int currentPosition = viewPager.getCurrentItem();
            int lastPosition = 0;
            if(viewPager.getAdapter() != null) {
                lastPosition = viewPager.getAdapter().getCount() - 1;
            }
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
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("currentPage").setValue(viewPager.getCurrentItem());
            }
        }
    }
}
