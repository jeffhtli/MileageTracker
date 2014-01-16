package com.telenav.jeff.location;

public interface ILocationProvider
{
    void requestLocationUpdate(String provider, long interval, ILocationListener listener);
    
    void stopLocationUpdate();
    
    boolean isStarted();

}
