package com.telenav.jeff.location;

import com.telenav.jeff.vo.mileage.GPSData;

public interface ILocationProvider
{
    void requestLocationUpdate(String provider, long interval, ILocationListener listener);
    
    void stopLocationUpdate();
}
