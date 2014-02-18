package com.telenav.jeff.sqlite;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.telenav.jeff.R;
import com.telenav.jeff.vo.mileage.BtDevice;
import com.telenav.jeff.vo.mileage.GPSData;
import com.telenav.jeff.vo.mileage.Mileage;
import com.telenav.jeff.vo.mileage.Segment;
import com.telenav.jeff.vo.mileage.Trip;
import com.telenav.jeff.vo.mileage.TripCategory;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "mileagetracker.db";

    private static final int DATABASE_VERSION = 1;
    
    private static DatabaseHelper instance;
    
    private static Context ctx;
    
    private static List<TripCategory> tripCategorys;

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
            TableUtils.createTable(connectionSource, Mileage.class);
            TableUtils.createTable(connectionSource, BtDevice.class);
            
            initCategoryData();
        }
        catch (SQLException e)
        {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }
    
    private void initCategoryData()
    {
        RuntimeExceptionDao<TripCategory, Integer> tripCategoryDAO = getRuntimeExceptionDao(TripCategory.class);
        
        TripCategory category = new TripCategory();
        category.setName("Personal");
        tripCategoryDAO.create(category);
        
        category = new TripCategory();
        category.setName("Business");
        tripCategoryDAO.create(category);
        
        RuntimeExceptionDao<Trip, Integer> tripDAO = getRuntimeExceptionDao(Trip.class);
        Trip trip = new Trip();
        trip.setDistance(48000);
        trip.setStartAddress("1130 Kifer Rd, Sunnyvalue, CA");
        trip.setEndAddress("SFO");
        trip.setStartTimeStamp(System.currentTimeMillis());
        trip.setCategroy(category);
        tripDAO.create(trip);
        
        trip = new Trip();
        trip.setDistance(2400);
        trip.setStartAddress("1155 Reed Avenue, Sunnyvalue, CA");
        trip.setEndAddress("950 De Guigne Dr, Sunnyvale, CA 94085");
        trip.setStartTimeStamp(System.currentTimeMillis());
        trip.setCategroy(category);
        tripDAO.create(trip);
        
        trip = new Trip();
        trip.setDistance(13800);
        trip.setStartAddress("Telenav");
        trip.setEndAddress("San Jose, CA");
        trip.setStartTimeStamp(System.currentTimeMillis());
        trip.setCategroy(category);
        tripDAO.create(trip);
        
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
            TableUtils.dropTable(connectionSource, Mileage.class, true);
            TableUtils.dropTable(connectionSource, BtDevice.class, true);
            
            onCreate(db, connectionSource);
        }
        catch (SQLException e)
        {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }

    }
    
    public List<TripCategory> getTripCategorys()
    {
        if (tripCategorys == null)
        {
            RuntimeExceptionDao<TripCategory, Integer> tripCategoryDAO = getRuntimeExceptionDao(TripCategory.class);
            tripCategorys = tripCategoryDAO.queryForAll();
        }
        
        return tripCategorys;
    }

}
