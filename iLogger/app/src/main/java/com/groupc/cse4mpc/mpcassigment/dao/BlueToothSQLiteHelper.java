package com.groupc.cse4mpc.mpcassigment.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by junqi on 20/10/15.
 */
public class BlueToothSQLiteHelper extends SQLiteOpenHelper{
    public static final String TABLE_BlueTooths = "bluetooths";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_Summary = "summary";
    public static final String COLUMN_Time = "time";
    public static final String COLUMN_Location = "location";

    private static final String DATABASE_NAME = "bluetooths.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_BlueTooths + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_Summary + " text not null, "
            + COLUMN_Time + " text not null, " + COLUMN_Location + " text not null);";

    public BlueToothSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(WiFiSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BlueTooths);
        onCreate(db);
    }
}
