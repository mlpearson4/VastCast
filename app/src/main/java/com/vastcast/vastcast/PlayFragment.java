package com.vastcast.vastcast;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
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
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class PlayFragment extends Fragment {
    private Boolean playing = false;
    private Boolean initialStage = true;
    private SeekBar seekBar;
    private ImageButton audio;
    private Runnable runnable;
    private URL source = null;
    private TextView txtCurrentTime;
    private ItemTouchHelper touchHelper;
    private View view;
    private Handler handler;
    private QueueAdapter adapter;
    private TextView txtEpisodeTitle;
    private TextView txtTotalTime;
    private String podcastUID;
    private Collection currentPodcast;
    private Integer currentEpisode;
    private Boolean reversed;
    private ArrayList<Episode> queue;
    private FirebaseUser user;
    private static DatabaseReference userData;
    private Integer timeSkip = 10;
    PlayService mService;
    boolean mBound = false;
    boolean resetTimer;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Play Create", "jsut created");
        view = inflater.inflate(R.layout.fragment_play, container, false);

        handler = new Handler();

        seekBar = view.findViewById(R.id.seekbarEpisode);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if (input){
                    mService.seekToAudio(progress);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        audio = view.findViewById(R.id.ibPlayPause);
        audio.setImageResource(R.drawable.ic_play_arrow_24dp);
        audio.setEnabled(false);
        Log.d("Play", "source: " + source);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playing){
                    /*if (mService.getSource().equals(source.toString())){
                        mService.resetAudio(source.toString());
                        playing = true;
                        initialStage = false;
                    }*/
                    if (initialStage){
                        mService.resetAudio();
                        new Player().execute(source);
                        //mService.startAudio();
                        audio.setImageResource(R.drawable.ic_pause_24dp);
                    }
                    else {
                        mService.startAudio();
                        audio.setImageResource(R.drawable.ic_pause_24dp);
                    }
                    playing = true;
                }
                else{
                    mService.pauseAudio();
                    audio.setImageResource(R.drawable.ic_play_arrow_24dp);
                    playing = false;
                }
            }
        });

        final ImageButton replay = view.findViewById(R.id.ibReplay);
        final ImageButton forward = view.findViewById(R.id.ibSkip);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String userID = user.getUid();
            DatabaseReference valuesRef = rootRef.child("Users").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("Settings"))
                    {
                        DataSnapshot settings = dataSnapshot.child("Settings");

                        if(settings.hasChild("timeSkip"))
                        {
                            DataSnapshot timeSkipSnapshot = settings.child("timeSkip");
                            if(timeSkipSnapshot != null) {
                                timeSkip = timeSkipSnapshot.getValue(Integer.class);
                                if(timeSkip != null) {
                                    switch(timeSkip){
                                        case 5:
                                            replay.setImageResource(R.drawable.ic_replay_5_24dp);
                                            forward.setImageResource(R.drawable.ic_forward_5_24dp);
                                            break;
                                        case 10:
                                            replay.setImageResource(R.drawable.ic_replay_10_24dp);
                                            forward.setImageResource(R.drawable.ic_forward_10_24dp);
                                            break;
                                        case 30:
                                            replay.setImageResource(R.drawable.ic_replay_30_24dp);
                                            forward.setImageResource(R.drawable.ic_forward_30_24dp);
                                            break;
                                    }
                                }
                            }
                        }
                        audio.setImageResource(R.drawable.ic_pause_24dp);
                    }

                    replay.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v) {
                            if (mService.isNull() != null) {
                                mService.seekToAudio(mService.getCurrentAudio() - (timeSkip * 1000));
                            }
                        }
                    });

                    forward.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            if(mService.isNull() != null) {
                                mService.seekToAudio(mService.getCurrentAudio() + (timeSkip * 1000));
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}

            };
            valuesRef.addValueEventListener(eventListener);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Queue");
            ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue() != null) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            switch(item.getKey()) {
                                case "uid":{
                                    podcastUID = item.getValue(String.class);
                                    Log.d("New podcastUID", podcastUID);
                                    break;
                                }
                                case "currentEpisode": {
                                    currentEpisode = item.getValue(Integer.class);
                                    break;
                                }
                                case "currentPodcast": {
                                    currentPodcast = item.getValue(Collection.class);
                                    if(currentPodcast != null) {
                                        queue = currentPodcast.getEpisodes();
                                    }
                                    break;
                                }
                                case "reversed": {
                                    reversed = item.getValue(Boolean.class);
                                    break;
                                }
                            }
                        }
                        if(podcastUID!=null && currentEpisode != null && currentPodcast != null && reversed != null) {
                            if(reversed) {
                                currentEpisode = queue.size() - currentEpisode - 1;
                                Collections.reverse(queue);
                            }

                            txtEpisodeTitle = view.findViewById(R.id.txtEpisodeTitle);
                            txtEpisodeTitle.setText(currentPodcast.getEpisodes().get(currentEpisode).getTitle());
                            txtEpisodeTitle.setSelected(true);
                            txtCurrentTime = view.findViewById(R.id.txtCurrentTime);

                            txtTotalTime = view.findViewById(R.id.txtTotalTime);
                            txtTotalTime.setText(currentPodcast.getEpisodes().get(currentEpisode).makeDurationText());
                            source = currentPodcast.getEpisodes().get(currentEpisode).makeLink();
                            Log.d("New source", source.toString());
                            audio.setEnabled(true);

                            URL image = currentPodcast.makeImage();
                            if(image != null) {
                                ImageView imgPlayPodcast = view.findViewById(R.id.imgPodcast);
                                new LoadImageTask(imgPlayPodcast).execute(image);
                            }

                            TextView txtPodcastName = view.findViewById(R.id.txtPodcastTitle);
                            txtPodcastName.setText(currentPodcast.getTitle());
                            txtPodcastName.setSelected(true);

                            RecyclerView rvQueue = view.findViewById(R.id.rvQueue);
                            rvQueue.setHasFixedSize(true);
                            rvQueue.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            adapter = new QueueAdapter();
                            rvQueue.setAdapter(adapter);
                            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
                            touchHelper = new ItemTouchHelper(callback);
                            touchHelper.attachToRecyclerView(rvQueue);

                            resetTimer = true;
                            initialStage = true;
                            playing = false;

                            ImageButton previous = view.findViewById(R.id.ibPrevious);
                            previous.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(currentEpisode - 1 >= 0) {
                                        currentEpisode--;
                                        initialStage = true;
                                        Episode episode = queue.get(currentEpisode);
                                        source = episode.makeLink();
                                        txtEpisodeTitle.setText(episode.getTitle());
                                        txtTotalTime.setText(episode.makeDurationText());
                                        mService.pauseAudio();
                                        audio.setImageResource(R.drawable.ic_play_arrow_24dp);
                                        resetTimer = true;
                                        adapter.notifyDataSetChanged();
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        if(user != null) {
                                            DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                            userData.child("Queue").child("currentEpisode").setValue(currentEpisode);
                                        }
                                    }
                                }
                            });

                            ImageButton next = view.findViewById(R.id.ibNext);
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(currentEpisode + 1 < queue.size()) {
                                        currentEpisode++;
                                        initialStage = true;
                                        Episode episode = queue.get(currentEpisode);
                                        source = episode.makeLink();
                                        txtEpisodeTitle.setText(episode.getTitle());
                                        txtTotalTime.setText(episode.makeDurationText());
                                        mService.pauseAudio();
                                        audio.setImageResource(R.drawable.ic_play_arrow_24dp);
                                        resetTimer = true;
                                        adapter.notifyDataSetChanged();
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        if(user != null) {
                                            DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                            userData.child("Queue").child("currentEpisode").setValue(currentEpisode);
                                        }
                                    }
                                }
                            });

                            ImageButton ibRightPodcast = view.findViewById(R.id.ibRightPodcast);
                            ibRightPodcast.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    //creating a popup menu
                                    PopupMenu popup = new PopupMenu(getContext(), view);
                                    //inflating menu from xml resource
                                    popup.inflate(R.menu.menu_detail);
                                    //adding click listener
                                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            switch (item.getItemId()) {
                                                case R.id.menuOne:
                                                    Log.d("PlayFragment", "Menu 1 has been clicked");
                                                    Intent i = new Intent(PlayFragment.this.getActivity(), DetailActivity.class);
                                                    i.putExtra("uid", podcastUID);
                                                    i.putExtra("podcast", currentPodcast);
                                                    PlayFragment.this.startActivity(i);
                                                    break;
                                            }
                                            return false;
                                        }
                                    });
                                    //displaying the popup
                                    popup.show();

                                }
                            });

                            final ImageButton ibLeftPodcast = view.findViewById(R.id.ibLeftPodcast);
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null) {
                                userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                final DatabaseReference episodePlayed = userData.child("Played").child(podcastUID).child(Integer.toString(currentEpisode));
                                episodePlayed.addValueEventListener(new ValueEventListener() {
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Integer playedStatus = dataSnapshot.child("playedStatus").getValue(Integer.class);
                                        if (playedStatus == null) {
                                            ibLeftPodcast.setImageResource(R.drawable.ic_diamond_hollow_24dp);
                                            txtCurrentTime.setText(R.string.zero_time);
                                        }
                                        else if (playedStatus == 1) {
                                            ibLeftPodcast.setImageResource(R.drawable.ic_diamond_partial_24dp);
                                            Integer currentTime = dataSnapshot.child("currentTime").getValue(Integer.class);
                                            if (currentTime != null) {
                                                long minutes = TimeUnit.SECONDS.toMinutes(currentTime);
                                                txtCurrentTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                                                        minutes, currentTime -
                                                                TimeUnit.MINUTES.toSeconds(minutes)
                                                ));
                                            }
                                        }
                                        else {
                                            ibLeftPodcast.setImageResource(R.drawable.ic_diamond_filled_24dp);
                                        }
                                        ibLeftPodcast.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                if (playedStatus == null || playedStatus == 1) {
                                                    episodePlayed.child("playedStatus").setValue(2);
                                                } else {
                                                    episodePlayed.removeValue();
                                                    mService.seekToAudio(0);
                                                }
                                            }
                                        });
                                    }
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        }
                    }
                }
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        Log.i("OnStart", "MainActivity - onStart-binding");
        doBindToService();
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
                Log.e("PlayFragment", Log.getStackTraceString(e));
            }
            else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }

    class Time extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings){
            int milliseconds;
            long minutes;
            while(!resetTimer){
                if(mService.isPlaying()) {
                    milliseconds = mService.getCurrentAudio();
                    minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                    txtCurrentTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                            minutes, TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                                    TimeUnit.MINUTES.toSeconds(minutes)
                    ));

                }
                seekBar.setProgress(mService.getCurrentAudio());
                handler.postDelayed(runnable, 1000);
            }
            return false;
        }

    }

    class Player extends AsyncTask<URL, Void, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(URL... urls){
            return mService.initPlayer(urls[0].toString());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);

            seekBar.setMax(mService.getDurationAudio());
            resetTimer = false;
            new Time().execute();

            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
            initialStage = false;
            mService.startAudio();
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }

    class QueueAdapter extends RecyclerView.Adapter<ViewHolder> {

        public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast_episode, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ImageButton ibLeftEpisode = holder.view.findViewById(R.id.ibLeftEpisode);
            ibLeftEpisode.setImageResource(R.drawable.ic_remove_24dp);
            ibLeftEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(holder.getAdapterPosition());
                }
            });

            TextView txtEpisodeTitle = holder.view.findViewById(R.id.txtEpisodeTitle);
            txtEpisodeTitle.setText(queue.get(position + currentEpisode + 1).getTitle());

            TextView txtTotalTime = holder.view.findViewById(R.id.txtTotalTime);
            txtTotalTime.setText(queue.get(position + currentEpisode + 1).makeDurationText());

            ImageButton ibRightEpisode = holder.view.findViewById(R.id.ibRightEpisode);
            ibRightEpisode.setImageResource(R.drawable.ic_drag_handle_24dp);
            ibRightEpisode.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                    }
                    return false;
                }
            });

            final TextView txtEpisodeTitleInner = view.findViewById(R.id.txtEpisodeTitle);
            final TextView txtTotalTimeInner = view.findViewById(R.id.txtTotalTime);

            holder.view.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    currentEpisode = holder.getAdapterPosition() + currentEpisode + 1;
                    initialStage = true; //some other stuff necessary probably
                    Episode episode = queue.get(currentEpisode);
                    source = episode.makeLink();
                    txtEpisodeTitleInner.setText(episode.getTitle());
                    txtTotalTimeInner.setText(episode.makeDurationText());
                    mService.pauseAudio();
                    audio.setImageResource(R.drawable.ic_play_arrow_24dp);
                    resetTimer = true;

                    adapter.notifyDataSetChanged();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {
                        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userData.child("Queue").child("currentEpisode").setValue(currentEpisode);
                    }
                }
            });
        }

        public int getItemCount() {
            return queue.size() - currentEpisode - 1;
        }

        public void onItemDismiss(int position) {
            queue.remove(position + currentEpisode + 1);
            notifyItemRemoved(position);
        }

        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(queue, i + currentEpisode + 1, i + currentEpisode + 1 + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(queue, i + currentEpisode + 1, i + currentEpisode + 1 - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final QueueAdapter mAdapter;

        ItemTouchHelperCallback(QueueAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("OnStop", "MainActivity - onStop - unbinding");

        //Intent intentStopService = new Intent(getActivity(), PlayService.class);
        //getActivity().stopService(intentStopService);
        doUnbindService();
        //mService.resetAudio(source.toString());
        mBound = false;

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            Log.i("ServiceConnection", "Bound service connected");
            mService = ((PlayService.LocalBinder) service).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    private void doUnbindService() {
        if(getActivity() != null) {
            getActivity().unbindService(mConnection);
        }

    }

    private void doBindToService() {
        //Toast.makeText(getActivity(), "Binding...", Toast.LENGTH_SHORT).show();
        if (!mBound) {
            Intent bindIntent = new Intent(getActivity(), PlayService.class);
            bindIntent.putExtra("DATA_SOURCE", source);

            if(getActivity() != null) {
                getActivity().startService(bindIntent);
                getActivity().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("OnDestroy", "Destroying activity...");
        if (getActivity() != null && getActivity().isFinishing()){
            Log.i("OnDestroy", "activity is finishing");
            Intent intentStopService = new Intent(getActivity(), PlayService.class);
            getActivity().stopService(intentStopService);
            doUnbindService();
        }
    }
}
