package com.telenav.jeff.vo.mileage;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "bt_device")
public class BtDevice
{
   
    @DatabaseField
    private String defineName;
    
    @DatabaseField
    private String name;
    
    @DatabaseField(id = true)
    private String macAddress;
  
    public String getDefineName()
    {
        return defineName;
    }

    public void setDefineName(String defineName)
    {
        this.defineName = defineName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }
}
