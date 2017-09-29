package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Created by shalom on 2017-04-23.
 */

public class PetsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetsEntry.TABLE_NAME + " ("
                    + PetsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PetsEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, "
                    + PetsEntry.COLUMN_NAME_BREED + " TEXT, "
                    + PetsEntry.COLUMN_NAME_GENDER + " INTEGER NOT NULL, "
                    + PetsEntry.COLUMN_NAME_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
    private static final String SQL_DELETE_PETS_TABLE = "DROP TABLE IF EXISTS " + PetsEntry.TABLE_NAME;

    public PetsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL(SQL_DELETE_PETS_TABLE);
//        onCreate(sqLiteDatabase);
    }
}
