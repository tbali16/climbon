package com.brew.climbon.helpers;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.brew.climbon.fragments.GridViewFragment;


/**
 * A custom square layout used by {@link com.brew.climbon.fragments.GridViewFragment}
 */
public class SquareLayout extends FrameLayout
{
  public SquareLayout(Context context)
  {
    super(context);
  }

  public SquareLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public SquareLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec,
                           int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
    int actionBarSize = (int) styledAttributes.getDimension(0, 0);
    styledAttributes.recycle();

    if(GridViewFragment.SCREEN_HEIGHT != 0)
      setMeasuredDimension(width, ((GridViewFragment.SCREEN_HEIGHT - actionBarSize - getStatusBarHeight()) / 2) + 1);
  }

  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}
