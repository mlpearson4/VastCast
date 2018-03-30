package com.vastcast.vastcast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        Collection podcast = (Collection) i.getSerializableExtra("podcast");
        ImageView imgPodcast = findViewById(R.id.imgPodcast);
        new LoadImageTask(imgPodcast).execute(podcast.getImage());

        RecyclerView episodeList = findViewById(R.id.rvEpisodeList);
        episodeList.setHasFixedSize(true);
        RecyclerView.LayoutManager podcastLayoutManager = new LinearLayoutManager(this);
        episodeList.setLayoutManager(podcastLayoutManager);
        RecyclerView.Adapter podcastAdapter = new PodcastAdapter(podcast);
        episodeList.setAdapter(podcastAdapter);
    }

    class LoadImageTask extends AsyncTask<URL, Void, Bitmap> {
        private Exception e;
        ImageView imgPodcast;

        LoadImageTask(ImageView imgPodcast) {
            this.imgPodcast = imgPodcast;
        }

        protected Bitmap doInBackground(URL... urls) {
            try {
                return BitmapFactory.decodeStream(urls[0].openStream());
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(e != null) {
                //Handle errors in loading an image
                Log.e("DetailActivity", Log.getStackTraceString(e));
            }
            else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }

    class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.ViewHolder> {
        private Collection podcast;

        public PodcastAdapter(Collection podcast) {
            this.podcast = podcast;
        }

        public PodcastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast_episode, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textView.setText(podcast.getEpisodes().get(position).getTitle());
            holder.textView.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    String title = podcast.getEpisodes().get(position).getTitle();
                    Log.d("DetailActivity", "Clicked on Episode: " + title);
                    //Below should allow for passing of data needed to play episodes
                    /*Intent i = new Intent(DetailActivity.this, PlayActivity.class);
                    i.putExtra("currentPlaylist", podcast);
                    i.putExtra("currentEpisodeLink", podcast.getEpisodes().get(position).getLink());
                    DetailActivity.this.startActivity(i);*/
                }
            });
        }

        public int getItemCount() {
            return podcast.getEpisodes().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;

            public ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.txtEpisodeTitle);
            }
        }
    }
}
