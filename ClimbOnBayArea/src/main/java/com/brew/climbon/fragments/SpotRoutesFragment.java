package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brew.climbon.activity.MainActivity;
import com.brew.climbon.R;
import com.brew.climbon.activity.RouteImageActivity;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.model.Spot;

import java.util.ArrayList;
import java.util.HashSet;

/*
 * Fragment displaying the rock info tab of a spot.
 * Path = {@link GridViewFragment} -> {@link SpotListFragment} -> {@link SwipeTabPagerFragment}
 * Displays list of routes for a given spot.
 */
public class SpotRoutesFragment extends ListFragment {
    public static final String EXTRA_SPOT_LABEL = "climbOn.SpotLogisticsFragment.SPOT_LABEL";

    private Pair<HashSet<Integer>, ArrayList<Spot.Route>> mRoutesWithSeparators;
    private String mSpotLabel;

    public static SpotRoutesFragment newInstance(String spotLabel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SPOT_LABEL, spotLabel);

        SpotRoutesFragment fragment = new SpotRoutesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String spotLabel = (String) getArguments().getSerializable(EXTRA_SPOT_LABEL);
        Spot spot = ListItemsLab.get(getActivity()).getSpot(spotLabel);
        mSpotLabel = spotLabel;
        mRoutesWithSeparators = spot.getRoutesWithSeperators();
        RoutesAdapter adapter = new RoutesAdapter(spot.getRoutes(), spot.getRoutesWithSeperators());
        setListAdapter(adapter);
    }

    private class RoutesAdapter extends BaseAdapter {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private LayoutInflater mInflater;
        private ArrayList<Spot.Route> mRoutes;
        private Pair<HashSet<Integer>, ArrayList<Spot.Route>> mRoutesWithSeparators;

        public RoutesAdapter(ArrayList<Spot.Route> routes, Pair<HashSet<Integer>, ArrayList<Spot.Route>> routesWithSeperators) {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mRoutes = routes;
            mRoutesWithSeparators = routesWithSeperators;
        }

        @Override
        public int getCount() {
            return mRoutesWithSeparators.second.size();
        }

        @Override
        public Spot.Route getItem(int position) {
            return mRoutesWithSeparators.second.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return mRoutesWithSeparators.first.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.spot_fragment_route_list_item, null);
            }
            // configure the view for this area
            Spot.Route r = mRoutesWithSeparators.second.get(position);

            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.spot_fragment_route_list_item, null);
                        convertView.setClickable(false);
                        convertView.setFocusable(false);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.spotfragment_routelist_header, null);
                        break;
                }
            }
            switch (type) {
                case TYPE_ITEM:
                    TextView titleTextView =
                            (TextView) convertView.findViewById(R.id.routeName);
                    titleTextView.setText(r.getName());
                    TextView countView =
                            (TextView) convertView.findViewById(R.id.routeDifficulty);
                    countView.setText(r.getDifficulty());
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.spotfragment_routelist_header, null);
                    TextView rockGroupText =
                            (TextView) convertView.findViewById(R.id.spotfragment_routesRockGroupHeaderText);
                    rockGroupText.setText(r.getName().toUpperCase());
                    break;
            }


            return convertView;
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mRoutesWithSeparators.first.contains(position))
            return;
        int result = position;
        for (int i : mRoutesWithSeparators.first) {
            if (i < position)
                result--;
        }
        MainActivity activity = (MainActivity) getActivity();
        Intent intent = new Intent(activity, RouteImageActivity.class);
        intent.putExtra(RouteImageActivity.EXTRA_SPOT_LABEL, mSpotLabel);
        intent.putExtra(RouteImageActivity.EXTRA_ROUTE_LIST_POSITION, result);
        startActivity(intent);
    }


    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return super.onCreateView(inflater, parent, savedInstanceState);
    }
}

