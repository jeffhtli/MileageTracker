package com.telenav.jeff;

import java.util.Calendar;

import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.mileage.Trip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class ConcurCreateMileageReport extends Activity
{
    
    private TextView reportNameTextField;
    private Button changeDateBtn;
    private TextView dateTextView;
    private TextView mileageTextView;
    private TextView fromTextView;
    private TextView projectTextView;
    private TextView purposeTextView;
    private Button submitBtn;
    private TextView toTextView;
    
    private String reportName;
    private String reportId;
    private TextView costTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_concur_mileage_report);
        
        reportName = getIntent().getStringExtra("reportName");
        reportId = getIntent().getStringExtra("reportId");
        
        reportNameTextField = (TextView)findViewById(R.id.mileage_report_name);
        changeDateBtn = (Button)findViewById(R.id.mileage_change_date_btn);
        dateTextView = (TextView)findViewById(R.id.mileage_date_textview);
        mileageTextView = (TextView)findViewById(R.id.mileage_distance_textview);
        fromTextView = (TextView)findViewById(R.id.mileage_from_textview);
        projectTextView = (TextView)findViewById(R.id.mileage_project_textview);
        purposeTextView = (TextView)findViewById(R.id.mileage_purpose_textview);
        submitBtn = (Button)findViewById(R.id.mileage_submit_btn);
        toTextView = (TextView)findViewById(R.id.mileage_to_textview);
        costTextView = (TextView)findViewById(R.id.mileage_cost_textview);
        
        initView();
    }

    private void initView()
    {
        dateTextView.setText(DateFormat.format("MM/dd/yyyy", System.currentTimeMillis()));
        reportNameTextField.setText(reportName);
        
        int tripIndex = TripModel.currentTripIndex;
        Trip trip = TripModel.tripList.get(tripIndex);
        
        fromTextView.setText(trip.getStartAddress());
        toTextView.setText(trip.getEndAddress());
        mileageTextView.setText(TextUtil.convert2Mile(trip.getDistance(), false));
        costTextView.setText(TextUtil.getFormattedCost(trip.getDistance()));
        //
//        changeDateBtn.setOnClickListener(new OnClickListener()
//        {
//            
//            @Override
//            public void onClick(View v)
//            {
//                int y = Calendar.getInstance().get(Calendar.YEAR);
//                int m = Calendar.getInstance().get(Calendar.MONTH);
//                int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//                
//                new DatePickerDialog(ConcurCreateMileageReport.this, 
//                    new OnDateSetListener()
//                    {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
//                        {
//                            
//                        }
//                    }, y, m, d).show();
//                
//            }
//        });
        
        
        
        
        
    }
    

}
