package com.vastcast.vastcast;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private int numTabs;
    private Episode episode = null;
    private Collection collection = null;
    private int episodeNumber = 0;
    private boolean reversed = false;

    public PagerAdapter(FragmentManager fm, Context context, int numTabs) {
        super(fm);
        this.context = context;
        this.numTabs = numTabs;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.discover);
            case 1:
                return context.getString(R.string.play);
            case 2:
                return context.getString(R.string.manage);
            default:
                return null;
        }
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DiscoverFragment();
            case 1:
                return PlayFragment.newInstance(episode, collection, episodeNumber, reversed);
            case 2:
                return new ManageFragment();
            default:
                return null;
        }
    }

    public void setPlayArguments(Episode e, Collection c, int episodeNumber, boolean reversed) {
        this.episode = e;
        this.collection = c;
        this.episodeNumber = episodeNumber;
        this.reversed = reversed;
    }

    public int getCount() {
        return numTabs;
    }
}
