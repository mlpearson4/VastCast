package com.vastcast.vastcast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Boolean playing = false;
    Boolean initialStage = true;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Button audio;
    Handler handler;
    Runnable runnable;
    //ImageView album;
    //ProgressDialog progressDialog;
    String url;

    private OnFragmentInteractionListener mListener;

    public PlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance(String param1, String param2) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        ImageButton btnQueue = (ImageButton) view.findViewById(R.id.btnQueue);

        url = "http://leopard.megaphone.fm/PPY7869295725.mp3";

        //album = (ImageView) findViewById(R.id.image);
        //need to handle image loading
        audio = (Button) view.findViewById(R.id.audioStreamBtn);
        //audio = (Button) view.findViefwById(R.id.audioStreamBtn);
        //progressDialog = new ProgressDialog(getContext());
        handler = new Handler();
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //new Player().execute(url);
        //seekBar.setMax(mediaPlayer.getDuration());
        //playCycle();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if (input){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playing){
                    audio.setText("Pause");
                    if (initialStage){
                        new Player().execute(url);
                    }
                    else{
                        if (!mediaPlayer.isPlaying()) {
                            playCycle();
                            mediaPlayer.start();
                        }
                    }
                    playing = true;
                }
                else{
                    audio.setText("Play");
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    playing = false;
                }
            }
        });

        btnQueue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlayFragment.this.startActivity(new Intent(getActivity(), QueueActivity.class));
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    class Player extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(String... strings){
            Boolean prepared = false;

            try{
                mediaPlayer.setDataSource(strings[0]);
                //seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion (MediaPlayer mediaPlayer){
                        initialStage = true;
                        playing = false;
                        audio.setText("Launch Streaming");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                try{
                    mediaPlayer.prepare();
                    seekBar.setMax(mediaPlayer.getDuration());
                    playCycle();
                } catch(IOException e){
                    e.printStackTrace();
                }
                prepared = true;

            }
            catch(Exception e){
                Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);

            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
            initialStage = false;
            mediaPlayer.start();
            playCycle();
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
