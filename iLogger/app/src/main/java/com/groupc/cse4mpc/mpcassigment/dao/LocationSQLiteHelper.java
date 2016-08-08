package com.groupc.cse4mpc.mpcassigment.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by junqi on 19/10/15.
 */
public class LocationSQLiteHelper extends SQLiteOpenHelper{
    public static final String TABLE_Locations = "locations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_Latitude = "latitude";
    public static final String COLUMN_Longitude = "longitude";
    public static final String COLUMN_Address = "address";

    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_Locations + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_Latitude + " float not null, "
            + COLUMN_Longitude + " float not null, "
            + COLUMN_Address + " text not null);";

    public LocationSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LocationSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Locations);
        onCreate(db);
    }
}