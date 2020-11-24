package com.endeavor.walter.getout9.ui.main;

import android.content.Context;
//import android.support.annotation.Nullable;
//import android.support.annotation.StringRes;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.endeavor.walter.getout9.CalendarFragment;
import com.endeavor.walter.getout9.LocationsFragment;
import com.endeavor.walter.getout9.MapFragment;
import com.endeavor.walter.getout9.NotesActivityFragment;
import com.endeavor.walter.getout9.R;

import static com.endeavor.walter.getout9.WVSInfo.TAB_TITLES;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private String TAG = SectionsPagerAdapter.class.getSimpleName();

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.i(TAG, " WVS getItem = " + position);
        if (position == 0){
            return new MapFragment();
        } else if (position == 1){
            LocationsFragment frag = new LocationsFragment();
//            return new LocationsFragment();
            return frag;
        }
        else if (position == 2) {
            NotesActivityFragment frag = new NotesActivityFragment();
            return frag;
        }
        else if (position == 3) {
//          wvs todo change to Calendar
            CalendarFragment frag = new CalendarFragment();
            return frag;
        }
        else {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show # total pages.  Maps, Routes, Notes, Calendar
        return 4;
    }
}