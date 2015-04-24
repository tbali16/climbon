package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brew.climbon.R;

/*
 * Fragment managing pager view for a spot.
 * An area is a collection of spots.
 * When you click on an area and a spot from a list, you come to this pager.
 * It pages between {@link SpotRockInfoFragment}, {@link SpotLogisticsFragment}
 * and {@link SpotRoutesFragment}.
 */
public class SwipeTabPagerFragment extends Fragment implements ActionBar.TabListener {
    private static final int NUM_ITEMS = 3;
    public static final String EXTRA_SPOT_LABEL = "climbOn.SwipeTabPagerFragment.SPOT_LABEL";
    private static final String[] TAB_LABELS = {"Rock Info", "Logistics", "Routes"};

    private ViewPager mPager;
    private SpotPagerAdapter mAdapter;
    private String mSpotLabel;

    public static SwipeTabPagerFragment newInstance(String spotLabel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SPOT_LABEL, spotLabel);

        SwipeTabPagerFragment fragment = new SwipeTabPagerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /*
     * Core logic for switching.
     */
    public class SpotPagerAdapter extends FragmentStatePagerAdapter {
        private String mSpotLabel;

        public SpotPagerAdapter(FragmentManager fm, String spotLabel) {
            super(fm);
            mSpotLabel = spotLabel;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SpotRockInfoFragment.newInstance(mSpotLabel);
                case 1:
                    return SpotLogisticsFragment.newInstance(mSpotLabel);
                case 2:
                    return SpotRoutesFragment.newInstance(mSpotLabel);
            }
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, parent, false);
        mSpotLabel = (String) getArguments().getSerializable(EXTRA_SPOT_LABEL);
        mAdapter = new SpotPagerAdapter(getActivity().getSupportFragmentManager(), mSpotLabel);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Tab support similar to Google PLAY app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = getActivity().getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mSpotLabel);
            if (actionBar.getTabCount() != 0) {
                actionBar.removeAllTabs();
            }
            for (int i = 0; i < 3; i++) {
                actionBar.addTab(actionBar.newTab().setText(TAB_LABELS[i]).setTabListener(this));
            }

            //actionBar.setSelectedNavigationItem(0);
            mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // When swiping between pages, select the corresponding tab.
                    if (actionBar.getSelectedNavigationIndex() != position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                }
            });
        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onTabSelected(final ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        if (mPager.getCurrentItem() != tab.getPosition()) {
            mPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
    }
}
