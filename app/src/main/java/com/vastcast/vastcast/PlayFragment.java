package com.vastcast.vastcast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class PlayFragment extends Fragment {
    Boolean playing = false;
    Boolean initialStage = true;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Button audio;
    Handler handler;
    Runnable runnable;
    URL image = null;
    URL source = null;

    public static PlayFragment newInstance(Episode e, Collection c) {
        PlayFragment fragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("episode", e);
        bundle.putSerializable("collection", c);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        Bundle arguments = getArguments();

        if(arguments != null) {
            Collection c = (Collection) getArguments().getSerializable("collection");
            if(c != null) image = c.getImage();
        }
        if(image != null) {
            ImageView imgPlayPodcast = view.findViewById(R.id.imgPlayPodcast);
            new LoadImageTask(imgPlayPodcast).execute(image);
        }

        handler = new Handler();
        seekBar = view.findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if (input){
                    mediaPlayer.seekTo(progress);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        audio = view.findViewById(R.id.audioStreamBtn);
        try {
            source = new URL("http://leopard.megaphone.fm/PPY7869295725.mp3");
        }
        catch(Exception e) {
            Log.e("PlayFragment", Log.getStackTraceString(e));
        }


        if(arguments != null) {
            Episode e = (Episode) getArguments().getSerializable("episode");
            if(e != null) {
                TextView title = view.findViewById(R.id.textEpisodeTitle);
                title.setText(e.getTitle());
                source = e.getLink();
                audio.setEnabled(true);
            }
        }
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playing){
                    if (initialStage){
                        new Player().execute(source);
                    }
                    else{
                        if (!mediaPlayer.isPlaying()) {
                            playCycle();
                            mediaPlayer.start();
                        }
                    }
                    audio.setText(R.string.pauseMedia);
                    playing = true;
                }
                else{
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    audio.setText(R.string.playMedia);
                    playing = false;
                }
            }
        });

        ImageButton btnQueue = view.findViewById(R.id.btnQueue);
        btnQueue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlayFragment.this.startActivity(new Intent(getActivity(), QueueActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();

        if (mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playCycle(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()){
            runnable = new Runnable(){
                @Override
                public void run(){
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);
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
                //Handle errors in loading an image
                Log.e("PlayFragment", Log.getStackTraceString(e));
            }
            else {
                imgPodcast.setImageBitmap(bitmap);
            }
        }
    }

    class Player extends AsyncTask<URL, Void, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(URL... urls){
            Boolean prepared = false;
//
//            try {
//                mediaPlayer.setDataSource(urls[0].getPath());
//                prepared = true;
//                //seekBar.setMax(mediaPlayer.getDuration());
////                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////                    @Override
////                    public void onCompletion (MediaPlayer mediaPlayer){
////                        initialStage = true;
////                        playing = false;
////                        audio.setText(R.string.playMedia);
////                        mediaPlayer.stop();
////                        mediaPlayer.reset();
////                    }
////                });
//            } catch(Exception e) {
//                Log.e("PlayFragment", Log.getStackTraceString(e));
//                prepared = false;
//            }
//            if(prepared) {
//                try{
//                    mediaPlayer.prepare();
//                    seekBar.setMax(mediaPlayer.getDuration());
//                    playCycle();
//                } catch(IOException e){
//                    Log.e("PlayFragment", Log.getStackTraceString(e));
//                }
//            }
//            return prepared;
            try {
                mediaPlayer.setDataSource(urls[0].getPath());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.prepareAsync();
                prepared = true;
            } catch(Exception e) {
                prepared = false;
                Log.e("PlayFragment", Log.getStackTraceString(e));
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);

            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
//            initialStage = false;
//            mediaPlayer.start();
//            playCycle();
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }
}
