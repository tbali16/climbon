package com.brew.climbon.asyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

/**
 * Handler to store images.
 * Check {@link ImageReaderContract} for table names, columns.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RoutesReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ImageReaderContract.ImageReaderEntry.TABLE_NAME + " (" +
                    ImageReaderContract.ImageReaderEntry._ID + " INTEGER PRIMARY KEY," +
                    ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                    ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE + TEXT_TYPE +
            " )";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void writeToDB(String imageUrl, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE, image);
        values.put(ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE_URL, imageUrl);

        db.insert(ImageReaderContract.ImageReaderEntry.TABLE_NAME,
                  null,
                  values);
        db.close();
    }

    public boolean isImageDownloaded(String imageUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, ImageReaderContract.ImageReaderEntry.TABLE_NAME,
                ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE_URL + "=?", new String[]{imageUrl}) > 0;
    }

    public Bitmap getImage(String imageUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ImageReaderContract.ImageReaderEntry._ID,
                ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE,
                ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE_URL
        };
        Cursor cursor = db.query(
                ImageReaderContract.ImageReaderEntry.TABLE_NAME,
                projection,
                ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE_URL + "=?",
                new String[] { String.valueOf(imageUrl) }, null, null, null, null);

        try {
            if (cursor != null)
                cursor.moveToFirst();
            byte[] image = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(ImageReaderContract.ImageReaderEntry.COLUMN_NAME_IMAGE)
            );
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            return BitmapFactory.decodeStream(imageStream);
        } catch (Exception e)
        {
            return null;
        }
    }
}
