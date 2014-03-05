package com.telenav.jeff.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.vo.mileage.CalendarAddress;

public class CalendarService
{
    
    private static final String LOG_TAG = "CalendarService";
    private static CalendarService instance;
    private Context context;

    private CalendarService()
    {

    }

    public static CalendarService getInstance()
    {
        if (instance == null)
        {
            instance = new CalendarService();
        }

        return instance;
    }
    
    public void init(Context context)
    {
        this.context = context;
    }
    
    public void syncCalendar()
    {
        RuntimeExceptionDao<CalendarAddress, Long> calendarDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(CalendarAddress.class);
        List<CalendarAddress> calendarList = queryCalendar();
        for (CalendarAddress calendar : calendarList)
        {
            CalendarAddress savedCalendar = calendarDAO.queryForId(calendar.getId());
            if (savedCalendar == null)
            {
                calendarDAO.create(calendar);
            }
            else
            {
                Map<String, String> calendarMap = new HashMap<String, String>();
                if (! savedCalendar.getName().equals(calendar.getName()))
                {
                    calendarMap.put("name", calendar.getName());
                }
                
                if (! savedCalendar.getAddress().equals(calendar.getAddress()))
                {
                    calendarMap.put("address", calendar.getAddress());
                }
                
                if (!calendarMap.isEmpty())
                {
                    try
                    {
                        UpdateBuilder<CalendarAddress, Long> builder = calendarDAO.updateBuilder();
                        builder.where().eq("id", savedCalendar.getId());
                        for (String key : calendarMap.keySet())
                        {
                            builder.updateColumnValue(key, calendarMap.get(key));
                        }
                        builder.update();
                    }
                    catch (SQLException e)
                    {
                    }
                }
            }
        }
        
    }
    
    private List<CalendarAddress> queryCalendar()
    {
        List<CalendarAddress> list = new ArrayList<CalendarAddress>();
        
        ContentResolver cr = context.getContentResolver();
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.TITLE};
 
        String selection = CalendarContract.Events.EVENT_LOCATION + " is not null";
        
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
        
        CalendarAddress calendarAddress = null;
        while (cursor.moveToNext())
        {
            calendarAddress = new CalendarAddress();
            calendarAddress.setId(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
            calendarAddress.setAddress(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
            calendarAddress.setName(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
            
            list.add(calendarAddress);
        }

        return list;
    }

}
