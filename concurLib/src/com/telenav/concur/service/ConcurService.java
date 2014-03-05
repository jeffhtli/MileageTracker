package com.telenav.concur.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public abstract class ConcurService
{
    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    private static final String HTTP_HEADER_ACCEPT = "Accept";
    private static final String LOG_TAG = "ConcurService";
    
    protected static HttpClient httpClient;
    
    static 
    {
        httpClient = new DefaultHttpClient();
    }
    
    protected byte[] sendRequest(HttpUriRequest request)
    {
        request.addHeader(HTTP_HEADER_AUTHORIZATION, "OAuth " + TokenManager.getToken().getToken());
        request.addHeader(HTTP_HEADER_ACCEPT, "application/xml");
        
        Log.d(LOG_TAG, request.getURI().toString());
        
        byte[] result = null;
        try
        {
            HttpResponse response = httpClient.execute(request);
            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            
            Log.d(LOG_TAG, String.format("HTTP Response code: %s", statusCode));
            
            if (statusCode == HttpStatus.SC_OK)
            {
               HttpEntity entity = response.getEntity();
               
               int length = (int)entity.getContentLength();
               Log.d(LOG_TAG, "Content Length: " + length);
               
               result = new byte[length];
               InputStream is = response.getEntity().getContent();
               BufferedInputStream bis = new BufferedInputStream(is);
               bis.read(result);
               bis.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
    
    protected byte[] get(String url)
    {
        HttpGet request = new HttpGet(url);
        return sendRequest(request);
    }
    
    protected byte[] post(String url, String xmlString)
    {
        byte[] data = null;
        
        HttpPost request = new HttpPost(url);
        StringEntity entity = null;
        try
        {
            entity = new StringEntity(xmlString);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (entity != null)
        {
            request.setEntity(entity);
            data = sendRequest(request);
        }

        return data;
    }
    
    protected void parse(byte[] data, DefaultHandler handler)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            InputStream is = new ByteArrayInputStream(data);
            reader.parse(new InputSource(is));
            is.close(); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
}
