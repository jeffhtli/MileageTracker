package com.telenav.jeff.bt;

import com.telenav.jeff.vo.mileage.BtDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BtService
{
    private Context context;
    private BluetoothAdapter btAdapter;
    private BtFoundBroadcast receiver;
    private boolean registed;
    
    public BtService(Context context, BtDiscoveryListener listener)
    {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BtFoundBroadcast(listener);
    }
    
    public void startDiscovery()
    {
        if (btAdapter.isDiscovering())
        {
            stopDiscovery();
        }
        
        if (! registed)
        {
            context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            context.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            context.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            registed = true;
        }
        
        btAdapter.startDiscovery();
    }
    
    public void stopDiscovery()
    {
        btAdapter.cancelDiscovery();
        if (registed)
        {
            context.unregisterReceiver(receiver);
            registed = false;
        }
    }
    
    public boolean isEnable()
    {
        return btAdapter.isEnabled();
    }
    
    
    private class BtFoundBroadcast extends BroadcastReceiver
    {
        private static final String LOG_TAG = "BluetoothFoundBroadcast";

        private BtDiscoveryListener listener;
        
        public BtFoundBroadcast(BtDiscoveryListener listener)
        {
            this.listener = listener;
        }
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.d(LOG_TAG, "onReceive: action = " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (listener != null)
                {
                    BtDevice btDevice = new BtDevice();
                    btDevice.setName(device.getName());
                    btDevice.setMacAddress(device.getAddress());
                    
                    Log.d(LOG_TAG, String.format("Found device: %s (%s)", btDevice.getName(), btDevice.getMacAddress()));
                    
                    listener.deviceFound(btDevice);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                listener.discoveryStarted();
                
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                listener.discoveryFinished();
            }
        }
        
        public void setBtDiscoveryListener(BtDiscoveryListener listener)
        {
            this.listener = listener;
        }
    }

}
