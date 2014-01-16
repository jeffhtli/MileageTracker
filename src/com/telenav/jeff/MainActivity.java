package com.telenav.jeff;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.telenav.jeff.LocationService.LocationServiceBinder;
import com.telenav.jeff.vo.GPSData;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private TextView serviceStatusTextView;
    private TextView distanceTextView;
    private TextView latTextView;
    private TextView lonTextView;
    private TextView speedTextView;
    private TextView headingTextView;
    private Button serviceSwitchBtn;
    private Button tripHistoryBtn;
    
    private LocationService locationService;
    private ScheduledExecutorService scheduleService;
    private LocationServiceConnection locationServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initialze();
    }
    
    private void initialze()
    {
        serviceStatusTextView = (TextView) findViewById(R.id.statusfield);
        distanceTextView = (TextView) findViewById(R.id.distancefield);
        latTextView = (TextView) findViewById(R.id.latfield);
        lonTextView = (TextView) findViewById(R.id.lonfield);
        speedTextView = (TextView) findViewById(R.id.speedfield);
        headingTextView = (TextView) findViewById(R.id.headingfield);
        
        serviceSwitchBtn = (Button) findViewById(R.id.switchbtn);
        serviceSwitchBtn.setText("Stop");
        serviceSwitchBtn.setOnClickListener(new SwitchBtnClickListener());
        
        tripHistoryBtn = (Button) findViewById(R.id.historybtn);
        tripHistoryBtn.setText("View trip History");
        
        serviceStatusTextView.setText("Started");
        distanceTextView.setText("0 mile");
        latTextView.setText("37.37348");
        lonTextView.setText("-121.99899");
        speedTextView.setText("23.123");
        headingTextView.setText("45");
        
        locationServiceConnection = new LocationServiceConnection();

        bindService(new Intent(this, LocationService.class), locationServiceConnection, BIND_AUTO_CREATE);
        
        startUpdateScreen();
    }
    
    private void startUpdateScreen()
    {
        scheduleService = Executors.newSingleThreadScheduledExecutor();
        scheduleService.scheduleAtFixedRate(new Updater(), 0, 3 * 1000, TimeUnit.MILLISECONDS);
    }
    
    private class LocationServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            locationService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            locationService = ((LocationServiceBinder)service).getService();
            if (locationService.isLocationServiceStarted())
            {
                serviceSwitchBtn.setText("Stop");
            }
            else
            {
                serviceSwitchBtn.setText("Start");
            }
        }
    }
    
    private class Updater extends Thread
    {
        @Override
        public void run()
        {
            System.out.println("schedule task: update screen");
            updateScreen();
        }
    }
    
    private class SwitchBtnClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if (locationService.isLocationServiceStarted())
            {
                locationService.stopLocationUpdate();
                scheduleService.shutdown();
                serviceSwitchBtn.setText("Start");
            }
            else
            {
                locationService.requestLocationUpdate();
                startUpdateScreen();
                serviceSwitchBtn.setText("Stop");
            }
        }
    }
    
    private void updateScreen()
    {
        if (locationService == null)
        {
            return;
        }
        
        final GPSData loc = locationService.getLastKnownLocation();
        if (loc != null)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    distanceTextView.setText("Caclulating...");
                    latTextView.setText(String.valueOf(loc.lat));
                    lonTextView.setText(String.valueOf(loc.lon));
                    speedTextView.setText(loc.speed + " m/s");
                    headingTextView.setText(String.valueOf(loc.heading));
                }
            });
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            return this.moveTaskToBack(false);
        }
        else
        {
            return super.onKeyUp(keyCode, event);   
        }
    }
    
}
