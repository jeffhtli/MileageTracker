package com.telenav.jeff;

import java.util.List;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.concur.service.TokenManager;
import com.telenav.jeff.MileageService.MileageServiceBinder;
import com.telenav.jeff.service.BtDiscoveryListener;
import com.telenav.jeff.service.BtService;
import com.telenav.jeff.service.CalendarService;
import com.telenav.jeff.service.ContactsService;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.trip.TripManager;
import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.mileage.BtDevice;
import com.telenav.jeff.vo.mileage.GPSData;

public class MainActivity extends Activity implements IUIListener, BtDiscoveryListener
{
    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private TextView serviceStatusTextView;
    private TextView distanceTextView;
    private TextView locationTextView;
    private Button serviceSwitchBtn;
    private Button tripHistoryBtn;
    
    private TripManager tripManager;
    private LocationServiceConnection locationServiceConnection;
    private Intent mileageServiceIntent;
    private BtService btService;
    private RuntimeExceptionDao<BtDevice, Integer> btDeviceDAO;
    private TextView btStatusTextView;
    
    private boolean btDeviceFound;
    private boolean isCarConfig;
    private Button btBtn;
    

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
        btDeviceDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(BtDevice.class);
        
        TokenManager.init(this);
        
        ContactsService.getInstance().init(this);
        CalendarService.getInstance().init(this);
        
        initView();
        
        initService();
        
    }
    
    private void initView()
    {
        serviceStatusTextView = (TextView) findViewById(R.id.statusfield);
        distanceTextView = (TextView) findViewById(R.id.distance_field);
        locationTextView = (TextView) findViewById(R.id.location_field);
        btStatusTextView = (TextView) findViewById(R.id.main_btstatus_textview);
        
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
                startActivity(new Intent(MainActivity.this, TripListActivity.class));
            }
        });
        
        btBtn = (Button) findViewById(R.id.main_bluetooth_btn);
        btBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
            }
        });
    }

    private void initService()
    {
        mileageServiceIntent = new Intent(this, MileageService.class);
        startService(mileageServiceIntent);
        locationServiceConnection = new LocationServiceConnection();
        bindService(mileageServiceIntent, locationServiceConnection, BIND_AUTO_CREATE);
        
        new Thread(){
            public void run() {
                ContactsService.getInstance().syncContacts();
                //CalendarService.getInstance().syncCalendar();
            };
        }.start();
    }

    private void updateViewContent(boolean isLocServiceStarted)
    {
        if (isLocServiceStarted)
        {
            serviceStatusTextView.setText("Started");
            serviceSwitchBtn.setText("Stop");
        }
        else
        {
            serviceStatusTextView.setText("Stopped");
            serviceSwitchBtn.setText("Start");
        }
        
        updateDistAndLoc();
         
    }
    
    private void updateDistAndLoc()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                distanceTextView.setText(TextUtil.convert2Mile(tripManager.getCurrentDistance(), true));
                
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
            tripManager.setUIListener(null);
            tripManager = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            tripManager = ((MileageServiceBinder)service).getTripManager();
            tripManager.setUIListener(MainActivity.this);
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
        
        if (btService != null)
        {
            btService.stopDiscovery();
        }
        super.onDestroy();
    }

    @Override
    public void update()
    {
        updateDistAndLoc();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                startBt();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onResume()
    {
        List<BtDevice> list = btDeviceDAO.queryForAll();
        if (list == null || list.size() <= 0)
        {
            isCarConfig = false;
        }
        else
        {
            isCarConfig = true;
        }
        
        startBt();
        super.onResume();
    }
   
    @Override
    public void deviceFound(BtDevice device)
    {
        List<BtDevice> list = btDeviceDAO.queryForEq("macAddress", device.getMacAddress());
        
        if (list != null && list.size() > 0)
        {
            btDeviceFound = true;
            
            BtDevice btDevice = list.get(0);
            changeBtStatusTextView(BtStatus.FOUND, btDevice);
            
        }
    }
    
    @Override
    public void discoveryStarted()
    {
        changeBtStatusTextView(BtStatus.DISCOVERYING, null);
    }

    @Override
    public void discoveryFinished()
    {
        stopBt();
        changeBtStatusTextView(BtStatus.DISCOVERY_FINISH, null);
    }
    
    private void stopBt()
    {
        if (btService != null)
        {
            btService.stopDiscovery();
        }
    }
    
    private void startBt()
    {
        if (!isCarConfig)
        {
            changeBtStatusTextView(BtStatus.NOT_CONFIG, null);
            return;
        }
        
        if (btService == null)
        {
            btService = new BtService(this, this);
        }
        
        if (! btService.isEnable())
        {
            changeBtStatusTextView(BtStatus.NOT_ENABLED, null);
        }
        else
        {
            btService.startDiscovery();
            btDeviceFound = false;
        }        
    }
    

    
    public void changeBtStatusTextView(BtStatus status, BtDevice device)
    {
        btStatusTextView.setOnClickListener(null);
        
        switch (status)
        {
            case NOT_ENABLED:
                addOpenBtView();
                break;
            case FOUND:
                addBtFoundView(device);
                break;
            case DISCOVERYING:
                addBtDiscoveryView();
                break;
            case DISCOVERY_FINISH:
                addBtDiscoveryFinishView();
                break;
            case NOT_CONFIG:
                addNotConfigView();
                break;
            default:
                break;
        }
        
    }
    
    private void addNotConfigView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {               
                btStatusTextView.setText("Click to config");
                btStatusTextView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
                    }
                });
            }
        });
        
    }

    private void addBtDiscoveryFinishView()
    {
        if (! btDeviceFound)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    btStatusTextView.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            startBt();
                        }
                    });
                    
                    btStatusTextView.setText("No. Click to retry");
                }
            });
        }
    }
    
    private void addBtDiscoveryView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                btStatusTextView.setText("Recognizing...");
            }
        });
    }
    

    private void addOpenBtView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                btStatusTextView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                });
                
                btStatusTextView.setText("Click to open BT");
            }
        });
    }
    
    private void addBtFoundView(BtDevice btDevice)
    {
        final String deviceName = TextUtil.getBtDevicePreferedName(btDevice);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                btStatusTextView.setText(String.format("Yes (%s)", deviceName));
            }
        });
    }

    private enum BtStatus
    {
        NOT_ENABLED,
        FOUND,
        DISCOVERYING, 
        DISCOVERY_FINISH, NOT_CONFIG
    }

   
}
