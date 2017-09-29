package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shalom on 2017-04-23.
 */

public final class PetsContract {

    private PetsContract() { }

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    public static class PetsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static final String TABLE_NAME = "pets";

        //INTEGER PRIMARY KEY AUTOINCREMENT
        public static final String COLUMN_NAME_ID = BaseColumns._ID;
        //TEXT NOT NULL
        public static final String COLUMN_NAME_NAME = "name";
        //TEXT
        public static final String COLUMN_NAME_BREED = "breed";
        //INTEGER NOT NULL DEFAULT 0
        public static final String COLUMN_NAME_GENDER = "gender";
        //INTEGER
        public static final String COLUMN_NAME_WEIGHT = "weight";

        //Gender values interpretation
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

        public static boolean isValidGender(int gender) {
            return gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE;
        }
    }
}