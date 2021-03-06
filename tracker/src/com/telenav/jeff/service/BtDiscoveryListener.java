package com.telenav.jeff.service;

import com.telenav.jeff.vo.mileage.BtDevice;

public interface BtDiscoveryListener
{
    void deviceFound(BtDevice device);
    void discoveryStarted();
    void discoveryFinished();
}
