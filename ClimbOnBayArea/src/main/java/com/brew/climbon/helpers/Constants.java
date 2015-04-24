package com.brew.climbon.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Constants.
 */
public class Constants {
    public static final String PREFS_NAME = "Preferences";
    public static final String FEEDBACK_EMAIL_SUBJECT = "climbOn Android App";
    public static final String FEEDBACK_EMAIL_DESTINATION = "climbon.bayarea@gmail.com";
    public static final String ALL_IMAGES_STORED_KEY = "ALL_IMAGES_STORED";

    public static void setAllImagesStored(Context context, boolean isAllImagesStored) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(ALL_IMAGES_STORED_KEY, isAllImagesStored);
        editor.commit();
    }

    public static Boolean hasAllImagesStored(Context context) {
        return getSharedPreferences(context).getBoolean(ALL_IMAGES_STORED_KEY, false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Boolean isNetAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
