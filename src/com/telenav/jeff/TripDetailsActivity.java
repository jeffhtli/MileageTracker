package com.telenav.jeff;

import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.Trip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TripDetailsActivity extends Activity
{
    private TextView dateTextView;
    private TextView startLocationTextView;
    private TextView endLocationTextView;
    private TextView mileageTextView;
    private TextView categoryTextView;
    private TextView tollTextView;
    private TextView parkingTextView;
    private TextView constTextView;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        
        setTitle("Mileage Details");
        
        setContentView(R.layout.activity_trip_details);
        
        dateTextView = (TextView)findViewById(R.id.detail_date_textview);
        startLocationTextView = (TextView)findViewById(R.id.detail_start_location_textview);
        endLocationTextView = (TextView)findViewById(R.id.detail_end_location_textview);
        mileageTextView = (TextView)findViewById(R.id.detail_mileage_textview);
        categoryTextView = (TextView)findViewById(R.id.detail_category_textview);
        tollTextView = (TextView)findViewById(R.id.detial_toll_textview);
        parkingTextView = (TextView)findViewById(R.id.detail_parking_textview);
        constTextView = (TextView)findViewById(R.id.detail_cost_textview);
        
        int tripIndex = getIntent().getIntExtra("tripIndex", -1);
        Trip trip = TripModel.tripList.get(tripIndex);
        
        dateTextView.setText(TextUtil.formatDate(trip.getStartTimeStamp()));
        startLocationTextView.setText(trip.getStartAddress());
        endLocationTextView.setText(trip.getEndAddress());
        mileageTextView.setText(TextUtil.convert2Mile(trip.getDistance(), true));
        categoryTextView.setText(trip.getCategroy().getName());
        constTextView.setText(TextUtil.getFormattedCost(trip.getDistance()));
    }

}
