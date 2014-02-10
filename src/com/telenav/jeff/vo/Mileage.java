package com.telenav.jeff.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mileage")
public class Mileage
{
    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private GPSData startLocation;
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private GPSData endLocation;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public GPSData getStartLocation()
    {
        return startLocation;
    }

    public void setStartLocation(GPSData startLocation)
    {
        this.startLocation = startLocation;
    }

    public GPSData getEndLocation()
    {
        return endLocation;
    }

    public void setEndLocation(GPSData endLocation)
    {
        this.endLocation = endLocation;
    }
    
}
