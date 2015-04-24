package com.brew.climbon.helpers;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.brew.climbon.R;
import com.brew.climbon.model.Area;
import com.brew.climbon.model.Spot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Handles taking all json files from assets and transforming them into classes
 * that are accessible across the app.
 */
public class JSONSerializer {
    private static final String TAG = "JSONSerializer";

    private Context mContext;
    private ArrayList<Area> mAreas;
    private HashMap<String, Spot> mSpots;

    public JSONSerializer(Context c) {
        mContext = c;
        try {
            loadResources();
        } catch (Exception e) {
            Log.e(TAG, "Error loading list: ", e);
            mAreas = new ArrayList<Area>();
            mSpots = new HashMap<String, Spot>();
        }
    }

    public void loadResources() throws IOException, JSONException {
        HashMap<String, ArrayList<Spot>> areaToSpots = new HashMap<String, ArrayList<Spot>>();
        mSpots = new HashMap<String, Spot>();
        BufferedReader reader = null;
        try {
            Field[] fields = R.raw.class.getFields();
            for (int count = 0; count < fields.length; count++) {
                String fileName = fields[count].getName();
                // open and read the file into a StringBuilder
                InputStream inputStream = mContext.getResources().openRawResource(mContext.getResources().getIdentifier("raw/" + fileName, "raw", mContext.getPackageName()));
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                JSONObject jsonObject = new JSONObject(jsonString.toString());
                String areaLabel = jsonObject.getString("area");
                String spotLabel = jsonObject.getString("spot");
                String difficulty = jsonObject.getString("difficulty");
                String climbType = jsonObject.getString("climbType");
                String assetFolder = jsonObject.optString("assetFolder", null);
                String coverPhoto = jsonObject.getString("cover");
                JSONObject location = jsonObject.getJSONObject("location");
                JSONObject rockInfo = jsonObject.getJSONObject("rockInfo");
                JSONObject rockLogistics = jsonObject.getJSONObject("logistics");
                JSONObject rockRoutesJSON = jsonObject.getJSONObject("routes");

                Iterator rockInfoKeys = rockInfo.keys();
                HashMap<String, String> rockInfoKeyValues = new HashMap<String, String>();
                while (rockInfoKeys.hasNext()) {
                    String key = (String) rockInfoKeys.next();
                    String value = rockInfo.getString(key);
                    rockInfoKeyValues.put(key, value);
                }

                Iterator rockLogisticsKeys = rockLogistics.keys();
                HashMap<String, Pair<String, Spot.Location>> rockLogisticsKeyValues = new HashMap<String, Pair<String, Spot.Location>>();
                while (rockLogisticsKeys.hasNext()) {
                    String key = (String) rockLogisticsKeys.next();
                    JSONObject value = rockLogistics.getJSONObject(key);
                    String main = value.getString("main");
                    JSONObject gps = value.optJSONObject("gps");
                    if (gps != null) {
                        Spot.Location logisticValueLocation = new Spot.Location(gps.getString("title"), gps.getDouble("latitude"), gps.getDouble("longitude"));
                        rockLogisticsKeyValues.put(key, new Pair<String, Spot.Location>(main, logisticValueLocation));
                    } else {
                        rockLogisticsKeyValues.put(key, new Pair<String, Spot.Location>(main, null));
                    }
                }

                Iterator rockRoutesKeys = rockRoutesJSON.keys();
                ArrayList<Spot.Route> rockRoutes = new ArrayList<Spot.Route>();
                ArrayList<Spot.Route> rockRoutesWithSeperators = new ArrayList<Spot.Route>();
                HashSet<Integer> seperatorPositions = new HashSet<Integer>();

                ArrayList<String> keyList = new ArrayList<String>();
                while (rockRoutesKeys.hasNext()) {
                    keyList.add((String) rockRoutesKeys.next());
                }
                Collections.sort(keyList);

                for (String key : keyList) {
                    JSONArray routes = rockRoutesJSON.getJSONArray(key);
                    if (key.indexOf(".") != -1)
                        key = key.substring(key.indexOf(".") + 2);
                    seperatorPositions.add(rockRoutesWithSeperators.size());
                    rockRoutesWithSeperators.add(new Spot.Route(key, null, null, null, null, false));


                    ArrayList<Spot.Route> routesForRock = new ArrayList<Spot.Route>();
                    while (rockRoutesKeys.hasNext()) {
                        keyList.add((String) rockRoutesKeys.next());
                    }
                    for (int i = 0; i < routes.length(); i++) {
                        JSONObject jsonRoute = (JSONObject) routes.get(i);
                        Spot.Route route = new Spot.Route(jsonRoute.getString("name"),
                                jsonRoute.getString("difficulty"),
                                jsonRoute.getString("image"),
                                jsonRoute.optString("localImage", null),
                                rockRoutes.size(),
                                jsonRoute.optBoolean("isLandscapeOrientation"));
                        routesForRock.add(route);
                    }
                    Collections.sort(routesForRock, new RoutesComparator());
                    rockRoutes.addAll(routesForRock);
                    rockRoutesWithSeperators.addAll(routesForRock);
                }

                Spot newSpot = new Spot(spotLabel,
                        coverPhoto,
                        climbType,
                        difficulty,
                        assetFolder,
                        rockInfoKeyValues,
                        rockLogisticsKeyValues,
                        rockRoutes,
                        new Pair(seperatorPositions, rockRoutesWithSeperators),
                        new Spot.Location(null, location.getDouble("latitude"), location.getDouble("longitude")));
                mSpots.put(spotLabel, newSpot);
                ArrayList<Spot> spotsInArea = areaToSpots.get(areaLabel);
                if (areaToSpots.get(areaLabel) != null) {
                    spotsInArea.add(newSpot);
                } else {
                    spotsInArea = new ArrayList<Spot>();
                    spotsInArea.add(newSpot);
                    areaToSpots.put(areaLabel, spotsInArea);
                }

            }
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        mAreas = new ArrayList<Area>();
        for (Map.Entry<String, ArrayList<Spot>> areaWithCount : areaToSpots.entrySet()) {
            mAreas.add(new Area(areaWithCount.getKey(), areaWithCount.getValue()));
        }
    }

    public ArrayList<Area> getAreas() {
        return mAreas;
    }

    public ArrayList<Spot> getSpots(String areaLabel) {
        for (Area area : mAreas) {
            if (area.getTitle().equals(areaLabel))
                return area.getSpots();
        }
        return new ArrayList<Spot>();
    }

    public Spot getSpot(String spotLabel) {
        return mSpots.get(spotLabel);
    }

    public List<String> getAllUrls() {
        List<String> allUrls = new ArrayList<String>();
        for (String spotLabel : mSpots.keySet()) {
            Spot spot = mSpots.get(spotLabel);
            for (Spot.Route route : spot.getRoutes()) {
                allUrls.add(route.getImage());
            }
        }
        return allUrls;
    }
}
