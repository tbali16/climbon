package com.brew.climbon.helpers;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

/*
 * Parallax scroll view to provide parallax scroll to the image in spot information.
 */
public class ParallaxScrollView extends ScrollView {

  private static int NUM_OF_PARALLAX_VIEWS = 1;
  private static float INNER_PARALLAX_FACTOR= 1.9F;
  private static float PARALLAX_FACTOR = 1.9F;
  private ArrayList<ParallaxedView> parallaxedViews = new ArrayList<ParallaxedView>();

  public ParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public ParallaxScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ParallaxScrollView(Context context) {
    super(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    makeViewsParallax();
  }

  private void makeViewsParallax() {
    if (getChildCount() > 0 && getChildAt(0) instanceof ViewGroup) {
      ViewGroup viewsHolder = (ViewGroup) getChildAt(0);
      int numOfParallaxViews = Math.min(this.NUM_OF_PARALLAX_VIEWS, viewsHolder.getChildCount());
      for (int i = 0; i < numOfParallaxViews; i++) {
        ParallaxedView parallaxedView = new ParallaxedScrollView(viewsHolder.getChildAt(i));
        parallaxedViews.add(parallaxedView);
      }
    }
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    float factor = PARALLAX_FACTOR;
    for (ParallaxedView parallaxedView : parallaxedViews) {
      parallaxedView.setOffset((float)t / factor);
      factor *= INNER_PARALLAX_FACTOR;
    }
  }

  public class ParallaxedScrollView extends ParallaxedView{
    public ParallaxedScrollView(View view) {
      super(view);
    }

    @Override
    protected void translatePreICS(View view, float offset) {
      view.offsetTopAndBottom((int)offset - lastOffset);
      lastOffset = (int)offset;
    }
  }
}