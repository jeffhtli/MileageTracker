package com.telenav.jeff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.trip.TripGenerator;
import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.mileage.Trip;

public class TripListActivity extends Activity
{
    
    private ListView listView;
    public Vector<Map<String, String>> dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip);
        
        this.setTitle("Mileage List");
        
        listView = (ListView)findViewById(R.id.tripList);
        
        new DirectionsTask().execute(new Void[]{});
    }
    
    
    private class DirectionsTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(TripListActivity.this, "", "Waiting...");
            super.onPreExecute();
        }
        
        @Override
        protected Void doInBackground(Void... params)
        {           
            TripGenerator generator = new TripGenerator();
            int count = generator.saveTripFromMileage();
            
            if (count > 0 || TripModel.tripList == null)
            {
                RuntimeExceptionDao<Trip, Integer> tripDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Trip.class);
                TripModel.tripList = tripDAO.queryForAll();
            }
            
            Map<String, String> map = null;
            dataSource = new Vector<Map<String,String>>();
            for (Trip trip : TripModel.tripList)
            {
                map = new HashMap<String, String>();
                map.put("start", trip.getStartAddress());
                map.put("end", trip.getEndAddress());
                map.put("id", String.valueOf(trip.getId()));
                map.put("date", TextUtil.formatDate(trip.getStartTimeStamp()));
                map.put("distance", TextUtil.convert2Mile(trip.getDistance(), true));
                dataSource.add(map);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            
            listView.setAdapter(new SimpleAdapter(TripListActivity.this, 
                dataSource, 
                R.layout.trip_list_item,
                new String[]{"date", "distance", "start", "end"},
                new int[]{
                    R.id.listitem_date_textview,
                    R.id.listitem_distance_textview,
                    R.id.listitem_start_location_textview,
                    R.id.listitem_end_location_textview}));
            
            listView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intent = new Intent(TripListActivity.this, TripDetailsActivity.class);
                    intent.putExtra("tripIndex", position);
                    startActivity(intent);
                }
            });
            
            if (progress != null)
            {
                progress.dismiss();
            }
        }
        
    }
    
    
}
