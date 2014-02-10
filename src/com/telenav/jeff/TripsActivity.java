package com.telenav.jeff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.trip.TripGenerator;
import com.telenav.jeff.vo.GPSData;
import com.telenav.jeff.vo.Trip;

public class TripsActivity extends Activity
{
    
    private ListView listView;
    public Vector<Map<String, String>> dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip);
        
        listView = (ListView)findViewById(R.id.tripList);
        
        new DirectionsTask().execute(new Void[]{});
    }
    
    
    private class DirectionsTask extends AsyncTask<Void, Void, Void>
    {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(TripsActivity.this, "", "Waiting...");
            super.onPreExecute();
        }
        
        @Override
        protected Void doInBackground(Void... params)
        {           
            TripGenerator generator = new TripGenerator();
            generator.saveTripFromMileage();
            
            RuntimeExceptionDao<Trip, Integer> tripDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Trip.class);
            List<Trip> tripList = tripDAO.queryForAll();
            
            Map<String, String> map = null;
            dataSource = new Vector<Map<String,String>>();
            for (Trip trip : tripList)
            {
                map = new HashMap<String, String>();
                map.put("start", trip.getStartAddress());
                map.put("end", trip.getEndAddress());
                dataSource.add(map);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            
            listView.setAdapter(new SimpleAdapter(TripsActivity.this, 
                dataSource, 
                R.layout.trip_list_item,
                new String[]{"start", "end"},
                new int[]{R.id.start_location_textview,R.id.end_location_textview}));
            
            if (progress != null)
            {
                progress.hide();
            }
        }
        
    }

}
