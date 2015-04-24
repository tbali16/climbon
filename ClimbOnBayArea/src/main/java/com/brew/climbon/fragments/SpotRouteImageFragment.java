package com.brew.climbon.fragments;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.loopj.android.image.SmartImageView;
import com.brew.climbon.helpers.Constants;
import com.brew.climbon.R;
import com.brew.climbon.asyncTasks.DatabaseHandler;
import com.brew.climbon.asyncTasks.DownloadRoutesTask;
import com.brew.climbon.helpers.ListItemsLab;

import java.util.List;

/*
 * Fragment displaying the image of a route
 * Path = {@link GridViewFragment} -> {@link SpotListFragment} -> {@link SwipeTabPagerFragment} -> {@link SpotRoutesFragment}
 * Displays the image of a route when clicked on from {@link SpotRouteImageFragment}
 */
public class SpotRouteImageFragment extends Fragment
{
  String mRouteImage;
  String mLocalImage;
  String mAssetFolder;

  public static final String EXTRA_ROUTE_IMAGE = "climbOn.SpotRouteImageFragment.ROUTE_IMAGE";
  public static final String EXTRA_ASSET_FOLDER = "climbOn.SpotRouteImageFragment.ASSET_FOLDER";
  public static final String EXTRA_LOCAL_IMAGE = "climbOn.SpotRouteImageFragment.LOCAL_IMAGE";

  public static SpotRouteImageFragment newInstance(String routeImage, String localImage, String assetFolder) {
    Bundle args = new Bundle();
    args.putSerializable(EXTRA_ROUTE_IMAGE, routeImage);
    args.putSerializable(EXTRA_LOCAL_IMAGE, localImage);
    args.putSerializable(EXTRA_ASSET_FOLDER, assetFolder);

    SpotRouteImageFragment fragment = new SpotRouteImageFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @TargetApi(11)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    mRouteImage = (String) getArguments().getSerializable(EXTRA_ROUTE_IMAGE);
    mLocalImage = (String) getArguments().getSerializable(EXTRA_LOCAL_IMAGE);
    mAssetFolder = (String) getArguments().getSerializable(EXTRA_ASSET_FOLDER);

    View view = inflater.inflate(R.layout.fast_image, parent, false);
    SmartImageView routeImageView = (SmartImageView) view.findViewById(R.id.smart_image);

    new DownloadRoutesTask(getActivity()).execute(mRouteImage);

    DatabaseHandler dbHandler = new DatabaseHandler(getActivity());
    Bitmap result = dbHandler.getImage(mRouteImage);
    if(result != null)
    {
        routeImageView.setImageBitmap(result);
        return view;
    } else {
        routeImageView.setImageUrl(mRouteImage);
        new DownloadRoutesTask(getActivity()).execute(mRouteImage);
        Constants.setAllImagesStored(getActivity(), false);
        if(Constants.isNetAvailable(getActivity())) {
            List<String> allUrls = ListItemsLab.get(getActivity()).getAllUrls();
            new DownloadRoutesTask(getActivity()).execute(allUrls.toArray(new String[allUrls.size()]));
        }
        return view;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
