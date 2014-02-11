package com.telenav.jeff.service.concur;

import android.util.Log;

import com.telenav.jeff.vo.concur.User;

public class UserService extends ConcurService
{
    private static final String URI_USER_GET = "https://www.concursolutions.com/api/user/v1.0/User";   
    private static final String LOG_TAG = "UserService";
    
    public User getUser()
    {
        byte[] data = get(URI_USER_GET);
        
        if (data != null)
        {
            Log.d(LOG_TAG, new String(data));
        }
        return null;
    }

}
