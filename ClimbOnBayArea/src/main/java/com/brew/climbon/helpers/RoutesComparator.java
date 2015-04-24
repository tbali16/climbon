package com.brew.climbon.helpers;


import com.brew.climbon.model.Spot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by tbali on 4/14/14.
 */
public class RoutesComparator implements Comparator<Spot.Route>
{
  public static ArrayList<String> keyOrdering = new ArrayList(Arrays.asList(new String[]{
      "n/a",
      " ",
      "5.2",
      "5.3",
      "5.4",
      "5.5",
      "5.6",
      "5.7",
      "5.8",
      "5.9",
      "5.10a",
      "5.10b",
      "5.10c",
      "5.10d",
      "5.11a",
      "5.11b"
  }));


  @Override
  public int compare(Spot.Route lhs, Spot.Route rhs)
  {
    String lhsDif = lhs.getDifficulty();
    String rhsDif = rhs.getDifficulty();
    if(keyOrdering.contains(lhsDif)) {
      if(keyOrdering.contains(rhsDif))
        return (keyOrdering.indexOf(lhsDif) - keyOrdering.indexOf(rhsDif));
      else
        return 1;
    } else if (keyOrdering.contains(rhsDif)) {
      return -1;
    }
    return getDifficulty(lhs) - getDifficulty(rhs);
  }

  public int getDifficulty(Spot.Route route) {
    String difficulty = route.getDifficulty();
    if(difficulty.startsWith("v")) {
      if(difficulty.contains("/"))
        return Integer.parseInt(difficulty.substring(1, difficulty.indexOf("/")));
      else
        return Integer.parseInt(difficulty.substring(1));
    } else {
      return 0;
    }
  }
}