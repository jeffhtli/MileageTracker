package com.telenav.jeff.location;

import com.telenav.jeff.vo.mileage.GPSData;

public interface ILocationListener
{
    void locationUpdate(GPSData data);
}
