package com.telenav.jeff.vo;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "trip")
public class Trip
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private GPSData startLocation;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private GPSData endLocation;
    
    @DatabaseField
    private String startAddress;
    
    @DatabaseField
    private String endAddress;

    @DatabaseField(foreign = true)
    private TripCategory categroy;
    
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Segment> segments;
    
    @ForeignCollectionField(eager = false, foreignFieldName="trip")
    private ForeignCollection<ExtraCost> extraCosts;

    
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

    public TripCategory getCategroy()
    {
        return categroy;
    }

    public void setCategroy(TripCategory categroy)
    {
        this.categroy = categroy;
    }

    public ForeignCollection<Segment> getSegments()
    {
        return segments;
    }

    public void setSegments(ForeignCollection<Segment> segments)
    {
        this.segments = segments;
    }

    public ForeignCollection<ExtraCost> getExtraCosts()
    {
        return extraCosts;
    }

    public void setExtraCosts(ForeignCollection<ExtraCost> extraCosts)
    {
        this.extraCosts = extraCosts;
    }

    public String getStartAddress()
    {
        return startAddress;
    }

    public void setStartAddress(String startAddress)
    {
        this.startAddress = startAddress;
    }

    public String getEndAddress()
    {
        return endAddress;
    }

    public void setEndAddress(String endAddress)
    {
        this.endAddress = endAddress;
    }
}
