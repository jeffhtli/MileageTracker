package com.telenav.jeff;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.concur.ConcurLoginActivity;
import com.telenav.concur.service.ExpenseService;
import com.telenav.concur.service.TokenManager;
import com.telenav.concur.service.TokenService;
import com.telenav.concur.vo.ReportSummary;
import com.telenav.jeff.service.avenger.AddressService;
import com.telenav.jeff.service.avenger.AddressService.LatLon;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.util.TextUtil;
import com.telenav.jeff.util.TvMath;
import com.telenav.jeff.vo.mileage.CalendarAddress;
import com.telenav.jeff.vo.mileage.ContactsAddress;
import com.telenav.jeff.vo.mileage.GPSData;
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
    private TextView costTextView;
    private Button saveToConcurBtn;
    
    private List<ReportSummary> reportList;
    private ProgressBar fromProgress;
    private ProgressBar toProgress;
    private TextView fromContacts;
    private TextView toContacts;
    private Trip currentTrip;
    private LinearLayout fromLayout;
    private LinearLayout toLayout;
    private ProgressBar calendarFromProgress;
    private ProgressBar calendarToProgress;
    private TextView calendarFromTextview;
    private TextView calendarToTextview;
    private LinearLayout calendarFromLayout;
    private LinearLayout calendarToLayout;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        
        setTitle("Mileage Details");
        
        setContentView(R.layout.activity_trip_details);
        
        initView();
        
        initService();
    }

    private void initView()
    {
        dateTextView = (TextView)findViewById(R.id.detail_date_textview);
        startLocationTextView = (TextView)findViewById(R.id.detail_start_location_textview);
        endLocationTextView = (TextView)findViewById(R.id.detail_end_location_textview);
        mileageTextView = (TextView)findViewById(R.id.detail_mileage_textview);
        categoryTextView = (TextView)findViewById(R.id.detail_category_textview);
        tollTextView = (TextView)findViewById(R.id.detial_toll_textview);
        parkingTextView = (TextView)findViewById(R.id.detail_parking_textview);
        costTextView = (TextView)findViewById(R.id.detail_cost_textview);
        
        //contact
        fromProgress = (ProgressBar)findViewById(R.id.trip_details_fromlocation_progress);
        fromProgress.setVisibility(View.GONE);
        toProgress = (ProgressBar)findViewById(R.id.trip_details_tolocation_progress);
        toProgress.setVisibility(View.GONE);
        fromContacts = (TextView)findViewById(R.id.trip_details_fromlocation_contacts_textview);
        toContacts = (TextView)findViewById(R.id.trip_details_tolocation_contacts_textview);
        fromLayout = (LinearLayout)findViewById(R.id.trip_details_from_contact_layout);
        fromLayout.setVisibility(View.GONE);
        toLayout = (LinearLayout)findViewById(R.id.trip_details_to_contact_layout);
        toLayout.setVisibility(View.GONE);

        //calendar
        calendarFromProgress = (ProgressBar)findViewById(R.id.trip_details_fromlocation_calendar_progress);
        calendarFromProgress.setVisibility(View.GONE);
        calendarToProgress = (ProgressBar)findViewById(R.id.trip_details_tolocation_calendar_progress);
        calendarToProgress.setVisibility(View.GONE);
        calendarFromTextview = (TextView)findViewById(R.id.trip_details_fromlocation_calendar_textview);
        calendarToTextview = (TextView)findViewById(R.id.trip_details_tolocation_calendar_textview);
        calendarFromLayout = (LinearLayout)findViewById(R.id.trip_details_from_calendar_layout);
        calendarFromLayout.setVisibility(View.GONE);
        calendarToLayout = (LinearLayout)findViewById(R.id.trip_details_tolocation_calendar_layout);
        calendarToLayout.setVisibility(View.GONE);
        
        
        int tripIndex = TripModel.currentTripIndex;
        currentTrip = TripModel.tripList.get(tripIndex);
        
        dateTextView.setText(TextUtil.formatDate(currentTrip.getStartTimeStamp()));
        startLocationTextView.setText(currentTrip.getStartAddress());
        endLocationTextView.setText(currentTrip.getEndAddress());
        mileageTextView.setText(TextUtil.convert2Mile(currentTrip.getDistance(), true));
        categoryTextView.setText(currentTrip.getCategroy().getName());
        costTextView.setText(TextUtil.getFormattedCost(currentTrip.getDistance()));

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
    
    
    private void initService()
    {
        new ContactsMatchService().execute("");
       // new CalendarMatchingService().execute("");
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
        if (data != null && data.hasExtra(ConcurLoginActivity.LOGIN_RESULT_KEY))
        {
            boolean isLoginSuccessful = data.getBooleanExtra(ConcurLoginActivity.LOGIN_RESULT_KEY, false);
            if (isLoginSuccessful)
            {
                getReportList();
            }
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

    private class ContactsMatchService extends SimpleAsyncTask
    {
        private ContactsAddress startMatchedContacts;
        private ContactsAddress endMatchedContacts;

        public ContactsMatchService()
        {
            super(null, null);
        }
        
        @Override
        protected void onPreExecute()
        {
            fromProgress.setVisibility(View.VISIBLE);
            toProgress.setVisibility(View.VISIBLE);
            
            fromLayout.setVisibility(View.VISIBLE);
            toLayout.setVisibility(View.VISIBLE);
            
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            GPSData startLocation = currentTrip.getStartLocation();
            GPSData endLocation = currentTrip.getEndLocation();
            
            RuntimeExceptionDao<ContactsAddress, Integer> contactsDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(ContactsAddress.class);
            List<ContactsAddress> list = contactsDAO.queryForAll();
            
            if (list != null)
            {
                for (ContactsAddress contacts : list)
                {
                    if (contacts.getLat() == 0 || contacts.getLon() == 0)
                    {
                        LatLon latlon = AddressService.getInstance().getAddressLonlat(contacts.getAddress());
                        contacts.setLat(latlon.lat);
                        contacts.setLon(latlon.lon);
                        contactsDAO.update(contacts);
                    }
                    
                    if (startMatchedContacts == null)
                    {
                        long delta = TvMath.calcDist(startLocation.getLat(), startLocation.getLon(), contacts.getLat(), contacts.getLon());
                        if (delta < 500)
                        {
                            startMatchedContacts = contacts;
                        }
                    }
                    
                    if (endMatchedContacts == null)
                    {
                        long delta = TvMath.calcDist(endLocation.getLat(), endLocation.getLon(), contacts.getLat(), contacts.getLon());
                        if (delta < 500)
                        {
                            endMatchedContacts = contacts;
                        }
                    }
                }
            }
            
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            fromProgress.setVisibility(View.GONE);
            toProgress.setVisibility(View.GONE);
            
            if (startMatchedContacts != null)
            {
                fromContacts.setText(startMatchedContacts.getName());
            }
            else
            {
                fromLayout.setVisibility(View.GONE);
            }
            
            if (endMatchedContacts != null)
            {
                toContacts.setText(endMatchedContacts.getName());
            }
            else
            {
                toLayout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
        
    }
    
    private class CalendarMatchingService extends SimpleAsyncTask
    {

        private CalendarAddress startMatchedContacts;
        private CalendarAddress endMatchedContacts;

        public CalendarMatchingService()
        {
            super(null, null);
        }

        @Override
        protected Void doInBackground(String... params)
        {

            GPSData startLocation = currentTrip.getStartLocation();
            GPSData endLocation = currentTrip.getEndLocation();
            
            RuntimeExceptionDao<CalendarAddress, Integer> calendarDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(CalendarAddress.class);
            List<CalendarAddress> list = calendarDAO.queryForAll();
            
            if (list != null)
            {
                for (CalendarAddress calendarAddress : list)
                {
                    if (calendarAddress.getLat() == 0 || calendarAddress.getLon() == 0)
                    {
                        LatLon latlon = AddressService.getInstance().getAddressLonlat(calendarAddress.getAddress());
                        calendarAddress.setLat(latlon.lat);
                        calendarAddress.setLon(latlon.lon);
                        calendarDAO.update(calendarAddress);
                    }
                    
                    if (startMatchedContacts == null)
                    {
                        long delta = TvMath.calcDist(startLocation.getLat(), startLocation.getLon(), calendarAddress.getLat(), calendarAddress.getLon());
                        if (delta < 500)
                        {
                            startMatchedContacts = calendarAddress;
                        }
                    }
                    
                    if (endMatchedContacts == null)
                    {
                        long delta = TvMath.calcDist(endLocation.getLat(), endLocation.getLon(), calendarAddress.getLat(), calendarAddress.getLon());
                        if (delta < 500)
                        {
                            endMatchedContacts = calendarAddress;
                        }
                    }
                }
            }
            
            
            return null;
        }
        
        @Override
        protected void onPreExecute()
        {
            calendarFromProgress.setVisibility(View.VISIBLE);
            calendarToProgress.setVisibility(View.VISIBLE);
            
            calendarFromLayout.setVisibility(View.VISIBLE);
            calendarToLayout.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            calendarFromProgress.setVisibility(View.GONE);
            calendarToProgress.setVisibility(View.GONE);
            
            if (startMatchedContacts != null)
            {
                calendarFromTextview.setText(startMatchedContacts.getName());
            }
            else
            {
                calendarFromLayout.setVisibility(View.GONE);
            }
            
            if (endMatchedContacts != null)
            {
                calendarToTextview.setText(endMatchedContacts.getName());
            }
            else
            {
                calendarToLayout.setVisibility(View.GONE);
            }
            
            super.onPostExecute(result);
        }
        
    }
}
