package com.intelisoft.easytorrents.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Technophile on 5/24/17.
 */
public class TestAdapter extends FragmentPagerAdapter {



    public TestAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieFragment.getInstance();
            case 1:
                return DownloadFragment.getInstance();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Movies";
            case 1: return "Downloads";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

}
