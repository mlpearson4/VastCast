package com.vastcast.vastcast;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

public class AddFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        /*TODO: Add a listener to launch RetrieveFeedTask on button press*/
        /*TODO: Check Settings to determine connection status and preference*/
        try {
            new RetrieveFeedTask().execute("https://rss.simplecast.com/podcasts/1497/rss").get();
        }
        catch(Exception e) {
            Log.e("RSS", Log.getStackTraceString(e));
        }
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
        /*TODO: Launch new activity with the resulting podcast details*/
        Log.d("Eps", Integer.toString(c.getEpisodes().size()));
        printEpisodes(c);
    }

    private void printEpisodes(Collection podcast) {
        ArrayList<Episode> episodes = podcast.getEpisodes();
        for(int i = 0; i < episodes.size(); i++) {
            Log.d("Eps", episodes.get(i).getTitle());
            try {
                Thread.sleep(1);
            } catch (Exception e){
                Log.e("Eps", Log.getStackTraceString(e));
            }
        }
    }
}
