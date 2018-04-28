package com.vastcast.vastcast;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlayService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    boolean prepared2 = false;

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        PlayService getService(){
            return PlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

    }

    public boolean initPlayer(String url){
        Log.i("OnStartCommand", "Service started by startService()");

        boolean prepared = false;

        Log.e("input string", url);
        try {
            mediaPlayer.setDataSource(url);
        }catch(IOException e) {
            Log.e("On Start Command", "Set Data Source Failed");
        }
        try {
            mediaPlayer.prepare();
            prepared = true;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    prepared2 = true;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.reset();
                }
            });
        }catch (IOException e){
            Log.e("On Start Command", "Prepare Failed");
        }

        return prepared;
    }

    public void startAudio(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void pauseAudio(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public MediaPlayer isNull(){
        return mediaPlayer;
    }

    public void seekToAudio(int location){
        mediaPlayer.seekTo(location);
    }

    public int getDurationAudio(){
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public int getCurrentAudio(){
        return mediaPlayer.getCurrentPosition();
    }

    public void resetAudio() {
        mediaPlayer.pause();
        mediaPlayer.reset();
    }
}
