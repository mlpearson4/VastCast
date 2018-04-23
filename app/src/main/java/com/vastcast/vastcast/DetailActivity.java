package com.vastcast.vastcast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbarAddFeed);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            /*TODO: Have Up Direct to Manage Page*/
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        Collection podcast = (Collection) i.getSerializableExtra("podcast");
        ImageView imgPodcast = findViewById(R.id.imgPodcast);
        new LoadImageTask(imgPodcast).execute(podcast.makeImage());

        TextView txtPodcastName = findViewById(R.id.txtPodcastTitle);
        txtPodcastName.setText(podcast.getTitle());

        TextView txtPodcastDescription = findViewById(R.id.txtPodcastDescription);
        txtPodcastDescription.setText(podcast.getDescription());

        Button btnAddRemove = findViewById(R.id.btnAddRemove);
        btnAddRemove.setEnabled(false);
        /*TODO: Make Add/Remove affect library in database*/

        RecyclerView episodeList = findViewById(R.id.rvEpisodeList);
        episodeList.setHasFixedSize(true);
        episodeList.setLayoutManager(new LinearLayoutManager(this));
        episodeList.setAdapter(new PodcastAdapter(podcast));
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
                Log.e("DetailActivity", Log.getStackTraceString(e));
            }
            else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }

    class PodcastAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Collection podcast;

        PodcastAdapter(Collection podcast) {
            this.podcast = podcast;
        }

        public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast_episode, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {
            TextView txtEpisodeTitle = holder.view.findViewById(R.id.txtEpisodeTitle);
            txtEpisodeTitle.setText(podcast.getEpisodes().get(position).getTitle());

            TextView txtTotalTime = holder.view.findViewById(R.id.txtTotalTime);
            txtTotalTime.setText(podcast.getEpisodes().get(position).getDurationText());

            holder.view.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    /*TODO: Change to pass episode to play through database*/
                    Intent i = new Intent(DetailActivity.this, MainActivity.class);
                    i.putExtra("toPlay", true);
                    i.putExtra("currentPlaylist", podcast);
                    i.putExtra("currentEpisode", podcast.getEpisodes().get(holder.getAdapterPosition()));
                    i.putExtra("currentEpisodeNumber", holder.getAdapterPosition());
                    i.putExtra("reversed", false);
                    DetailActivity.this.startActivity(i);
                }
            });

            /*TODO: Do something with left and right episode buttons*/
        }

        public int getItemCount() {
            return podcast.getEpisodes().size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        ViewHolder(View v) {
            super(v);
            view = v;
        }
    }
}
