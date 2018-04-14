package com.vastcast.vastcast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.URL;

public class AddFeedActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        Toolbar toolbar = findViewById(R.id.toolbarAddFeed);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*TODO: Check Settings to determine connection status and preference*/
        Button btnEnterUrl = findViewById(R.id.btnEnterUrl);
        final EditText txtUrl = findViewById(R.id.txtUrl);
        btnEnterUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new RetrieveFeedTask().execute(txtUrl.getText().toString());
            }
        });
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Collection> {
        private Exception e = null;

        protected Collection doInBackground(String... urls) {
            try {
                URL source = new URL(urls[0]);
                return RSSFetcher.fetch(source);
            } catch(Exception e) {
                this.e = e;
                return null;
            }
        }

        protected void onPostExecute(Collection c) {
            if(e != null) {
                //Handle errors in reading a feed
                Log.e("RSS", Log.getStackTraceString(e));
            }
            else {
                Intent i = new Intent(AddFeedActivity.this, DetailActivity.class);
                i.putExtra("podcast", c);
                AddFeedActivity.this.startActivity(i);
            }
        }
    }
}
