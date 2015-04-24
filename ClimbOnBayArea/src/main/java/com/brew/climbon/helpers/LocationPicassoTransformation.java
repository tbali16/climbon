package com.brew.climbon.helpers;

import android.graphics.Bitmap;

import com.makeramen.RoundedDrawable;
import com.squareup.picasso.Transformation;

/**
 * Transformation for picasso image on {@link com.brew.climbon.fragments.SpotLogisticsFragment}
 */
public class LocationPicassoTransformation implements Transformation {
    float radius;
    int border;
    boolean oval;
    int color;

    public LocationPicassoTransformation(float radius, int border, int color) {
        this.radius = radius;
        this.border = border;
        this.color = color;
        this.oval = false;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap transformed = RoundedDrawable.fromBitmap(bitmap)
                .setBorderColor(color)
                .setBorderWidth(border)
                .setCornerRadius(radius)
                .setOval(oval)
                .toBitmap();
        if (!bitmap.equals(transformed)) {
            bitmap.recycle();
        }
        return transformed;
    }

    @Override
    public String key() {
        return "rounded_radius_" + radius + "_border_" + border + "_color_" + color + "_oval_" + oval;
    }
}
