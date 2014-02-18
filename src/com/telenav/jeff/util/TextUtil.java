package com.telenav.jeff.util;

import java.text.DecimalFormat;

import com.telenav.jeff.vo.mileage.BtDevice;

import android.text.format.DateFormat;

public class TextUtil
{
    public static String convert2Mile(long meters, boolean withUnit)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        String mile = df.format(meters * 0.0006213712);
        return withUnit ? mile + " Mi" : mile;
    }
    
    public static String conver2Km(long meters, boolean withUnit)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        String km = df.format(meters * 0.001);

        return withUnit ? km + " Km" : km;
    }
    
    public static String formatDate(long timeMillis)
    {
        return DateFormat.format("MM/dd yyyy", timeMillis).toString();
    }
    
    public static String getFormattedCost(long meters)
    {
        DecimalFormat df = new DecimalFormat("0.00$");
        double d = meters * 0.0006213712 * 0.555;
        return df.format(d);
    }
    
    public static String getBtDevicePreferedName(BtDevice device)
    {
        String deviceName = device.getDefineName();
        if (deviceName == null || "".equals(deviceName))
        {
            deviceName = device.getName();
        }
        
        return deviceName;
    }
}
