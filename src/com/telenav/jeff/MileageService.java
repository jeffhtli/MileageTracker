package com.telenav.jeff;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.telenav.jeff.trip.TripManager;

public class MileageService extends Service 
{

    private static final String LOG_TAG = "MileageService";
    
    private final IBinder binder = new MileageServiceBinder();
    private TripManager tripManager;
    
    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "onCreate");
        tripManager = new TripManager(getApplicationContext());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOG_TAG, "onStartCommand");
        return Service.START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent arg0)
    {
        return binder;
    }
    
    public class MileageServiceBinder extends Binder {
        TripManager getTripManager() {
            return MileageService.this.tripManager;
        }
    }
    
}
