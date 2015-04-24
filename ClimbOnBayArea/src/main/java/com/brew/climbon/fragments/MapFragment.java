package com.brew.climbon.fragments;

import android.os.Bundle;
import android.view.Display;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.brew.climbon.model.Spot;
import java.util.ArrayList;

/*
 * Map fragment to map out all locations of climbs within each area.
 */
public class MapFragment extends SupportMapFragment {
    public static final String EXTRA_MAP_SPOT_LOCATIONS = "climbOn.MapFragment.MAP_SPOT_LOCATIONS";

    public static MapFragment newInstance(ArrayList<Spot.Location> locations) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MAP_SPOT_LOCATIONS, locations);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        ArrayList<Spot.Location> locations = (ArrayList<Spot.Location>) getArguments().getSerializable(EXTRA_MAP_SPOT_LOCATIONS);
        GoogleMap map = getMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (Spot.Location spotLocation : locations) {
                LatLng latLng = new LatLng(spotLocation.getLatitude(), spotLocation.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title(spotLocation.getTitle())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                boundsBuilder.include(latLng);
            }
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), display.getWidth(), display.getHeight(), 250));
        }
    }
}
