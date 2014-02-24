package com.telenav.jeff.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.telenav.jeff.service.avenger.AddressService;
import com.telenav.jeff.service.avenger.AddressService.LatLon;
import com.telenav.jeff.sqlite.DatabaseHelper;
import com.telenav.jeff.vo.mileage.ContactsAddress;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsService
{
    private static ContactsService instance;
    private Context context;

    private ContactsService()
    {
    }

    public static ContactsService getInstance()
    {
        if (instance == null)
        {
            instance = new ContactsService();
        }

        return instance;
    }
    
    public void init(Context context)
    {
        this.context = context;
    }
    
    public void syncContacts()
    {
        final RuntimeExceptionDao<ContactsAddress, Long> contactsAddrDAO = DatabaseHelper.getInstance().getRuntimeExceptionDao(ContactsAddress.class);
        List<ContactsAddress> contactsList = queryAddress();
        for (ContactsAddress contacts : contactsList)
        {
            ContactsAddress queryContact = contactsAddrDAO.queryForId(contacts.getId());
            if (queryContact == null )
            {
                contactsAddrDAO.create(contacts);
            }
            else
            {
                Map<String, String> contactMap = new HashMap<String, String>();
                if (! contacts.getName().equals(queryContact.getName()))
                {
                    contactMap.put("name", contacts.getName());
                }
                
                if (! contacts.getAddress().equals(queryContact.getAddress()))
                {
                    contactMap.put("address", contacts.getAddress());
                }
                
                if (!contactMap.isEmpty())
                {
                    try
                    {
                        UpdateBuilder<ContactsAddress, Long> builder = contactsAddrDAO.updateBuilder();
                        builder.where().eq("id", queryContact.getId());
                        for (String key : contactMap.keySet())
                        {
                            builder.updateColumnValue(key, contactMap.get(key));
                        }
                        builder.update();
                    }
                    catch (SQLException e)
                    {
                    }
                }
            }
        }
    }
    
    private List<ContactsAddress> queryAddress()
    {
        List<ContactsAddress> list = new ArrayList<ContactsAddress>();
        
        ContentResolver cr = context.getContentResolver();
        String[] projection = {
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.REGION};
        
        String[] queryParameter = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
        
        StringBuilder selection = new StringBuilder();
        selection.append(ContactsContract.Data.MIMETYPE + " = ?");
        
        Cursor cursor = cr.query(
            ContactsContract.Data.CONTENT_URI, 
            projection,
            selection.toString(),
            queryParameter,null);
        
        while(cursor.moveToNext())
        {
            ContactsAddress address = new ContactsAddress();
            address.setId(cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID)));
            address.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
            
            String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)); 
            String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            String state = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            StringBuilder builder = new StringBuilder();
            if (street != null && street.length() > 0)
            {
                builder.append(street);
            }
            
            if (city!=null && city.length() > 0)
            {
                builder.append("," + city);
            }
            
            if (state != null && state.length() > 0)
            {
                builder.append("," + state);
            }
            address.setAddress(builder.toString());
            
            list.add(address);
        }
        cursor.close();
        

        
        return list;
    }
    
}
