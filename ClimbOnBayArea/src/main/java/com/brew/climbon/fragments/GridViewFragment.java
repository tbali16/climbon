package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.climbon.helpers.Constants;
import com.brew.climbon.activity.MainActivity;
import com.brew.climbon.R;
import com.brew.climbon.asyncTasks.DownloadRoutesTask;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.helpers.SquareLayout;
import com.brew.climbon.model.Area;
import com.brew.climbon.model.Spot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Grid fragment for landing page. Currently static till v2.
 * An area is a collection of spots. This is a grid view for the areas.
 * Clicking on an area lands you on a fragments showing list of spots.
 * Check - {@link SpotListFragment}
 */
public class GridViewFragment extends Fragment {
    public static String TAG = GridViewFragment.class.getName();

    public static HashMap<String, Integer> areaToPosition = new HashMap<String, Integer>(){{
        put("North Bay", 0);
        put("South Bay", 1);
        put("East Bay", 2);
        put("San Francisco", 3);
    }};

    public static int SCREEN_HEIGHT;
    public static int TEXT_PADDING = 30;

    private GridView gridView;
    private ArrayList<Area> mAreas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get the list of areas from JSONs
        mAreas = ListItemsLab.get(getActivity()).getAreas();

        // Set screen height (used later for placing square grids correctly
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_HEIGHT = metrics.heightPixels;

        // If images are not cached, run an async task. Make the store offline feature have a UI for v2.
        if (!Constants.hasAllImagesStored(getActivity()) && Constants.isNetAvailable(getActivity())) {
            List<String> allUrls = ListItemsLab.get(getActivity()).getAllUrls();
            new DownloadRoutesTask(getActivity()).execute(allUrls.toArray(new String[allUrls.size()]));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = getActivity().getActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = getActivity().getActionBar();
            actionBar.setTitle(R.string.app_name);
        }

        // Set up grid view with an adapter corresponding to the four areas (hard-coded for now. big no no for future)
        View view = inflater.inflate(R.layout.gridview, parent, false);
        gridView = (GridView) view.findViewById(R.id.photoGridView);
        ArrayAdapter<Area> adapter = new PhotoAdapter(mAreas);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View v,
                                    int position,
                                    long id) {
                Area clickedArea = mAreas.get(position);
                String areaLabel = clickedArea.getTitle();
                MainActivity activity = (MainActivity) getActivity();
                ArrayList<String> areaLabels = new ArrayList<String>();
                for (Area currentArea : mAreas) {
                    areaLabels.add(currentArea.getTitle());
                }
                activity.switchToSpotListFragment(areaLabel, areaLabels);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.grid_fragment_menu, menu);
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_open_google_maps:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                MapFragment mapFragment = MapFragment.newInstance(getLocations());
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, mapFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.menu_item_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constants.FEEDBACK_EMAIL_DESTINATION, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.FEEDBACK_EMAIL_SUBJECT);
                startActivity(emailIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Spot.Location> getLocations() {
        ArrayList<Spot.Location> result = new ArrayList<Spot.Location>();
        for (Area area : mAreas) {
            ArrayList<Spot> spots = ListItemsLab.get(getActivity()).getSpots(area.getTitle());
            for (Spot spot : spots) {
                result.add(new Spot.Location(spot.getTitle(), spot.getLocation().getLatitude(), spot.getLocation().getLongitude()));
            }
        }
        return result;
    }

    private class PhotoAdapter extends ArrayAdapter<Area> {
        public PhotoAdapter(ArrayList<Area> areas) {
            super(getActivity(), 0, areas);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Area currentArea = getItem(position);

            SquareLayout squareLayout = new SquareLayout(getActivity());

            ImageView imageView = new ImageView(getActivity());
            try {
                String fileName = null;
                // TODO: Avoid hardcoding these.
                switch (getSwitchCase(currentArea)) {
                    case 0:
                        fileName = "GridNorthBay.jpg";
                        break;
                    case 1:
                        fileName = "GridSouthBay.jpg";
                        break;
                    case 2:
                        fileName = "GridEastBay.jpg";
                        break;
                    case 3:
                        fileName = "GridSF.jpg";
                        break;
                }
                InputStream ims = getActivity().getAssets().open(fileName);
                Drawable drawable = Drawable.createFromStream(ims, null);
                imageView.setImageDrawable(drawable);
            } catch (IOException e) {
                Log.e(TAG, "Exception opening cover image", e);
            }

            // Setting image layout params based on action and status bar height.
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.drawable.bg_image_view);
            SquareLayout.LayoutParams imageLayoutParams =
                    new SquareLayout.LayoutParams(SquareLayout.LayoutParams.MATCH_PARENT, ((SCREEN_HEIGHT - getActionBarHeight(getContext()) - getStatusBarHeight()) / 2) + 1);
            imageView.setLayoutParams(imageLayoutParams);

            // Set caption for each image with bottom gravity
            TextView textView = new TextView(getActivity());
            SquareLayout.LayoutParams layoutParams = new SquareLayout.LayoutParams(SquareLayout.LayoutParams.MATCH_PARENT, SquareLayout.LayoutParams.WRAP_CONTENT);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setBackgroundResource(R.drawable.bg_text_view);
            textView.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            textView.setLayoutParams(layoutParams);
            textView.setText(currentArea.getTitle());
            layoutParams.gravity = Gravity.BOTTOM;

            squareLayout.addView(imageView);
            squareLayout.addView(textView);

            return squareLayout;
        }
    }

    public int getSwitchCase(Area currentArea) {
        if (areaToPosition.keySet().contains(currentArea.getTitle())) {
            return areaToPosition.get(currentArea.getTitle());
        }
        return -1;
    }

    private int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
