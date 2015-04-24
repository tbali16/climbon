package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brew.climbon.R;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.helpers.RockInfoComparator;
import com.brew.climbon.helpers.TagHandler;
import com.brew.climbon.model.Spot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Fragment displaying the rock info tab of a spot.
 * Path = {@link GridViewFragment} -> {@link SpotListFragment} -> {@link SwipeTabPagerFragment}
 */
public class SpotRockInfoFragment extends Fragment {
    public static final String TAG = SpotRockInfoFragment.class.getName();
    public static final String EXTRA_SPOT_LABEL = "climbOn.SpotRockInfoFragment.SPOT_LABEL";

    private Spot mSpot;

    public static SpotRockInfoFragment newInstance(String spotLabel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SPOT_LABEL, spotLabel);

        SpotRockInfoFragment fragment = new SpotRockInfoFragment();
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
        View currentView = inflater.inflate(R.layout.spot_fragment_parallax_scroll_view, parent, false);
        LinearLayout currentLinearLayoutView = (LinearLayout) currentView.findViewById(R.id.spot_fragment_parallax_scroll_view_linear_layout);

        // Set logic for parallax image scrolling etc
        setParallaxLogic(currentView);
        // Decorate all other sections of this fragment
        decorateRockInfoFragment(currentLinearLayoutView, parent, inflater);

        return currentView;
    }

    private void decorateRockInfoFragment(LinearLayout currentLinearLayoutView,
                                          ViewGroup parent,
                                          LayoutInflater inflater) {
        ArrayList<String> keyList = new ArrayList<String>();
        keyList.addAll(mSpot.getRockInfo().keySet());
        Collections.sort(keyList, new RockInfoComparator());
        for (String rockInfoKey : keyList) {
            TextView sectionTitle = (TextView) inflater.inflate(R.layout.spot_fragment_scroll_view_text_view_with_list_seperator,
                    parent,
                    false);
            sectionTitle.setText(rockInfoKey.toUpperCase());
            currentLinearLayoutView.addView(sectionTitle);

            View separator = inflater.inflate(R.layout.spot_fragment_seperator, parent, false);
            separator.setBackgroundColor(getResources().getColor(R.color.seperatorColor));
            currentLinearLayoutView.addView(separator);

            TextView sectionContent = (TextView) inflater.inflate(R.layout.spot_fragment_scroll_view_text_view_without_list_seperator,
                    parent,
                    false);
            sectionContent.setText(Html.fromHtml(mSpot.getRockInfo().get(rockInfoKey), null, new TagHandler()));
            currentLinearLayoutView.addView(sectionContent);
        }
    }

    private void setParallaxLogic(View currentView) {
        try {
            ImageView parallaxImage = (ImageView) currentView.findViewById(R.id.parallaximage);
            InputStream coverPhotoStream = getActivity().getAssets().open(mSpot.getCover());
            Drawable coverPhotoDrawable = Drawable.createFromStream(coverPhotoStream, null);
            Bitmap coverPhotoBitmap = ((BitmapDrawable) coverPhotoDrawable).getBitmap();

            // Get scaling factor to fit the max possible width of the ImageView
            float scalingFactor = this.getBitmapScalingFactor(parallaxImage, coverPhotoBitmap);

            // Create a new bitmap with the scaling factor
            Bitmap newBitmap = scaleBitmap(coverPhotoBitmap, scalingFactor);

            // Set the bitmap as the ImageView source
            parallaxImage.setImageBitmap(newBitmap);
        } catch (IOException e) {
            Log.e(TAG, "Exception opening cover image", e);
        }
    }

    private float getBitmapScalingFactor(ImageView iv, Bitmap bm) {
        // Get display width from device
        int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

        // Get margin to use it for calculating to max width of the ImageView
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) iv.getLayoutParams();
        int leftMargin = layoutParams.leftMargin;
        int rightMargin = layoutParams.rightMargin;

        // Calculate the max width of the imageView
        int imageViewWidth = displayWidth - (leftMargin + rightMargin);

        // Calculate scaling factor and return it
        return ((float) imageViewWidth / (float) bm.getWidth());
    }

    public static Bitmap scaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);
        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }
}

