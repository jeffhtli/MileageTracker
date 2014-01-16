package com.telenav.jeff.sqlite;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.telenav.jeff.vo.GPSData;

public class GPSRecordDAO extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "mymileage.db";

    private static final String TABLE_NAME = "gpsrecord";

    private static final String COLUMN_LAT = "lat";

    private static final String COLUMN_LON = "lon";

    private static final String COLUMN_HEADING = "heading";

    private static final String COLUMN_SPEED = "speed";

    private static final String LOG_TAG = "GPSRecordHandler";

    public GPSRecordDAO(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_LAT + " REAL, " + COLUMN_LON + " REAL, " + COLUMN_HEADING + " REAL, "
                + COLUMN_SPEED + " REAL)";
        Log.d(LOG_TAG, sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addGPSData(List<GPSData> list)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for(GPSData data : list)
        {
            ContentValues value = new ContentValues();
            value.put(COLUMN_LAT, data.lat);
            value.put(COLUMN_LON, data.lon);
            value.put(COLUMN_SPEED, data.speed);
            value.put(COLUMN_HEADING, data.heading);
            db.insert(TABLE_NAME, null, value);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<GPSData> getAllGPSData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        List<GPSData> result = new Vector<GPSData>();
        
        while (cursor.moveToNext())
        {
            GPSData data = new GPSData();
            data.lat = cursor.getDouble(0);
            data.lon = cursor.getDouble(1);
            data.speed = cursor.getDouble(2);
            data.heading = cursor.getDouble(3);
            result.add(data);
            
        }
        return result;
    }
}
