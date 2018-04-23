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
                DiscoverFragment.this.startActivity(new Intent(DiscoverFragment.this.getActivity(), AddFeedActivity.class));
            }
        });
        /*TODO: Get basic page displaying podcasts from database*/
        /*TODO: Get basic search working*/
        return view;
    }
}
