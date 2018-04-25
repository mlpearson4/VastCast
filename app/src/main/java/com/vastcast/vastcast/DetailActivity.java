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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSettings:
                //launch settings with an intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {
                        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userData.child("currentPage").setValue(1);
                        userData.child("Queue").child("currentPodcast").setValue(podcast);
                        userData.child("Queue").child("currentEpisode").setValue(holder.getAdapterPosition());
                        /*TODO: add ability to filter or replace below with setting*/
                        userData.child("Queue").child("reversed").setValue(false);
                        Intent i = new Intent(DetailActivity.this, MainActivity.class);
                        DetailActivity.this.startActivity(i);
                    }
                    else {
                        Log.e("DetailActivity", "Tried to play episode without being logged in");
                    }
                }
            });

            // This is a menu for the ibRightEpisode - discontinued, but here if we decide to add it back in
            /*ImageButton ibRightEpisode = holder.view.findViewById(R.id.ibRightEpisode);
            ibRightEpisode.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getBaseContext(), view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_detail);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menuOne:
                                    Log.d("DetailActivity", "Menu 1 has been clicked");
                                    //handle menu1 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });
            */

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
