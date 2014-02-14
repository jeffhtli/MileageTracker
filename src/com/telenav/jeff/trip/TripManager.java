package com.telenav.jeff.trip;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.IUIListener;
import com.telenav.jeff.location.AndroidLocationProvider;
import com.telenav.jeff.location.ILocationListener;
import com.telenav.jeff.location.ILocationProvider;
import com.telenav.jeff.location.MviewLocationProvider;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.util.TvMath;
import com.telenav.jeff.vo.mileage.GPSData;
import com.telenav.jeff.vo.mileage.Mileage;

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
    private IUIListener uiListener;
    
    private enum TripStatus
    {
        NotStart,
        Started
    }
    
    public TripManager(Context context)
    {
        status = TripStatus.NotStart;
        //locationProvider = new MviewLocationProvider();
        locationProvider = new AndroidLocationProvider(context);
        gpsDataDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(GPSData.class);
        mileageDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Mileage.class);
    }
    
    public void startTrack()
    {
        if (! isStarted.get())
        {
            locationProvider.requestLocationUpdate("", TripConfig.TIME_UPDATE_GPS, this);
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
                if (System.currentTimeMillis() - timeStamp >= TripConfig.TIME_TRIP_AUTO_END)
                {
                    saveCurrentTrip();
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    private void saveCurrentTrip()
    {
        Log.d(LOG_TAG, "saveCurrentTrip");
        
        if (status == TripStatus.Started)
        {
            mileage.setEndLocation(currentGPS);
            long dist = calcDistance(mileage.getStartLocation(), mileage.getEndLocation());
            if (dist > TripConfig.DISTANCE_VALID_TRIP)
            {
                mileageDAO.create(mileage);
            }
            else
            {
                Log.d(LOG_TAG, "Trip distance is too short, abort!");
            }
            
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

        long distance = calcDistance(currentGPS, data);
        if (distance > TripConfig.DISTANCE_VALID_GPS || currentGPS == null)
        {
            timeStamp = System.currentTimeMillis();
            
            persistGPSData(data);
            
            currentDistance += distance;
            
            currentGPS = data;
            
            if (status == TripStatus.NotStart)
            {
                startNewTrip(data);
            }
            
            if (uiListener != null)
            {
                uiListener.update();
            }
        }
        else
        {
            Log.d(LOG_TAG, "invalid GPS, too close!");
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
    
    public void setUIListener(IUIListener l)
    {
        this.uiListener = l;
    }
    
}
