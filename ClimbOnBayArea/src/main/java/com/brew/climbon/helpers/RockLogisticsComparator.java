package com.brew.climbon.helpers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class RockLogisticsComparator implements Comparator<String> {
    public static ArrayList<String> keyOrdering = new ArrayList(Arrays.asList(new String[]{
            "PARKING",
            "APPROACH"
    }));

    @Override
    public int compare(String lhs, String rhs) {
        int lhsIndex = keyOrdering.indexOf(lhs.toUpperCase());
        int rhsIndex = keyOrdering.indexOf(rhs.toUpperCase());
        if (lhsIndex == -1) {
            return 1;
        }
        if (rhsIndex == -1) {
            return -1;
        }
        return lhsIndex - rhsIndex;
    }
}

