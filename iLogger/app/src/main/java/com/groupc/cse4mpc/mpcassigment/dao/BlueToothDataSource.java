package com.groupc.cse4mpc.mpcassigment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junqi on 20/10/15.
 */
public class BlueToothDataSource {
    // Database fields
    private SQLiteDatabase database;
    private BlueToothSQLiteHelper dbHelper;
    private String[] allColumns = {
            BlueToothSQLiteHelper.COLUMN_ID,
            BlueToothSQLiteHelper.COLUMN_Summary,
            BlueToothSQLiteHelper.COLUMN_Time,
            BlueToothSQLiteHelper.COLUMN_Location
    };

    public BlueToothDataSource(Context context) {
        dbHelper = new BlueToothSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Assign values for each row &
    // Insert the row into your table
    public MyBlueTooth createBlueTooth(String summary, String time, String location) {
        ContentValues values = new ContentValues();
        values.put(BlueToothSQLiteHelper.COLUMN_Summary, summary);
        values.put(BlueToothSQLiteHelper.COLUMN_Time, time);
        values.put(BlueToothSQLiteHelper.COLUMN_Location, location);

        long insertId = database.insert(BlueToothSQLiteHelper.TABLE_BlueTooths, null,values);
        Cursor cursor = database.query(BlueToothSQLiteHelper.TABLE_BlueTooths,
                allColumns, BlueToothSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MyBlueTooth newBlueTooth = cursorToBlueTooth(cursor);
        cursor.close();
        return newBlueTooth;
    }

    public void deleteBlueTooth(MyBlueTooth blueTooth)
    {
        long id = blueTooth.getId();
        System.out.println("Location deleted with id: " + id);
        database.delete(BlueToothSQLiteHelper.TABLE_BlueTooths, BlueToothSQLiteHelper.COLUMN_ID + " = " + id, null);
    }
    public MyBlueTooth getMyBlueToothById(int id){
        Cursor cursor = database.query(BlueToothSQLiteHelper.TABLE_BlueTooths,allColumns, BlueToothSQLiteHelper.COLUMN_ID +" = " + id, null, null, null,null);
        cursor.moveToFirst();
        MyBlueTooth newBlueTooth = cursorToBlueTooth(cursor);
        cursor.close();
        return newBlueTooth;
    }

    //Extracting values from a Cursor
    public List<MyBlueTooth> getAllBlueTooths()
    {
        List<MyBlueTooth> blueTooths = new ArrayList<MyBlueTooth>();
        Cursor cursor = database.query(BlueToothSQLiteHelper.TABLE_BlueTooths,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyBlueTooth blueTooth = cursorToBlueTooth(cursor);
            blueTooths.add(blueTooth);
            cursor.moveToNext();
        }
        cursor.close();
        return blueTooths;
    }

    private MyBlueTooth cursorToBlueTooth(Cursor cursor)
    {
        MyBlueTooth blueTooth = new MyBlueTooth();
        blueTooth.setId(cursor.getLong(0));
        blueTooth.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
        blueTooth.setTime(cursor.getString(cursor.getColumnIndex("time")));
        blueTooth.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        return blueTooth;
    }
}
