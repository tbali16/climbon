package com.brew.climbon.model;


import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * A spot is a single location. Eg. Glen Canyon.
 */
public class Spot
{
  private String mTitle;
  private String mCover;
  private String mClimbType;
  private String mDifficulty;
  private String mAssetFolder;
  private HashMap<String, String> mRockInfo;
  private HashMap<String, Pair<String, Location>> mRockLogistics;
  private ArrayList<Route> mRoutes;
  private Pair<HashSet<Integer>, ArrayList<Route>> mRoutesWithSeperators;
  private Location mLocation;

  public Spot(String title,
              String cover,
              String climbType,
              String difficulty,
              String assetFolder,
              HashMap<String, String> rockInfo,
              HashMap<String, Pair<String, Location>> rockLogistics,
              ArrayList<Route> routes,
              Pair<HashSet<Integer>, ArrayList<Route>> routesWithSeperators,
              Location location)
  {
    mTitle = title;
    mCover = cover;
    mRockInfo = rockInfo;
    mDifficulty = difficulty;
    mClimbType = climbType;
    mAssetFolder = assetFolder;
    mRockLogistics = rockLogistics;
    mRoutes = routes;
    mRoutesWithSeperators = routesWithSeperators;
    mLocation = location;
  }

  public String getDifficulty()
  {
    return mDifficulty;
  }

  public String getClimbType()
  {
    return mClimbType;
  }

  public String getTitle()
  {
    return mTitle;
  }

  public String getCover()
  {
    return mCover;
  }

  public String getAssetFolder() {
    return mAssetFolder;
  }

  public Location getLocation()
  {
    return mLocation;
  }

  public HashMap<String, String> getRockInfo()
  {
    return mRockInfo;
  }

  public HashMap<String, Pair<String, Location>> getRockLogistics()
  {
    return mRockLogistics;
  }

  public ArrayList<Route> getRoutes()
  {
    return mRoutes;
  }

  public Pair<HashSet<Integer>, ArrayList<Route>> getRoutesWithSeperators()
  {
    return mRoutesWithSeperators;
  }

  @Override
  public String toString()
  {
    return mTitle;
  }

  public static class Route
  {
    private String mName;
    private String mDifficulty;
    private String mImage;
    private String mLocalImage;
    private Integer mListPosition;
    private boolean mIsLandscapeOrientation;

    public Route(String name, String difficulty, String image, String localImage, Integer listPosition, boolean isLandscapeOrientation)
    {
      mName = name;
      mDifficulty = difficulty;
      mImage = image;
      mLocalImage = localImage;
      mListPosition = listPosition;
      mIsLandscapeOrientation = isLandscapeOrientation;
    }

    public String getName()
    {
      return mName;
    }

    public String getDifficulty()
    {
      return mDifficulty;
    }

    public String getImage()
    {
      return mImage;
    }

    public String getLocalImage() {
      return mLocalImage;
    }

    public Integer getListPosition()
    {
      return mListPosition;
    }

    public boolean getIsLandscapeOrientation()
    {
      return mIsLandscapeOrientation;
    }
  }

  public static class Location
  {
    private String mTitle;
    private Double mLat;
    private Double mLon;

    public Location(String title, Double lat, Double lon)
    {
      mTitle = title;
      mLat = lat;
      mLon = lon;
    }

    public String getTitle()
    {
      return mTitle;
    }

    public Double getLatitude()
    {
      return mLat;
    }

    public Double getLongitude()
    {
      return mLon;
    }
  }
}
