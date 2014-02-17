package com.telenav.jeff.service.concur;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.telenav.jeff.vo.concur.Token;

public class TokenManager
{
    private static Context context;
    
    private static final String PREFERENCE_TOKEN = "token";
    
    private static Token token;
    
    public static void init(Context context)
    {
        TokenManager.context = context;
        loadToken();
    }
    
    private static void loadToken()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_TOKEN, Context.MODE_PRIVATE);
        String tokenstring = sharedPref.getString(Token.ELEMENT_TOKEN, null);
        Log.d("TokenManager", "token: " + tokenstring);
        token = new Token();
        if (tokenstring != null)
        {
            String expiredTime = sharedPref.getString(Token.ELEMENT_EXPIRE_TIME, null);
            String refreshToken = sharedPref.getString(Token.ELEMENT_REFRESH_TOKEN, null);
            
            
            token.setToken(tokenstring);
            token.setExpireTime(expiredTime);
            token.setRefreshToken(refreshToken);
        }
        else
        {
            token.setHasError(true);
        }
    }
    
    public static boolean hasVaildToken()
    {
        boolean ret = false;
        
        if ( !token.isHasError())
        {
            try
            {
                String tokenstring = token.getExpireTime();
                String[] array = tokenstring.split("\\s");
                String[] dateArray = array[0].split("/");
                String[] timeArray = array[1].split(":");
                String amPmStr = array[2];
                int hour = Integer.parseInt(timeArray[0]);
                
                SimpleTimeZone gmtZone = new SimpleTimeZone(0, TimeZone.getAvailableIDs(0)[0]);
                Calendar expired = new GregorianCalendar(gmtZone);
                expired.set(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]),
                    "am".equalsIgnoreCase(amPmStr) ? hour : hour + 12, Integer.parseInt(timeArray[1]), Integer.parseInt(timeArray[2]));
                
                Calendar now = Calendar.getInstance(gmtZone);
                ret = expired.compareTo(now) >= 0;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return ret;
    }
    
    public static void saveToken(Token token)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_TOKEN, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(Token.ELEMENT_TOKEN, token.getToken());
        editor.putString(Token.ELEMENT_EXPIRE_TIME, token.getExpireTime());
        editor.putString(Token.ELEMENT_REFRESH_TOKEN, token.getRefreshToken());
        editor.commit();
        loadToken();
    }
    
    public static Token getToken()
    {
        return token;
    }
}
