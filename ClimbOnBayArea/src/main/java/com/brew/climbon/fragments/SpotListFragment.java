package com.brew.climbon.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brew.climbon.activity.MainActivity;
import com.brew.climbon.R;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.model.Spot;

import java.util.ArrayList;

/*
 * Fragment displaying list of spots in an area.
 * An area is a collection of spots. On landing page, you are shown all areas.
 * Clicking on one lands you to this fragment.
 * This fragment presents the list of spots in the area.
 * Clicking on any lands you to {@link SwipeTabPagerFragment} (pager fragment for a spot)
 */
public class SpotListFragment extends ListFragment {
    public static final String EXTRA_AREA_LABEL = "climbOn.SpotListFragment.AREA_LABEL";
    public static final String EXTRA_AREA_LABELS = "climbOn.SpotListFragment.AREA_LABELS";

    private ArrayList<Spot> mSpots;
    private ArrayList<String> mAreaLabels;

    public static SpotListFragment newInstance(String areaLabel, ArrayList<String> areas) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_AREA_LABEL, areaLabel);
        args.putStringArrayList(EXTRA_AREA_LABELS, areas);

        SpotListFragment fragment = new SpotListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_fragment_menu, menu);
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Show all locations in a map fragment ({@link MapFragment})
            case R.id.menu_item_open_google_maps:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                MapFragment mapFragment = MapFragment.newInstance(getLocations(mSpots));
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, mapFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Spot.Location> getLocations(ArrayList<Spot> spots) {
        ArrayList<Spot.Location> result = new ArrayList<Spot.Location>();
        for (Spot spot : spots) {
            result.add(new Spot.Location(spot.getTitle(),
                    spot.getLocation().getLatitude(),
                    spot.getLocation().getLongitude()));
        }
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        final String areaLabel = (String) getArguments().getSerializable(EXTRA_AREA_LABEL);

        // Create a list adapter of spots
        getActivity().setTitle(areaLabel);
        mSpots = ListItemsLab.get(getActivity()).getSpots(areaLabel);
        SpotAdapter listAdapter = new SpotAdapter(mSpots);
        setListAdapter(listAdapter);

        mAreaLabels = getArguments().getStringArrayList(EXTRA_AREA_LABELS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = getActivity().getActionBar();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            // Drop down spinner in the action bar to switch to the spot list of another area
            final String[] dropdownValues = mAreaLabels.toArray(new String[mAreaLabels.size()]);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
                    android.R.layout.simple_spinner_item,
                    android.R.id.text1,
                    dropdownValues);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set up the dropdown list navigation in the action bar.
            actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBarListener());
            actionBar.setSelectedNavigationItem(mAreaLabels.indexOf(areaLabel));
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return super.onCreateView(inflater, parent, savedInstanceState);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Spot spot = ((SpotAdapter) getListAdapter()).getItem(position);
        String spotLabel = spot.getTitle();
        MainActivity activity = (MainActivity) getActivity();
        activity.switchToSpotFragment(spotLabel);
    }

    private class SpotAdapter extends ArrayAdapter<Spot> {
        public SpotAdapter(ArrayList<Spot> spots) {
            super(getActivity(), android.R.layout.simple_list_item_1, spots);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_spot, null);
            }

            // Configure the view for this spot on the list
            Spot spot = getItem(position);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.list_item_spot_titleTextView);
            titleTextView.setText(spot.getTitle());
            TextView difficultyTextView =
                    (TextView) convertView.findViewById(R.id.list_item_spot_difficultyTextView);
            difficultyTextView.setText(spot.getDifficulty());
            TextView climbTypeTextView =
                    (TextView) convertView.findViewById(R.id.list_item_spot_climbTypeTextView);
            climbTypeTextView.setText(spot.getClimbType());

            return convertView;
        }
    }

    private class ActionBarListener implements
            ActionBar.OnNavigationListener {
        private boolean mNaviFirstHit = true; // I absolutely hate this hack.

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            if (mNaviFirstHit) {
                mNaviFirstHit = false;
                return true;
            }
            String newAreaLabel = mAreaLabels.get(itemPosition);
            ((MainActivity) getActivity()).switchToSpotListFragment(newAreaLabel, mAreaLabels);
            return true;
        }
    }
}

