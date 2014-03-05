package com.telenav.concur.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.telenav.concur.vo.Token;

/**
 * Service for request token
 * @author htli
 *
 */
public class TokenService extends ConcurService
{
    private static final String TOKEN_URL_TEMP = 
            "https://www.concursolutions.com/net2/oauth2/" +
            "GetAccessToken.ashx?code=%s&client_id=%s&client_secret=%s";

    private static final String LOG_TAG = "TokenService";
    
    private Token token;
    
    /**
     * Request authorization token from Concur service
     * @param code Get from login URL when click "Accpet" button on TOC page. 
     * @param clientId Application authorization key
     * @param consumerSecretKey Application authorization secret
     */
    public void requestToken(String code, String clientId, String consumerSecretKey)
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(String.format(TOKEN_URL_TEMP, code, clientId, consumerSecretKey));
        
        try
        {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            
            //Print XML
            String ret = "";
            String tmp = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            while ( (tmp = br.readLine()) != null)
            {
                ret += tmp;
            }
            Log.d(LOG_TAG, ret);
            
            //Parse XML
            token = new Token();
            parse(ret.getBytes(), new AuthResponseHandler());
            
            if (! token.isHasError())
            {
                TokenManager.saveToken(token);
            }
            
            Log.d("LOG_TAG", token.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
   
    /**
     * Authorization response SAX parser
     * 
     * @author htli
     *
     */
    private class AuthResponseHandler extends DefaultHandler
    {
    
        private String content;
        
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            content = new String(ch, start, length);
            super.characters(ch, start, length);
        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if (Token.ELEMENT_TOKEN.equalsIgnoreCase(localName))
            {
                token.setToken(content);
            }
            else if (Token.ELEMENT_EXPIRE_TIME.equalsIgnoreCase(localName))
            {
                token.setExpireTime(content);
            }
            else if (Token.ELEMENT_REFRESH_TOKEN.equalsIgnoreCase(localName))
            {
                token.setRefreshToken(content);
            }
            else if (Token.ELEMENT_ERROR.equalsIgnoreCase(localName))
            {
                token.setHasError(true);
            }
            else if (Token.ELEMENT_MESSAGE.equalsIgnoreCase(localName));
            {
                token.setErrorMsg(content);
            }
            super.endElement(uri, localName, qName);
        }
    }
    
    public Token getToken()
    {
        return token;
    }

    
}
