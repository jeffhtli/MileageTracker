package com.telenav.jeff.service.avenger;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AddressService extends AvengerService
{
    
    private final static String  ADDRESS_SEARCH_URL = "http://avengersdev.telenav.com/entity/v1/search/json?query=%s&geo_source=tomtom&entity_source=telenav";
    private static final String LOG_TAG = "AddressService";
    private static AddressService instance;

    private AddressService()
    {

    }

    public static AddressService getInstance()
    {
        if (instance == null)
        {
            instance = new AddressService();
        }

        return instance;
    }
    
    public class LatLon
    {
        public double lon;
        public double lat;

    }
    
    public LatLon getAddressLonlat(String address)
    {
        LatLon latlon = null;
        
        try
        {
            String url = String.format(ADDRESS_SEARCH_URL, URLEncoder.encode(address, "UTF-8"));
            Log.d("AddressService", url);
            latlon = parseJson(getJson(url));
            Log.d(LOG_TAG, String.format("lat: %s, lon: %s", latlon.lat, latlon.lon));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return latlon;
    }
    
    public LatLon parseJson(String json) throws JSONException
    {
        JSONObject rootObj = new JSONObject(json);
        JSONObject geoObj = rootObj.getJSONArray("result").getJSONObject(0).getJSONObject("entity").getJSONObject("rooftop_geocode");
        LatLon latlon = new LatLon();
        latlon.lat = geoObj.getDouble("lat");
        latlon.lon = geoObj.getDouble("lon");
        
        return latlon;
    }
 

}
