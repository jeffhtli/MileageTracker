package com.telenav.jeff.service.avenger;

import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class MapService extends AvengerService
{
    private static final String DIRECTION_URL = "http://avengersdev.telenav.com/maps/v1/directions/json?origin=%s,%s&destination=%s,%s&max_route_number=1";
    private static final String RGC_URL = "http://avengersdev.telenav.com/entity/v1/search/json?query=%s&offset=0&limit=10&geo_source=Tomtom&entity_source=Telenav";
    private static final String RGC_PARAM_QUERY = "{!V1}{rgc_search_query:{location:{lat:%s,lon:%s}}}";
    private static final String LOG_TAG = "MapService";
    
    
    public static class Direction
    {
        public long distance;
        public List<String> roadName = new Vector<String>();
        public List<Long> roadLength = new Vector<Long>();
    }
    
    public static class Address
    {
        public String firstLine = "Unknown Street";
        public String sencondLine = "";
    }
    
    public static Direction getDirection(double originLat, double originLon, double destLat, double destLon)
    {
        String url = String.format(DIRECTION_URL,  originLat,  originLon,  destLat,  destLon);
        Log.d(LOG_TAG, url);
        try
        {
            return parseDirectionResult(getJson(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Direction parseDirectionResult(String resultString) throws Exception
    {
        JSONObject object = new JSONObject(resultString);
        JSONObject route = object.getJSONArray("route").getJSONObject(0);
        JSONObject routeInfo = route.getJSONObject("route_info");
        JSONArray segment = route.getJSONArray("segment");
        JSONArray segIndexArray = route.getJSONArray("path").getJSONObject(0).getJSONArray("segment_index");
        
        Direction direction = new Direction();
        direction.distance = routeInfo.getLong("travel_dist_in_meter");
        for (int i = 0; i<segIndexArray.length(); i++)
        {
            int segIndex = segIndexArray.getInt(i);
            JSONObject segObject = segment.getJSONObject(segIndex);
            JSONObject roadNameObject = segObject.getJSONObject("road_name");
            
            String roadName = "Unknown road";
            if (!roadNameObject.isNull("official_name"))
            {
                roadName = roadNameObject.getJSONArray("official_name").getString(0);
                
            }
            else if (!roadNameObject.isNull("route_number"))
            {
                roadName = roadNameObject.getJSONArray("route_number").getString(0);
            }
            
            direction.roadName.add(roadName);
            direction.roadLength.add(Math.round(segObject.getDouble("length_in_meter")));
        }
        
        return direction;
    }
    
    public static Address getAddress(double lat, double lon)
    {
        Address address = new Address();
        try
        {
            String url = String.format(RGC_URL, URLEncoder.encode(String.format(RGC_PARAM_QUERY, lat, lon), "UTF-8"));
            Log.d(LOG_TAG, url);
            address = parseAddressResult(getJson(url));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return address;
    }
    
    private static Address parseAddressResult(String json) throws Exception
    {
        JSONObject root = new JSONObject(json);
        JSONObject result = root.getJSONArray("result").getJSONObject(0);
        JSONObject addressObject = result.getJSONObject("entity").getJSONObject("display_address");
        
        Address address = new Address();
        address.firstLine = addressObject.getJSONObject("street").getString("formatted_name");
        address.sencondLine = String.format("%s, %s, %s", 
            addressObject.getString("city"),
            addressObject.getString("state"),
            addressObject.getString("postal_code"));
        
        Log.d(LOG_TAG, "RGC Result: " + address);
        return address;
    }
    
    
}
