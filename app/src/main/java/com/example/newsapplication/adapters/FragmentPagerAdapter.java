package com.example.newsapplication.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.newsapplication.fragments.AgricultureFragment;
import com.example.newsapplication.fragments.EducationFragment;
import com.example.newsapplication.fragments.HealthFragment;
import com.example.newsapplication.fragments.HomeFragment;
import com.example.newsapplication.fragments.NationalFragment;
import com.example.newsapplication.fragments.ScienceFragment;
import com.example.newsapplication.fragments.SportsFragment;
import com.example.newsapplication.fragments.TechFragment;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
    private static final int NUM_PAGES = 8; // Number of fragments
    private static final String[] TAB_TITLES = {"Home", "National",  "Sports",  "Agriculture", "Health", "Education", "Tech", "Science"};

    public FragmentPagerAdapter(FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        // Return the appropriate fragment based on the position
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new NationalFragment();
            case 2:
                return new SportsFragment();
            case 3:
                return new AgricultureFragment();
            case 4:
                return new HealthFragment();
            case 5:
                return new EducationFragment();
            case 6:
                return new TechFragment();
            case 7:
                return new ScienceFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // Return the title of the tab at the specified position
        return TAB_TITLES[position];
    }
}
