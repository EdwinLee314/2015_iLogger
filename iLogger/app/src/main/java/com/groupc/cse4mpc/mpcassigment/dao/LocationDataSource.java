package com.groupc.cse4mpc.mpcassigment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junqi on 19/10/15.
 */
public class LocationDataSource {
    // Database fields
    private SQLiteDatabase database;
    private LocationSQLiteHelper dbHelper;
    private String[] allColumns = {
            LocationSQLiteHelper.COLUMN_ID,
            LocationSQLiteHelper.COLUMN_Latitude,
            LocationSQLiteHelper.COLUMN_Longitude,
            LocationSQLiteHelper.COLUMN_Address
    };

    public LocationDataSource(Context context) {
        dbHelper = new LocationSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Assign values for each row &
    // Insert the row into your table
    public MyLocation createLocation(double latitude, double longitude, String address)
    {
        ContentValues values = new ContentValues();
        values.put(LocationSQLiteHelper.COLUMN_Latitude, latitude);
        values.put(LocationSQLiteHelper.COLUMN_Longitude, longitude);
        values.put(LocationSQLiteHelper.COLUMN_Address, address);

        long insertId = database.insert(LocationSQLiteHelper.TABLE_Locations, null, values);

        Cursor cursor = database.query(LocationSQLiteHelper.TABLE_Locations,
                allColumns, LocationSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);


        cursor.moveToFirst();
        MyLocation newLocation = cursorToLocation(cursor);
        cursor.close();

        return newLocation;
    }

    public void deleteLocation(MyLocation location) {
        long id = location.getId();
        System.out.println("Location deleted with id: " + id);
        database.delete(LocationSQLiteHelper.TABLE_Locations, LocationSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    //Extracting values from a Cursor
    public List<MyLocation> getAllLocations() {

        List<MyLocation> locations = new ArrayList<MyLocation>();
        Cursor cursor = database.query(LocationSQLiteHelper.TABLE_Locations,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyLocation location = cursorToLocation(cursor);
            locations.add(location);
            cursor.moveToNext();
        }
        cursor.close();

        return locations;
    }

    private MyLocation cursorToLocation(Cursor cursor) {
        MyLocation location = new MyLocation();
        location.setId(cursor.getLong(0));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
        location.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
        location.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        return location;
    }

}