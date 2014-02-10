package com.telenav.jeff.sqlite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.telenav.jeff.R;
import com.telenav.jeff.vo.ExtraCost;
import com.telenav.jeff.vo.ExtraCostType;
import com.telenav.jeff.vo.GPSData;
import com.telenav.jeff.vo.Mileage;
import com.telenav.jeff.vo.Segment;
import com.telenav.jeff.vo.Trip;
import com.telenav.jeff.vo.TripCategory;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "mileagetracker.db";

    private static final int DATABASE_VERSION = 1;
    
    private static DatabaseHelper instance;
    
    private static Context ctx;

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }
    
    public static void init(Context context)
    {
        ctx = context;
    }
    
    public static DatabaseHelper getInstance()
    {
        if (instance == null)
        {
            instance = new DatabaseHelper(ctx);
        }
        
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1)
    {
        try
        {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Trip.class);
            TableUtils.createTable(connectionSource, TripCategory.class);
            TableUtils.createTable(connectionSource, Segment.class);
            TableUtils.createTable(connectionSource, GPSData.class);
            TableUtils.createTable(connectionSource, ExtraCost.class);
            TableUtils.createTable(connectionSource, ExtraCostType.class);
            TableUtils.createTable(connectionSource, Mileage.class);
        }
        catch (SQLException e)
        {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource arg1, int arg2, int arg3)
    {
        try
        {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Trip.class, true);
            TableUtils.dropTable(connectionSource, TripCategory.class, true);
            TableUtils.dropTable(connectionSource, Segment.class, true);
            TableUtils.dropTable(connectionSource, GPSData.class, true);
            TableUtils.dropTable(connectionSource, ExtraCost.class, true);
            TableUtils.dropTable(connectionSource, ExtraCostType.class, true);
            TableUtils.dropTable(connectionSource, Mileage.class, true);
            
            onCreate(db, connectionSource);
        }
        catch (SQLException e)
        {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }

    }

}
