package com.brew.climbon.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.brew.climbon.R;
import com.brew.climbon.fragments.GridViewFragment;
import com.brew.climbon.fragments.SpotListFragment;
import com.brew.climbon.fragments.SwipeTabPagerFragment;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
  private static String AREA_LIST_TO_SPOT_LIST_TRANSACTION_TAG = "AreaList_To_SpotList";

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment);
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

    if (fragment == null)
    {
      fragment = new GridViewFragment();
      manager.beginTransaction().add(R.id.fragmentContainer, fragment, "InitialFragment").commit();
    }
  }

  @Override
  public void onBackPressed()
  {
    FragmentManager fm = getSupportFragmentManager();
    if (fm.getBackStackEntryCount() != 0)
    {
      FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
      if (backEntry != null) {
        if(AREA_LIST_TO_SPOT_LIST_TRANSACTION_TAG.equals(backEntry.getName())) {
          for (int i = 0; i < fm.getBackStackEntryCount(); ++i)
          {
            fm.popBackStack();
            if (!AREA_LIST_TO_SPOT_LIST_TRANSACTION_TAG.equals(backEntry.getName())) return;
          }
          return;
        }
      }
    }

    super.onBackPressed();
  }

  public void switchToSpotListFragment(String areaLabel, ArrayList<String> areaLabels)
  {
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = SpotListFragment.newInstance(areaLabel, areaLabels);
    manager.beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(
        AREA_LIST_TO_SPOT_LIST_TRANSACTION_TAG).commit();
  }

  public void switchToSpotFragment(String spotLabel)
  {
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = SwipeTabPagerFragment.newInstance(spotLabel);
    manager.beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
  }
}