package com.telenav.jeff.trip;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.telenav.jeff.avengerservice.MapService;
import com.telenav.jeff.avengerservice.MapService.Address;
import com.telenav.jeff.avengerservice.MapService.Direction;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.vo.GPSData;
import com.telenav.jeff.vo.Mileage;
import com.telenav.jeff.vo.Segment;
import com.telenav.jeff.vo.Trip;

public class TripGenerator
{
    private static final String LOG_TAG = "TripGenerator";
    
    private RuntimeExceptionDao<Trip, Integer> tripDAO;
    private RuntimeExceptionDao<Mileage, Integer> mileageDAO;
    private RuntimeExceptionDao<Segment, Integer> segmentDAO;
    
    public TripGenerator()
    {
        mileageDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Mileage.class);
        tripDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Trip.class);
        segmentDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(Segment.class);
    }
    
    public int saveTripFromMileage()
    {
        List<Mileage> list = mileageDAO.queryForAll();
        if (list == null || list.size() <= 0)
        {
            return 0;  
        }
        
        for(Mileage mileage : list)
        {
            GPSData start = mileage.getStartLocation();
            GPSData end = mileage.getEndLocation();
            
            Trip trip = new Trip();
            trip.setStartLocation(start);
            trip.setEndLocation(end);
            tripDAO.create(trip);
            
            Direction directions = MapService.getDirection(start.getLat(), start.getLon(), end.getLat(), end.getLon());
            if (directions == null)
            {
                tripDAO.delete(trip);
            }
            else
            {
                ForeignCollection<Segment> segments = tripDAO.getEmptyForeignCollection("segments");
                for (int i = 0; i<directions.roadName.size(); i++)
                {
                    Segment seg = new Segment();
                    seg.setName(directions.roadName.get(i));
                    seg.setDistance(directions.roadLength.get(i));
                    seg.setTrip(trip);
                    
                    segments.add(seg);
                }
                
                trip.setSegments(segments);
                
                Address startAddress = MapService.getAddress(start.getLat(), start.getLon());
                Address endAddress = MapService.getAddress(end.getLat(), end.getLon());
                
                trip.setStartAddress(startAddress.firstLine + "\n" + startAddress.sencondLine);
                trip.setEndAddress(endAddress.firstLine + "\n" + endAddress.sencondLine);
                
                tripDAO.update(trip);
                mileageDAO.delete(mileage);
            }
        }
        
        return list.size();
    }

}