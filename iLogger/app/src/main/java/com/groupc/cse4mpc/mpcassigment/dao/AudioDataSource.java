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
public class AudioDataSource {
    // Database fields
    private SQLiteDatabase database;
    private AudioSQLiteHelper dbHelper;
    private String[] allColumns = {
            AudioSQLiteHelper.COLUMN_ID,
            AudioSQLiteHelper.COLUMN_Filepath,
            AudioSQLiteHelper.COLUMN_Description,
            AudioSQLiteHelper.COLUMN_Time,
            AudioSQLiteHelper.COLUMN_Location
    };

    public AudioDataSource(Context context) {
        dbHelper = new AudioSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Assign values for each row &
    // Insert the row into your table
    public MyAudio createAudio(String filepath, String description, String time, String location) {
        ContentValues values = new ContentValues();
        values.put(AudioSQLiteHelper.COLUMN_Filepath,filepath);
        values.put(AudioSQLiteHelper.COLUMN_Description, description);
        values.put(AudioSQLiteHelper.COLUMN_Time, time);
        values.put(AudioSQLiteHelper.COLUMN_Location, location);

        long insertId = database.insert(AudioSQLiteHelper.TABLE_Audios, null,values);
        Cursor cursor = database.query(AudioSQLiteHelper.TABLE_Audios,
                allColumns, AudioSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MyAudio newAudio = cursorToAudio(cursor);
        cursor.close();
        return newAudio;
    }

    public void deleteAudio(MyAudio Audio)
    {
        long id = Audio.getId();
        System.out.println("Location deleted with id: " + id);
        database.delete(AudioSQLiteHelper.TABLE_Audios, AudioSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public MyAudio getMyAudioById(long id){
        Cursor cursor = database.query(AudioSQLiteHelper.TABLE_Audios,allColumns, AudioSQLiteHelper.COLUMN_ID +" = " + id, null, null, null,null);
        cursor.moveToFirst();
        MyAudio newAudio = cursorToAudio(cursor);
        cursor.close();
        return newAudio;
    }

    //Extracting values from a Cursor
    public List<MyAudio> getAllAudios() {
        List<MyAudio> Audios = new ArrayList<MyAudio>();
        Cursor cursor = database.query(AudioSQLiteHelper.TABLE_Audios,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyAudio Audio = cursorToAudio(cursor);
            Audios.add(Audio);
            cursor.moveToNext();
        }
        cursor.close();
        return Audios;
    }

    private MyAudio cursorToAudio(Cursor cursor) {
        MyAudio Audio = new MyAudio();
        Audio.setId(cursor.getLong(0));
        Audio.setFilepath(cursor.getString(cursor.getColumnIndex("filepath")));
        Audio.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        Audio.setTime(cursor.getString(cursor.getColumnIndex("time")));
        Audio.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        return Audio;
    }
}
