package com.vastcast.vastcast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * This class manages the tab fragments
 */

public class PagerAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeFragment tab1 = new HomeFragment();
                return tab1;
            case 1:
                PlayFragment tab2 = new PlayFragment();
                return tab2;
            case 2:
                ManageFragment tab3 = new ManageFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

