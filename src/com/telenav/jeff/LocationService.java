package com.telenav.jeff;

import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.telenav.jeff.location.ILocationListener;
import com.telenav.jeff.location.ILocationProvider;
import com.telenav.jeff.location.MviewLocationProvider;
import com.telenav.jeff.sqlite.GPSRecordDAO;
import com.telenav.jeff.vo.GPSData;

public class LocationService extends Service implements ILocationListener
{

    private final IBinder binder = new LocationServiceBinder();
    private ILocationProvider locationProvider;
    private Vector<GPSData> gpsDataList;
    private GPSRecordDAO gpsRecordDAO;
    
    @Override
    public void onCreate()
    {
        gpsDataList = new Vector<GPSData>();
        locationProvider = new MviewLocationProvider();
        gpsRecordDAO = new GPSRecordDAO(getApplicationContext());
        //requestLocationUpdate();
    }
    
    @Override
    public IBinder onBind(Intent arg0)
    {
        return binder;
    }
    
    @Override
    public void locationUpdate(GPSData data)
    {
        recordGPSData(data); 
    }
    
    private void recordGPSData(GPSData data)
    {
        synchronized (gpsDataList)
        {
            if (gpsDataList.size() >= 10)
            {
                gpsRecordDAO.addGPSData(gpsDataList);
                gpsDataList.clear();
            }
            gpsDataList.insertElementAt(data, 0);
        }
    }

    public class LocationServiceBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    
    public boolean isLocationServiceStarted()
    {
        return locationProvider.isStarted();
    }
    
    public GPSData getLastKnownLocation()
    {
        return gpsDataList.elementAt(0);
    }
    
    public void stopLocationUpdate()
    {
        locationProvider.stopLocationUpdate();
    }
    
    public void requestLocationUpdate()
    {
        if (! locationProvider.isStarted())
        {
            locationProvider.requestLocationUpdate("", 60 * 1000, this);
        }
    }

}
