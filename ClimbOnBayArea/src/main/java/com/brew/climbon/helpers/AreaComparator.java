package com.brew.climbon.helpers;


import com.brew.climbon.model.Area;

import java.util.ArrayList;
import java.util.Comparator;

/*
 * Comparator to sort areas as North Bay, East Bay, South Bay, San Francisco.
 */
public class AreaComparator implements Comparator<Area>
{
  public static ArrayList keyOrdering = new ArrayList();
  static {
      keyOrdering.add("NORTH BAY");
      keyOrdering.add("EAST BAY");
      keyOrdering.add("SOUTH BAY");
      keyOrdering.add("SAN FRANCISCO");
  }

  @Override
  public int compare(Area lhs, Area rhs)
  {
    int lhsIndex = keyOrdering.indexOf(lhs.getTitle().toUpperCase());
    int rhsIndex = keyOrdering.indexOf(rhs.getTitle().toUpperCase());
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

