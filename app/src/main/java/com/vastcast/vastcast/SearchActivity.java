package com.vastcast.vastcast;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    //private SearchAdapter mSearchAdapter;
    private DatabaseReference mUserDatabase;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private ArrayList<String>uidKeys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        handleIntent(getIntent());
    }

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

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            firebaseUserSearch(query);
        }
    }

    private void firebaseUserSearch(final String searchText) {
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Database");
        mUserDatabase.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<Collection> podcasts = new ArrayList<>();
                    uidKeys = new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String s = data.child("title").getValue(String.class);
                    if(s.toLowerCase().contains(searchText.toLowerCase())){
                        Collection c = data.getValue(Collection.class);
                        String key = data.getKey();
                        podcasts.add(c);
                        uidKeys.add(key);
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.rvSearchList);
                    int numberOfColumns = 2;
                    recyclerView.setLayoutManager(new android.support.v7.widget.GridLayoutManager(SearchActivity.this, numberOfColumns));
                    SearchActivity.rvAdapter adapter = new SearchActivity.rvAdapter(podcasts);
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        class rvAdapter extends RecyclerView.Adapter<SearchActivity.rvAdapter.ViewHolder> {

        private ArrayList<Collection> podcasts;
        // Data is passed into the constructor
        rvAdapter(ArrayList<Collection> data) {
            this.podcasts = data;
        }
        // Inflates the cell layout from xml when needed
        @Override
        public @NonNull SearchActivity.rvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast, parent, false);
            return new SearchActivity.rvAdapter.ViewHolder(view);
        }

        // Binds the titles and images to the view in each cell
        @Override
        public void onBindViewHolder(@NonNull SearchActivity.rvAdapter.ViewHolder holder, int position) {
            // Get and set the title
            String title = podcasts.get(position).getTitle();
            holder.txtTitle.setText(title);
            holder.txtTitle.setSelected(true);
            // Get and set the image
            java.net.URL image = podcasts.get(position).makeImage();
            new SearchActivity.LoadImageTask(holder.imgPodcast).execute(image);
        }

        @Override
        public int getItemCount() {
            return podcasts.size();
        }
        // Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txtTitle;
            public ImageView imgPodcast;
            // Find and set the text and image views
            ViewHolder(View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.info_text);
                imgPodcast = itemView.findViewById(R.id.imgPodcast);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Collection podcast = podcasts.get(position);
                Intent i = new Intent(SearchActivity.this, DetailActivity.class);
                i.putExtra("uid", uidKeys.get(position));
                i.putExtra("podcast", podcast);
                SearchActivity.this.startActivity(i);
            }
        }
    }
    // asynchronously load an image and place it into the specified ImageView
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
            if (e != null) {
                //Handle errors in loading an image
                Log.e("SearchActivity", Log.getStackTraceString(e));
            } else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }
}