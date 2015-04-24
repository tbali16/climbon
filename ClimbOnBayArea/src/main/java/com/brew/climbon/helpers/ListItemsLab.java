package com.brew.climbon.helpers;

import android.content.Context;

import com.brew.climbon.model.Area;
import com.brew.climbon.model.Spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Getter for serializer. Uses context to check if serializer is ready.
 */
public class ListItemsLab {
    private static final String TAG = "ListItemsLab";

    private JSONSerializer mSerializer;

    private static ListItemsLab sListItemsLab;
    private Context mAppContext;

    private ListItemsLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new JSONSerializer(mAppContext);
    }

    public static ListItemsLab get(Context c) {
        if (sListItemsLab == null) {
            sListItemsLab = new ListItemsLab(c.getApplicationContext());
        }
        return sListItemsLab;
    }

    public ArrayList<Area> getAreas() {
        ArrayList<Area> areas = mSerializer.getAreas();
        Collections.sort(areas, new AreaComparator());
        return areas;
    }

    public ArrayList<Spot> getSpots(String areaLabel) {
        return mSerializer.getSpots(areaLabel);
    }

    public Spot getSpot(String spotLabel) {
        return mSerializer.getSpot(spotLabel);
    }

    public List<String> getAllUrls() {
      return mSerializer.getAllUrls();
    }
}

