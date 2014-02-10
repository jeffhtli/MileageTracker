package com.telenav.jeff;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.telenav.jeff.MileageService.MileageServiceBinder;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.trip.TripManager;
import com.telenav.jeff.vo.GPSData;

public class MainActivity extends Activity
{
    private static final String LOG_TAG = "MainActivity";
    private TextView serviceStatusTextView;
    private TextView distanceTextView;
    private TextView locationTextView;
    private Button serviceSwitchBtn;
    private Button tripHistoryBtn;
    
    private TripManager tripManager;
    private LocationServiceConnection locationServiceConnection;
    private ScheduledExecutorService uiScheduleTask;
    private Intent mileageServiceIntent;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initialze();
    }
    
    private void initialze()
    {
        DatabaseHelper.init(this);
        
        serviceStatusTextView = (TextView) findViewById(R.id.statusfield);
        distanceTextView = (TextView) findViewById(R.id.distance_field);
        locationTextView = (TextView) findViewById(R.id.location_field);
        
        serviceStatusTextView.setText("Started");
        distanceTextView.setText("-- MI");
        locationTextView.setText("--, --");
        
        serviceSwitchBtn = (Button) findViewById(R.id.switchbtn);
        serviceSwitchBtn.setText("Stop");
        serviceSwitchBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tripManager.isTracking())
                {
                    tripManager.stopStrack();
                    updateViewContent(false);
                }
                else
                {
                    tripManager.startTrack();
                    updateViewContent(true);
                }
            }
        });
        
        tripHistoryBtn = (Button) findViewById(R.id.historybtn);
        tripHistoryBtn.setText("View trip History");
        tripHistoryBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, TripsActivity.class));
            }
        });
        
        initService();
        
    }

    private void initService()
    {
        mileageServiceIntent = new Intent(this, MileageService.class);
        startService(mileageServiceIntent);
        locationServiceConnection = new LocationServiceConnection();
        bindService(mileageServiceIntent, locationServiceConnection, BIND_AUTO_CREATE);
        
    }

    private void updateViewContent(boolean isLocServiceStarted)
    {
        if (isLocServiceStarted)
        {
            serviceStatusTextView.setText("Started");
            serviceSwitchBtn.setText("Stop");
            
            uiScheduleTask = Executors.newScheduledThreadPool(1);
            uiScheduleTask.scheduleAtFixedRate(new Runnable()
            {
                @Override
                public void run()
                {
                    updateDistAndLoc();
                    
                }
            }, 0, 20, TimeUnit.SECONDS);
        }
        else
        {
            serviceStatusTextView.setText("Stopped");
            serviceSwitchBtn.setText("Start");
            
            if (uiScheduleTask != null)
            {
                uiScheduleTask.shutdown();
            }
        }
         
    }
    
    private void updateDistAndLoc()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                distanceTextView.setText(tripManager.getCurrentDistance() + " Meters");
                
                GPSData currentGPS = tripManager.getCurrentGPS();
                String locString = "Unknown";
                if (currentGPS != null)
                {
                    locString = currentGPS.getLat() + ", " + currentGPS.getLon();
                }
                locationTextView.setText(locString);
            }
        });
    }
    
    private class LocationServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            tripManager = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            tripManager = ((MileageServiceBinder)service).getTripManager();
            updateViewContent(tripManager.isTracking());
        }
    }
    
    @Override
    protected void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy");
        unbindService(locationServiceConnection);
        
        if (!tripManager.isTracking())
        {
            stopService(mileageServiceIntent);
        }
        super.onDestroy();
    }
}
