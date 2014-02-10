package com.telenav.jeff.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "gps_data")
public class GPSData
{
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private long timeStamp;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lon;

    @DatabaseField
    private double speed;

    @DatabaseField
    private double heading;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public double getSpeed()
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public double getHeading()
    {
        return heading;
    }

    public void setHeading(double heading)
    {
        this.heading = heading;
    }

    
    

}
