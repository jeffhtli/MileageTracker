package com.telenav.jeff.trip;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.location.ILocationListener;
import com.telenav.jeff.location.ILocationProvider;
import com.telenav.jeff.location.MviewLocationProvider;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.util.TvMath;
import com.telenav.jeff.vo.GPSData;
import com.telenav.jeff.vo.Mileage;
import com.telenav.jeff.vo.Segment;
import com.telenav.jeff.vo.Trip;

public class TripManager implements ILocationListener
{
    private static final String LOG_TAG = "TripManager";
    private GPSData currentGPS;
    private long currentDistance;
    private long timeStamp;
    
    private Mileage mileage;
    private TripStatus status;
    
    private AtomicBoolean isStarted = new AtomicBoolean();
    
    private ILocationProvider locationProvider;
    
    private RuntimeExceptionDao<GPSData, Integer> gpsDataDAO;
    private RuntimeExceptionDao<Mileage, Integer> mileageDAO;
    private ScheduledExecutorService timeoutPool;
    
    private enum TripStatus
    {
        NotStart,
        Started
    }
    
    public TripManager()
    {
        status = TripStatus.NotStart;
        locationProvider = new MviewLocationProvider();
        gpsDataDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(GPSData.class);
        mileageDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Mileage.class);
    }
    
    public void startTrack()
    {
        if (! isStarted.get())
        {
            locationProvider.requestLocationUpdate("", 10 * 1000, this);
            isStarted.set(true);
        }
    }
    
    public void stopStrack()
    {
        locationProvider.stopLocationUpdate();
        saveCurrentTrip();
        isStarted.set(false);
    }
    
    public boolean isTracking()
    {
        return isStarted.get();
    }
     
    private void persistGPSData(GPSData data)
    {
        gpsDataDAO.create(data);
    }

    private boolean isValidGPS(GPSData data)
    {
        return true;
    }

    private void startNewTrip(GPSData data)
    {
        Log.d(LOG_TAG, "startNewTrip");
        
        mileage = new Mileage();
        mileage.setStartLocation(data);
        mileage.setStartTimeStamp(System.currentTimeMillis());
        
        status = TripStatus.Started;
        
        startTimeoutTask();
    }

    private void startTimeoutTask()
    {
        timeoutPool = Executors.newScheduledThreadPool(1);
        timeoutPool.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                if (System.currentTimeMillis() - timeStamp >= 60 * 1000)
                {
                    saveCurrentTrip();
                }
            }
        }, 60, 30, TimeUnit.SECONDS);
    }
    
    private void saveCurrentTrip()
    {
        Log.d(LOG_TAG, "saveCurrentTrip");
        
        if (status == TripStatus.Started)
        {
            mileage.setEndLocation(currentGPS);
            mileageDAO.create(mileage);
            
            timeoutPool.shutdown();
            
            currentGPS = null;
            currentDistance = 0;
            status = TripStatus.NotStart;
        }
    }

    @Override
    public void locationUpdate(GPSData data)
    {
        Log.d(LOG_TAG, "new GPS arrive! ");
        timeStamp = System.currentTimeMillis();
        
        if (isValidGPS(data))
        {
            persistGPSData(data);
            
            currentDistance += calcDistance(currentGPS, data);
            
            currentGPS = data;
            
            if (status == TripStatus.NotStart)
            {
                startNewTrip(data);
            }
        }
    }
    
    private long calcDistance(GPSData lastGPS, GPSData currentGPS)
    {
        if (lastGPS == null || currentGPS == null)
            return 0;
        
        long dist = 0;
        try
        {
            dist = TvMath.calcDist(lastGPS.getLat(), lastGPS.getLon(), currentGPS.getLat(), currentGPS.getLon());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return dist;
    }
    
    public GPSData getCurrentGPS()
    {
        return this.currentGPS;
    }
    
    public long getCurrentDistance()
    {
        return this.currentDistance;
    }
       
    
}
