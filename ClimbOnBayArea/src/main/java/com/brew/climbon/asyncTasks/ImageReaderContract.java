package com.brew.climbon.asyncTasks;

import android.provider.BaseColumns;

/**
 * Defines table contents and names. Check {@link com.brew.climbon.asyncTasks.DatabaseHandler}
 */
public class ImageReaderContract {
    public ImageReaderContract() {}

    public static abstract class ImageReaderEntry implements BaseColumns {
        public static final String TABLE_NAME = "routes";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
