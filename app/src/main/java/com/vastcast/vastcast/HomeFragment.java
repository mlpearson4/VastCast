package com.vastcast.vastcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button btnRssFetch = view.findViewById(R.id.btnRSSFetch);
        btnRssFetch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //might need to check HomeFragment.this.isAdded() before using getActivity()
                HomeFragment.this.startActivity(new Intent(HomeFragment.this.getActivity(), AddFeedActivity.class));
            }
        });
        return view;
    }
}
