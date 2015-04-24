package com.brew.climbon.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.brew.climbon.R;
import com.brew.climbon.fragments.SpotRouteImageFragment;
import com.brew.climbon.helpers.ListItemsLab;
import com.brew.climbon.model.Spot;
import java.util.ArrayList;


/**
 * Created by tbali on 3/2/14.
 */
public class RouteImageActivity extends FragmentActivity
{
  public static String EXTRA_SPOT_LABEL = "ImageActivity_SpotLabel";
  public static String EXTRA_ROUTE_LIST_POSITION = "ImageActivity_RouteListPosition";

  private ArrayList<Spot.Route> mRoutes;
  private String mAssetFolder;
  private ViewPager mViewPager;
  private ImagePagerAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // Get the message from the intent
    Intent intent = getIntent();
    String spotLabel = intent.getStringExtra(EXTRA_SPOT_LABEL);
    int routeListPosition = intent.getIntExtra(EXTRA_ROUTE_LIST_POSITION, 0);
    Spot spot = ListItemsLab.get(this).getSpot(spotLabel);
    mRoutes = spot.getRoutes();
    mAssetFolder = spot.getAssetFolder();
    mViewPager = new ViewPager(this);
    mViewPager.setId(R.id.pager);
    mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mRoutes);
    mViewPager.setAdapter(mAdapter);
    mViewPager.setCurrentItem(routeListPosition);
    setContentView(mViewPager);
  }

  public class ImagePagerAdapter extends FragmentStatePagerAdapter
  {
    private ArrayList<Spot.Route> mRoutes;
    public ImagePagerAdapter(FragmentManager fm, ArrayList<Spot.Route> routes) {
      super(fm);
      mRoutes = routes;
    }

    @Override
    public int getCount() {
      return mRoutes.size();
    }

    @Override
    public Fragment getItem(int position) {
      String routeImage = mRoutes.get(position).getImage();
      String localImage = mRoutes.get(position).getLocalImage();
      return SpotRouteImageFragment.newInstance(routeImage, localImage, mAssetFolder);
    }
  }
}
