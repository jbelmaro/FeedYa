package com.jbelmaro.feedya;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    public static final String ARG_SECTION_NUMBER = "section_number";
    Context context;

    public SectionPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        context = c;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        if (position == 1) {
            Fragment fragment = new FavoriteExpandableFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        } else if (position == 2) {
            Fragment fragment2 = new UltimaHoraFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            fragment2.setArguments(args);
            return fragment2;
        } else if (position == 0) {
            Fragment fragment3 = new LecturaFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            fragment3.setArguments(args);
            return fragment3;
        } else
            return null;

    }

    @Override
    public int getCount() {

        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
        case 1:
            return context.getResources().getString(R.string.favorites_title).toUpperCase(l);
        case 2:
            return context.getResources().getString(R.string.lastnews_title).toUpperCase(l);
        case 0:
            return context.getResources().getString(R.string.save_later_title).toUpperCase(l);
        default:
            return null;
        }
    }

}
