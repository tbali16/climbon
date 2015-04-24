package com.brew.climbon.helpers;


import java.util.ArrayList;
import java.util.Comparator;


public class RockInfoComparator implements Comparator<String>
{
    public static ArrayList keyOrdering = new ArrayList();
    static {
        keyOrdering.add("DESCRIPTION");
        keyOrdering.add("GEAR");
    }

  @Override
  public int compare(String lhs, String rhs)
  {
    int lhsIndex = keyOrdering.indexOf(lhs.toUpperCase());
    int rhsIndex = keyOrdering.indexOf(rhs.toUpperCase());
    if (lhsIndex == -1)
    {
      return 1;
    }
    if (rhsIndex == -1)
    {
      return -1;
    }
    return lhsIndex - rhsIndex;
  }
}

