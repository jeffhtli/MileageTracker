package com.telenav.jeff.service.avenger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public abstract class AvengerService
{
    protected static String getJson(String url)
    {
        HttpClient client = new DefaultHttpClient();
        BufferedReader br = null;
        try
        {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                br = new BufferedReader(new InputStreamReader(entity.getContent()));
                String tmp = "";
                StringBuffer sb = new StringBuffer();
                while ((tmp = br.readLine()) != null)
                {
                    sb.append(tmp);
                }

                String json = sb.toString();
                Log.d("AvengerService", json);

                return json;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

}
