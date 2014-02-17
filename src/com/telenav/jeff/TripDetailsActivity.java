package com.telenav.jeff;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.telenav.jeff.service.concur.ExpenseService;
import com.telenav.jeff.service.concur.TokenManager;
import com.telenav.jeff.service.concur.TokenService;
import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.vo.concur.ReportSummary;
import com.telenav.jeff.vo.mileage.Trip;

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
    private Button saveToConcurBtn;
    
    private List<ReportSummary> reportList;

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
        
        int tripIndex = TripModel.currentTripIndex;
        Trip trip = TripModel.tripList.get(tripIndex);
        
        dateTextView.setText(TextUtil.formatDate(trip.getStartTimeStamp()));
        startLocationTextView.setText(trip.getStartAddress());
        endLocationTextView.setText(trip.getEndAddress());
        mileageTextView.setText(TextUtil.convert2Mile(trip.getDistance(), true));
        categoryTextView.setText(trip.getCategroy().getName());
        constTextView.setText(TextUtil.getFormattedCost(trip.getDistance()));

        saveToConcurBtn = (Button) findViewById(R.id.detail_save_to_concur_btn);
        saveToConcurBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!TokenManager.hasVaildToken())
                {
                    login();
                }
                else
                {
                    getReportList();
                }
            }
        });
    }
    
    private void getReportList()
    {
        new ReportListTask(TripDetailsActivity.this, "Getting report").execute("");
    }
    
    private void login()
    {
        Intent intent = new Intent(TripDetailsActivity.this, ConcurLoginActivity.class);
        startActivityForResult(intent, 1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data != null && data.hasExtra("code"))
        {
            String code = data.getStringExtra("code");
            new AuthenticationTask(this, "Authenticating").execute(code);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ReportListTask extends SimpleAsyncTask
    {
        public ReportListTask(Context context, String msg)
        {
            super(context, msg);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            reportList = new ExpenseService().getReportList();
            
            if (reportList == null)
            {
                reportList = new Vector<ReportSummary>();
            }
            
            ReportSummary summary = new ReportSummary();
            summary.setReportName("Create new report...");
            reportList.add(summary);
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            new AlertDialog.Builder(context).setItems(getReportNameList(), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (which == reportList.size() - 1)
                    {
                        startActivity(new Intent(context, ConcurCreateReportActivity.class));
                    }
                    else
                    {
                        ReportSummary summary = reportList.get(which);
                        String id = summary.getReportID();
                        String name = summary.getReportName();
                        
                        Intent intent = new Intent(context, ConcurCreateMileageReport.class);
                        intent.putExtra("reportName", name);
                        intent.putExtra("reportId", id);
                        startActivity(intent);
                    }
                    
                }}).setTitle("Select report:").show();
            
            super.onPostExecute(result);
        }
        
        private String[] getReportNameList()
        {
            String[] array = new String[reportList.size()];
            
            for(int i = 0; i< array.length; i++)
            {
                array[i] = reportList.get(i).getReportName();
            }
            
            return array;
        }
        
    }

    private class AuthenticationTask extends SimpleAsyncTask
    {
        public AuthenticationTask(Context context, String msg)
        {
            super(context, msg);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            new TokenService().requestToken(params[0]);
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            getReportList();
            super.onPostExecute(result);
        }
    }
}
