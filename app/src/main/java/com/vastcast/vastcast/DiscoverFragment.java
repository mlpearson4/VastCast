package com.vastcast.vastcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        FloatingActionButton fabAddPodcast = view.findViewById(R.id.fabAddPodcast);
        fabAddPodcast.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //might need to check DiscoverFragment.this.isAdded() before using getActivity()
                DiscoverFragment.this.startActivity(new Intent(DiscoverFragment.this.getActivity(), AddFeedActivity.class));
            }
        });
        //check connection status and disable fab if not connected, also activate snackbar
        /*Snackbar snackbar = Snackbar.make(view, "No Connection", Snackbar.LENGTH_LONG);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            public  void onClick(View view) {
                //check connection status again
                snackbar.hide();
                fabAddPodcast.setEnabled(true);
                //populate list from database
            }
        }).show();*/
        return view;
    }
}
