package com.telenav.jeff.vo.concur;

public class Token
{
    public static final String ELEMENT_TOKEN = "Token";
    
    public static final String ELEMENT_EXPIRE_TIME = "Expiration_date";
    
    public static final String ELEMENT_REFRESH_TOKEN = "Refresh_Token";  
    
    public static final String ELEMENT_ERROR = "Error";
    
    public static final String ELEMENT_MESSAGE = "Message";
    
    private String token;

    private String expireTime;

    private String refreshToken;
    
    private boolean hasError;
    
    private String errorMsg;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(String expireTime)
    {
        this.expireTime = expireTime;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }
    
    public boolean isHasError()
    {
        return hasError;
    }

    public void setHasError(boolean hasError)
    {
        this.hasError = hasError;
    }

    @Override
    public String toString()
    {
        return this.hasError ? String.format("errorMsg: %s", errorMsg) : 
            String.format("token=%s, expireTime=%s, refreshToken=%s", token, expireTime, refreshToken);
    }
}