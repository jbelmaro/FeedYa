package com.jbelmaro.feedya;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ArticlePagerAdapter extends FragmentPagerAdapter {
    public static final String ARG_SECTION_NUMBER = "section_number";

    public ArticlePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        if ((position + 1) == 1) {
            Fragment fragment = new UltimaHoraFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            args.putLong("color", Color.BLACK);
            fragment.setArguments(args);

            return fragment;
        } else {
            Fragment fragment2 = new UltimaHoraFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            args.putInt("color", Color.GREEN);
            fragment2.setArguments(args);
            return fragment2;
        }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

}
