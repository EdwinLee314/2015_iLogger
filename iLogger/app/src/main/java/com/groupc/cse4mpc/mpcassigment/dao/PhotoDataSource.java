package com.groupc.cse4mpc.mpcassigment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junqi on 21/10/15.
 */
public class PhotoDataSource  {
    // Database fields
    private SQLiteDatabase database;
    private PhotoSQLiteHelper dbHelper;
    private String[] allColumns = {
            PhotoSQLiteHelper.COLUMN_ID,
            PhotoSQLiteHelper.COLUMN_Filepath,
            PhotoSQLiteHelper.COLUMN_Description,
            PhotoSQLiteHelper.COLUMN_Time,
            PhotoSQLiteHelper.COLUMN_Location
    };

    public PhotoDataSource(Context context) {
        dbHelper = new PhotoSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Assign values for each row &
    // Insert the row into your table
    public MyPhoto createPhoto(String filepath, String description, String time, String location) {
        ContentValues values = new ContentValues();
        values.put(PhotoSQLiteHelper.COLUMN_Filepath,filepath);
        values.put(PhotoSQLiteHelper.COLUMN_Description, description);
        values.put(PhotoSQLiteHelper.COLUMN_Time, time);
        values.put(PhotoSQLiteHelper.COLUMN_Location, location);

        long insertId = database.insert(PhotoSQLiteHelper.TABLE_Photos, null,values);
        Cursor cursor = database.query(PhotoSQLiteHelper.TABLE_Photos,
                allColumns, PhotoSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MyPhoto newPhoto = cursorToPhoto(cursor);
        cursor.close();
        return newPhoto;
    }

    public void deletePhoto(MyPhoto photo)
    {
        long id = photo.getId();
        System.out.println("Location deleted with id: " + id);
        database.delete(PhotoSQLiteHelper.TABLE_Photos, PhotoSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public MyPhoto getMyPhotoById(long id){
        Cursor cursor = database.query(PhotoSQLiteHelper.TABLE_Photos,allColumns, PhotoSQLiteHelper.COLUMN_ID +" = " + id, null, null, null,null);
        cursor.moveToFirst();
        MyPhoto myPhoto = cursorToPhoto(cursor);
        cursor.close();
        return myPhoto;
    }

    //Extracting values from a Cursor
    public List<MyPhoto> getAllPhotos() {
        List<MyPhoto> photos = new ArrayList<MyPhoto>();
        Cursor cursor = database.query(PhotoSQLiteHelper.TABLE_Photos,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyPhoto photo = cursorToPhoto(cursor);
            photos.add(photo);
            cursor.moveToNext();
        }
        cursor.close();
        return photos;
    }

    private MyPhoto cursorToPhoto(Cursor cursor) {
        MyPhoto photo = new MyPhoto();
        photo.setId(cursor.getLong(0));
        photo.setFilepath(cursor.getString(cursor.getColumnIndex("filepath")));
        photo.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        photo.setTime(cursor.getString(cursor.getColumnIndex("time")));
        photo.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        return photo;
    }
}
