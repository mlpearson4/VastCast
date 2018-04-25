package com.vastcast.vastcast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

public class ManageFragment extends Fragment {

    MyRecyclerViewAdapter adapter;
    private View view;
    private ArrayList<String> uidKeys;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Get DatabaseReference for all podcasts in database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String userID=user.getUid();
            DatabaseReference valuesRef = rootRef.child("User").child(userID).child("Library");
            ValueEventListener eventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Collect all podcasts from database into an ArrayList
                    ArrayList<Collection> podcasts = new ArrayList<>();
                    uidKeys = new ArrayList<>();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Collection thisPodcast = ds.getValue(Collection.class);
                        String thisKeys = ds.getKey();
                        podcasts.add(thisPodcast);
                        uidKeys.add(thisKeys);
                    }
                    // RecyclerView setup with GridLayoutManager
                    RecyclerView recyclerView = view.findViewById(R.id.rvManage);
                    int numberOfColumns = 2;
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
                    adapter = new MyRecyclerViewAdapter(podcasts);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            valuesRef.addValueEventListener(eventListener);
        }

        return view;
    }

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Collection> podcasts;

        // Data is passed into the constructor
        MyRecyclerViewAdapter(ArrayList<Collection> data) {
            this.podcasts = data;
        }

        // Inflates the cell layout from xml when needed
        @Override
        public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast, parent, false);
            return new ViewHolder(view);
        }

        // Binds the titles and images to the view in each cell
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Get and set the title
            String title = podcasts.get(position).getTitle();
            holder.txtTitle.setText(title);
            holder.txtTitle.setSelected(true);

            // Get and set the image
            URL image = podcasts.get(position).makeImage();
            new LoadImageTask(holder.imgPodcast).execute(image);
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
                Intent i = new Intent(ManageFragment.this.getActivity(), DetailActivity.class);
                i.putExtra("uid", uidKeys.get(position));
                i.putExtra("podcast", podcast);
                ManageFragment.this.startActivity(i);
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
                Log.e("ManageFragment", Log.getStackTraceString(e));
            } else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }
}
