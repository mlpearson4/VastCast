package com.vastcast.vastcast;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private ViewPager vpMain;
    private DatabaseReference mUserDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tbMain = findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TabLayout tlMain = findViewById(R.id.tlMain);
        vpMain = findViewById(R.id.vpMain);
        vpMain.addOnPageChangeListener(new CircularOnPageChangeListener(vpMain));
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tlMain.getContext(), tlMain.getTabCount());
        vpMain.setAdapter(adapter);
        tlMain.setupWithViewPager(vpMain);

    }

    @Override
    protected void onStart() {
        super.onStart();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        inflater.inflate(R.menu.menu_settings, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

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
