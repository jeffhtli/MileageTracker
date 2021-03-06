package com.telenav.jeff;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.service.BtDiscoveryListener;
import com.telenav.jeff.service.BtService;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.mileage.BtDevice;

public class BluetoothActivity extends Activity implements BtDiscoveryListener
{
    
    private static final int REQUEST_ENABLE_BT = 1;
    
    private static final String LOG_TAG = "BluetoothActivity";
    
    private BtService btService;
    private ListView savedDeviceList;
    private ListView scanedDeviceList;
    private Button scanBtn;
    private BluetoothListAdapter savedDeviceListAdapter;
    private BluetoothListAdapter scanedDeviceListAdapter;
    
    private LinearLayout searchLayout;
    
    //private BtDevice selectedBtDevice;
    private RuntimeExceptionDao<BtDevice, Integer> btDeviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_bluetooth);
        
        savedDeviceList = (ListView)findViewById(R.id.bt_saved_device_listview);
        savedDeviceListAdapter = new BluetoothListAdapter(this, new ArrayList<BtDevice>());
        savedDeviceList.setAdapter(savedDeviceListAdapter);
        savedDeviceList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                BtDevice device = savedDeviceListAdapter.getItem(arg2);
                showInputNameDialog(device, true);
            }
        });
        
        scanedDeviceList = (ListView)findViewById(R.id.bt_scaned_device_listview);
        scanedDeviceListAdapter = new BluetoothListAdapter(this, new ArrayList<BtDevice>());
        scanedDeviceList.setAdapter(scanedDeviceListAdapter);
        scanedDeviceList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                BtDevice device = scanedDeviceListAdapter.getItem(arg2);
                showInputNameDialog(device, false);
            }
        });
        
        searchLayout = (LinearLayout)findViewById(R.id.bt_searching_layout);
        searchLayout.setVisibility(View.GONE);
        
        scanBtn = (Button)findViewById(R.id.bt_scan_btn);
        scanBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                scanDevice();
            }
        });
        
        btDeviceDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(BtDevice.class);
        loadDevice();
    }
    
    private void showInputNameDialog(final BtDevice device, boolean needDelBtn)
    {
        final EditText view = new EditText(this);
        ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParam);
        view.setText(TextUtil.getBtDevicePreferedName(device));
        Builder dialogBuilder = new AlertDialog.Builder(BluetoothActivity.this)
            .setTitle("Input Name")
            .setView(view)
            .setNegativeButton("Save", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    device.setDefineName(view.getText().toString());
                    saveDevice(device);
                }
            });
        
        if (needDelBtn)
        {
            dialogBuilder.setPositiveButton("Delete",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                   removeDevice(device);
                }
            });
        }
        
        dialogBuilder.show();
            
    }
    
    private void removeDevice(BtDevice device)
    {
        int ret = btDeviceDAO.delete(device);
        if (ret >= 0)
        {
            savedDeviceListAdapter.remove(device); 
        }
    }

    private void saveDevice(BtDevice btDevice)
    {
        BtDevice device = btDeviceDAO.queryForSameId(btDevice);
        if (device != null)
        {
            btDeviceDAO.update(btDevice);
            savedDeviceListAdapter.notifyDataSetChanged();
        }
        else
        {
            btDeviceDAO.create(btDevice);
            savedDeviceListAdapter.insert(btDevice, 0);
        }
        
    }
    
    private void loadDevice()
    {
        try
        {
            List<BtDevice> list = btDeviceDAO.queryForAll();
            
            for (BtDevice device : list)
            {
                savedDeviceListAdapter.add(device);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void scanDevice()
    {

        if (btService == null)
        {
            btService = new BtService(this, this);
        }
        
        if ( !btService.isEnable())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            btService.startDiscovery();
            scanedDeviceListAdapter.clear();
            searchLayout.setVisibility(View.VISIBLE);
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                scanDevice();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

   
    @Override
    protected void onDestroy()
    {
        if (btService != null)
        {
            btService.stopDiscovery();
        }
        super.onDestroy();
    }

    
    private class BluetoothListAdapter extends ArrayAdapter<BtDevice>
    {
        public BluetoothListAdapter(Context context, List<BtDevice> dataList )
        {
            super(context, -1, dataList);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            BtDevice item = getItem(position);
            
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.bt_list_item, null);
            TextView textView = (TextView)view.findViewById(R.id.bt_list_item_textview);
            if (item.getDefineName() != null)
            {
                textView.setText(item.getDefineName());
            }
            else
            {
                textView.setText(String.format("%s (%s)", item.getName(), item.getMacAddress()));
            }
            
            return view;
        }
    }

    @Override
    public void deviceFound(BtDevice device)
    {
        scanedDeviceListAdapter.add(device);
    }

    @Override
    public void discoveryStarted()
    {
        searchLayout.setVisibility(View.VISIBLE);
        
    }

    @Override
    public void discoveryFinished()
    {
        searchLayout.setVisibility(View.GONE);
        
    }
}
