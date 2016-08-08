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
public class WiFiDataSource {
    // Database fields
    private SQLiteDatabase database;
    private WiFiSQLiteHelper dbHelper;
    private String[] allColumns = {
            WiFiSQLiteHelper.COLUMN_ID,
            WiFiSQLiteHelper.COLUMN_Summary,
            WiFiSQLiteHelper.COLUMN_Time,
            WiFiSQLiteHelper.COLUMN_Location
    };

    public WiFiDataSource(Context context) {
        dbHelper = new WiFiSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Assign values for each row &
    // Insert the row into your table
    public MyWiFi createWiFi(String summary, String time, String location) {
        ContentValues values = new ContentValues();
        values.put(WiFiSQLiteHelper.COLUMN_Summary, summary);
        values.put(WiFiSQLiteHelper.COLUMN_Time, time);
        values.put(WiFiSQLiteHelper.COLUMN_Location, location);

        long insertId = database.insert(WiFiSQLiteHelper.TABLE_Wifis, null,values);
        Cursor cursor = database.query(WiFiSQLiteHelper.TABLE_Wifis,
                allColumns, WiFiSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MyWiFi newWiFi = cursorToWiFi(cursor);
        cursor.close();
        return newWiFi;
    }

    public void deleteWiFi(MyWiFi wiFi)
    {
        long id = wiFi.getId();
        System.out.println("Location deleted with id: " + id);
        database.delete(WiFiSQLiteHelper.TABLE_Wifis, WiFiSQLiteHelper.COLUMN_ID + " = " + id, null);
    }
    public MyWiFi getMyWiFiById(long id){
        Cursor cursor = database.query(WiFiSQLiteHelper.TABLE_Wifis,allColumns, WiFiSQLiteHelper.COLUMN_ID +" = " + id, null, null, null,null);
        cursor.moveToFirst();
        MyWiFi newWiFi = cursorToWiFi(cursor);
        cursor.close();
        return newWiFi;
    }

    //Extracting values from a Cursor
    public List<MyWiFi> getAllWiFis()
    {
        List<MyWiFi> wiFis = new ArrayList<MyWiFi>();
        Cursor cursor = database.query(WiFiSQLiteHelper.TABLE_Wifis,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyWiFi wiFi = cursorToWiFi(cursor);
            wiFis.add(wiFi);
            cursor.moveToNext();
        }
        cursor.close();
        return wiFis;
    }

    public List<String> getAllWiFisTime()
    {
        List<String> wiFis = new ArrayList<String>();
        Cursor cursor = database.query(WiFiSQLiteHelper.TABLE_Wifis,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyWiFi wiFi = cursorToWiFi(cursor);
            wiFis.add(wiFi.getTime());
            cursor.moveToNext();
        }
        cursor.close();
        return wiFis;
    }

    private MyWiFi cursorToWiFi(Cursor cursor)
    {
        MyWiFi wiFi = new MyWiFi();
        wiFi.setId(cursor.getLong(0));
        wiFi.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
        wiFi.setTime(cursor.getString(cursor.getColumnIndex("time")));
        wiFi.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        return wiFi;
    }
}
