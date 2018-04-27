package com.vastcast.vastcast;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;



/*
HANDLE SWITCHING DATA SOURCES FOR QUEUE AND AUTOPLAY
 */


public class PlayService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String source;
    boolean prepared2 = false;

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("OnStartCommand", "Service started by startService()");

        source = intent.getStringExtra("DATA_SOURCE");

        Log.e("input string", source);
        try {
            mediaPlayer.setDataSource(source);
        }catch(IOException e) {
            Log.e("On Start Command", "Set Data Source Failed");
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Toast.makeText(PlayService.this, "Prepared", Toast.LENGTH_SHORT).show();
                    //mediaPlayer.start();
                    //configMediaPlayerState();
                }
            });
        }catch (IOException e){
            Log.e("On Start Command", "Prepare Failed");
        }

        return START_STICKY;
    }*/

    public boolean initPlayer(String url){
        Log.i("OnStartCommand", "Service started by startService()");

        boolean prepared = false;
        //mediaPlayer.reset();

        //source = intent.getStringExtra("DATA_SOURCE");

        source = url;

        Log.e("input string", source);
        try {
            mediaPlayer.setDataSource(source);
        }catch(IOException e) {
            Log.e("On Start Command", "Set Data Source Failed");
        }
        try {
            mediaPlayer.prepare();
            prepared = true;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //Toast.makeText(PlayService.this, "Prepared", Toast.LENGTH_SHORT).show();
                    prepared2 = true;
                    //mediaPlayer.start();
                    //mediaPlayer.seekTo(0);
                    //configMediaPlayerState();
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

    public boolean isPrepared(){
        return prepared2;
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

    public void resetAudio(/*String url*/) {
        //prepared2 = false;
        mediaPlayer.pause();
        mediaPlayer.reset();

        //mediaPlayer = new MediaPlayer();
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        /*try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            Log.e("reset Audio", "Set Data Source Failed");

        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Toast.makeText(PlayService.this, "Prepared", Toast.LENGTH_SHORT).show();
                    prepared2 = true;
                    mediaPlayer.start();
                    mediaPlayer.seekTo(0);
                    //configMediaPlayerState();
                }
            });
        }catch(IOException e){
            Log.e("prepare Audio", "Prepare failed");
        }
        //prepared2 = true;

        //mediaPlayer.seekTo(0);*/

    }

    public String getSource(){
        return source;
    }

    public void actualReset(){
        mediaPlayer.reset();
    }

}

