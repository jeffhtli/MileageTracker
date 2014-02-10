package com.telenav.jeff.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "extra_cost")
public class ExtraCost
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private ExtraCostType type;
    
    @DatabaseField(foreign = true)
    private Trip trip;

    @DatabaseField
    private float cost;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public ExtraCostType getType()
    {
        return type;
    }

    public void setType(ExtraCostType type)
    {
        this.type = type;
    }

    public float getCost()
    {
        return cost;
    }

    public void setCost(float cost)
    {
        this.cost = cost;
    }

    public Trip getTrip()
    {
        return trip;
    }

    public void setTrip(Trip trip)
    {
        this.trip = trip;
    }

}
