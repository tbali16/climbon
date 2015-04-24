package com.brew.climbon.model;

import java.util.ArrayList;

/*
 * An area has multiple spots. Eg. San Francisco has Glen Canyon, Ocean Beach etc.
 */
public class Area {
    private String mTitle;
    private ArrayList<Spot> mSpots;

    public Area(String title, ArrayList<Spot> spots) {
        mTitle = title;
        mSpots = spots;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<Spot> getSpots() {
        return mSpots;
    }

    public void setSpots(ArrayList<Spot> spots) {
        mSpots = spots;
    }
}
