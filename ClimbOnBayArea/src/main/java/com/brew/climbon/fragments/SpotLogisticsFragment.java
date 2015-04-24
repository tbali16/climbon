package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brew.climbon.R;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.helpers.LocationPicassoTransformation;
import com.brew.climbon.helpers.RockInfoComparator;
import com.brew.climbon.helpers.TagHandler;
import com.brew.climbon.model.Spot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Fragment displaying the rock logistics tab of a spot.
 * Path = {@link GridViewFragment} -> {@link SpotListFragment} -> {@link SwipeTabPagerFragment}
 */
public class SpotLogisticsFragment extends Fragment {
    public static final String EXTRA_SPOT_LABEL = "climbOn.SpotLogisticsFragment.SPOT_LABEL";

    private Spot mSpot;

    public static SpotLogisticsFragment newInstance(String spotLabel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SPOT_LABEL, spotLabel);

        SpotLogisticsFragment fragment = new SpotLogisticsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String spotLabel = (String) getArguments().getSerializable(EXTRA_SPOT_LABEL);
        mSpot = ListItemsLab.get(getActivity()).getSpot(spotLabel);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spot_fragment_scroll_view, parent, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.spot_fragment_scroll_view_linear_layout);
        decorateRockLogisticsFragment(linearLayout, parent, inflater);
        return view;
    }

    //TODO: Refactor
    private void decorateRockLogisticsFragment(LinearLayout currentLinearLayoutView,
                                               ViewGroup parent,
                                               LayoutInflater inflater) {
        ArrayList<String> keyList = new ArrayList<String>();
        keyList.addAll(mSpot.getRockLogistics().keySet());
        Collections.sort(keyList, new RockInfoComparator());
        for (String rockLogisticsKey : keyList) {
            final Pair<String, Spot.Location> value = mSpot.getRockLogistics().get(rockLogisticsKey);

            TextView sectionTitle = (TextView) inflater.inflate(R.layout.spot_fragment_scroll_view_text_view_with_list_seperator,
                    parent,
                    false);
            sectionTitle.setText(rockLogisticsKey.toUpperCase());
            currentLinearLayoutView.addView(sectionTitle);

            View separator = inflater.inflate(R.layout.spot_fragment_seperator, parent, false);
            currentLinearLayoutView.addView(separator);

            TextView sectionContent = (TextView) inflater.inflate(R.layout.spot_fragment_scroll_view_text_view_without_list_seperator,
                    parent,
                    false);
            sectionContent.setText(Html.fromHtml(value.first, null, new TagHandler()));
            currentLinearLayoutView.addView(sectionContent);
            if (value.second != null) {
                setLocationButton(currentLinearLayoutView,
                        value.second,
                        parent,
                        inflater);
            }
        }
    }

    private void setLocationButton(LinearLayout currentLinearLayoutView,
                                   final Spot.Location location,
                                   ViewGroup parent,
                                   LayoutInflater inflater) {
        final RelativeLayout locationButton = (RelativeLayout) inflater.inflate(R.layout.spot_fragment_logistics_location_button,
                parent,
                false);

        // Set up picasso image for the location button
        final ImageView imageView = ((ImageView) locationButton.findViewById(R.id.locationImageView));
        final TextView locationTextView = ((TextView) locationButton.findViewById(R.id.locationCaptionView));
        Transformation transformation =
                new LocationPicassoTransformation(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), // radius = 2dip
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()), // border = 2dip
                        getResources().getColor(R.color.picassoLocation)
                );
        Picasso.with(getActivity())
                .load(getMapUrl(location))
                .fit()
                .transform(transformation)
                .into(imageView);

        locationTextView.setText(location.getTitle());

        // set up color changes when button is touched and start google maps intent.
        locationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PorterDuffColorFilter colorFilter =
                            new PorterDuffColorFilter(0x0A000000, PorterDuff.Mode.SRC_ATOP);
                    imageView.setColorFilter(colorFilter);
                    imageView.invalidate();
                    locationTextView.setBackgroundResource(R.drawable.button_shape_upward_corners_selected);
                    locationTextView.invalidate();
                } else {
                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        imageView.setColorFilter(null);
                        imageView.invalidate();
                        locationTextView.setBackgroundResource(R.drawable.button_shape_upward_corners);
                        locationTextView.invalidate();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        imageView.setColorFilter(null);
                        imageView.invalidate();
                        locationTextView.setBackgroundResource(R.drawable.button_shape_upward_corners);
                        locationTextView.invalidate();
                        Uri locationUri = Uri.parse("geo:0,0?q=" + location.getLatitude() + "," + location.getLongitude() + "(" + location.getTitle() + ")"); // z param is zoom level
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
                        startActivity(mapIntent);
                    }
                }
                return true;
            }
        });
        currentLinearLayoutView.addView(locationButton);
    }

    /*
     * Return google maps with given location:
     * http://maps.google.com/maps/api/staticmap?center=37.8824240,-122.2416600&markers=37.8824240,-122.2416600&size=350x200&sensor=true&zoom=15
     */
    private String getMapUrl(Spot.Location location) {
        return "http://maps.google.com/maps/api/staticmap?center=" + location.getLatitude() + "," + location.getLongitude()
                + "&zoom=15"
                + "&size=350x150"
                + "&markers=color:0xF1AA40|" + location.getLatitude() + "," + location.getLongitude()
                + "&sensor=true"
                + "&key=AIzaSyBrOU882OEzDOtH_Xsb2qBRBEyao2lO1-0";

    }
}