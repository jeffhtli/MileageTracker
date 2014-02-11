package com.telenav.jeff.location;

import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.telenav.jeff.vo.GPSData;

public class MviewLocationProvider implements ILocationProvider
{
    private ScheduledExecutorService scheduleService;
    private ILocationListener locationListener;
    
    private GPSData currentGPS;
    
    /** 1 meter = 9 DM6 */
    private static final int METER_DM6_FACTOR = 9;
    
    /** 50 m/s -> 450 DM6/s -> 112 mile/hour */
    private static final int MAX_SPEED = 50 * METER_DM6_FACTOR;
    
    /** 5 m/s -> 45 DM6/s -> 11 mile/hour */
    private static final int SPEED_ERROR = 5 * METER_DM6_FACTOR;
    
    public MviewLocationProvider()
    {
        
    }

    @Override
    public void requestLocationUpdate(String provider, long interval, ILocationListener listener)
    {
        scheduleService = Executors.newSingleThreadScheduledExecutor();
        scheduleService.scheduleAtFixedRate(new GPSDataReceiver(), 0, interval, TimeUnit.MILLISECONDS);
        locationListener = listener;
    }
    
    private class GPSDataReceiver extends Thread
    {

        @Override
        public void run()
        {
            if (locationListener != null)
            {
                currentGPS = parse(getMviewerData());
                if (currentGPS != null)
                {
                    locationListener.locationUpdate(currentGPS);
                }
            }
        }
        
        private GPSData parse(String gpsDataString)
        {
            GPSData gpsData = null;
            if (gpsDataString != null)
            {
                System.out.println(gpsDataString);
                String[] dataArray = gpsDataString.split(",");
                if (dataArray.length == 6)
                {
                    gpsData = new GPSData();
                    gpsData.setLat(Double.valueOf(dataArray[1]) / 100000);
                    gpsData.setLon(Double.valueOf(dataArray[2]) / 100000);
                    //gpsData.setSpeed(Double.valueOf(dataArray[3]) / METER_DM6_FACTOR);
                    //gpsData.heading = Double.valueOf(dataArray[4]);
                }
                
            }
            return gpsData;
        }
    }

    @Override
    public void stopLocationUpdate()
    {
        scheduleService.shutdown();
    }
    
    
    private String getMviewerData()
    {
        Socket socket = null;
        InputStream is = null;
        StringBuffer sb = new StringBuffer();
        try
        {
            socket = new Socket("10.66.40.35", 11159);
            socket.setSoTimeout(3000);
            is = socket.getInputStream();
            byte[] buffer = new byte[512];
            int count = is.read(buffer);
           
            while (count != -1)
            {
                sb.append(new String(buffer, 0, count));
                count = is.read(buffer);
            }
            
        }
        catch (Throwable ex)
        {
            //ex.printStackTrace();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    is = null;
                }
            }
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    socket = null;
                }
            }
        }
        
        return sb.toString();
    }

}
