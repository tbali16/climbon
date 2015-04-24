package com.brew.climbon.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.brew.climbon.helpers.Constants;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Async task to download all routes and update the shared preferences if they are all downloaded.
 * TODO: Change to rx-observable-observer model.
 */
public class DownloadRoutesTask extends AsyncTask<String, Integer, Boolean> {

    private Context mContext;

    public DownloadRoutesTask(Context context) {
        mContext = context;
    }

    protected Boolean doInBackground(String... urlsToDownload) {
        DatabaseHandler dbHandler = new DatabaseHandler(mContext);
        for(String urlToDownload: urlsToDownload) {
            boolean isImageDownloaded = dbHandler.isImageDownloaded(urlToDownload);
            if(!isImageDownloaded)
            {
                URL url = null;
                try {
                    url = new URL(urlToDownload);
                    URLConnection ucon = url.openConnection();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is,128);
                    ByteArrayBuffer baf = new ByteArrayBuffer(128);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                    }
                    dbHandler.writeToDB(urlToDownload, baf.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        /*
         * The return boolean returns true if:
         * a. The request was to download all images (checked by making sure the urls length > 1)
         * b. All images were downloaded and stored.
         * If it returns true, we will not run this task again for a THRESHOLD time.
         */
        return urlsToDownload.length > 1;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            Constants.setAllImagesStored(mContext, result);
        }
    }
}