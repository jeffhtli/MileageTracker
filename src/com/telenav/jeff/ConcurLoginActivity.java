package com.telenav.jeff;

import com.telenav.jeff.service.concur.ConcurService;
import com.telenav.jeff.service.concur.TokenManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class ConcurLoginActivity extends Activity
{

    private static final String LOGIN_URL_TEMPLATE = 
            "https://www.concursolutions.com/net2/oauth2/Login.aspx?" +
            "client_id=%s&scope=%s&redirect_uri=%s&state=%s";
    
    private static final String PARAM_SCOPE = "EXPRPT,USER";
    private static final String PARAM_REDIRECT_URI = "login://result";
    private static final String PARAM_STATE = "";
    
    
    protected static final String LOG_TAG = null;
        
        
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        loadLoginView();
    }
    
    private void loadLoginView()
    {
        setContentView(R.layout.activity_concur_login);        
        WebView loginView = (WebView) findViewById(R.id.loginWebView);
        loginView.loadUrl(String.format(LOGIN_URL_TEMPLATE, ConcurService.AUTH_PARAM_CLIENT_ID, 
            PARAM_SCOPE, PARAM_REDIRECT_URI, PARAM_STATE));
        WebSettings settings = loginView.getSettings();
        settings.setJavaScriptEnabled(true);
        loginView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.d(LOG_TAG, url);
                if (url.startsWith(PARAM_REDIRECT_URI))
                {
                    String[] strArray = url.split("\\?");
                    LoginResult result = paraseResult(strArray[1]);
                    Log.d(LOG_TAG, "==================== login result:=" + result.isLoginSuccessfully);
                    nextView(result);
                    return true;
                }
                else
                {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                
            }

            private LoginResult paraseResult(String loginStr)
            {
                LoginResult result = new LoginResult();
                String[] loginStatus = loginStr.split("&");
                String[] status = loginStatus[0].split("=");
                if ("code".equals(status[0]))
                {
                    result.isLoginSuccessfully = true;
                    result.code = status[1];
                }
                else
                {
                    result.error = loginStr;
                }
                
                return result;
            }
        });
    }
    
    private class LoginResult
    {
        boolean isLoginSuccessfully;
        String code;
        String error;
        String errorMsg;
    }
    
    private void nextView(LoginResult loginResult)
    {
        if (loginResult.isLoginSuccessfully)
        {
            Intent intent = new Intent(this, ExportToConcurActivity.class);
            intent.putExtra("code", loginResult.code);
            
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        }
        
    }
}
