package com.telenav.jeff.location;

import com.telenav.jeff.vo.mileage.GPSData;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class AndroidLocationProvider implements ILocationProvider
{
    public static final String LOG_TAG = "AndroidLocationProvider";
    private LocationManager locationManager;
    private SimpleLocationListener nativeLocListener;
    private ILocationListener locationListener;

    public AndroidLocationProvider(Context context)
    {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void requestLocationUpdate(String provider, long interval, ILocationListener listener)
    {
        if (locationManager != null)
        {
            locationListener = listener;
            nativeLocListener = new SimpleLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, nativeLocListener);
        }
    }

    @Override
    public void stopLocationUpdate()
    {
        if (locationManager != null)
        {
            locationManager.removeUpdates(nativeLocListener);
        }

    }
    
    private class SimpleLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            Log.d(LOG_TAG, String.format("new GPS arrived: %s,%s", location.getLatitude(), location.getLongitude()));
            GPSData gps = new GPSData();
            gps.setTimeStamp(location.getTime());
            gps.setLat(location.getLatitude());
            gps.setLon(location.getLongitude());
            
            if (locationListener != null)
            {
                locationListener.locationUpdate(gps);
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            
        }
    }

}
