package com.telenav.concur;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.telenav.concur.service.TokenManager;
import com.telenav.concur.service.TokenService;

@SuppressLint("SetJavaScriptEnabled")
public class ConcurLoginActivity extends Activity
{

    public static final int LOGIN_RESULT_REQUEST_CODE = 1;
    public static final String LOGIN_RESULT_KEY = "loginResult";
    
    private static final String LOGIN_URL_TEMPLATE = 
            "https://www.concursolutions.com/net2/oauth2/Login.aspx?" +
            "client_id=%s&scope=%s&redirect_uri=%s&state=%s";
    
    private static final String PARAM_SCOPE = "EXPRPT,USER";
    private static final String PARAM_REDIRECT_URI = "login://result";
    private static final String PARAM_STATE = "";
    
    private static final String LOG_TAG = "ConcurLoginActivity";
    
    private String consumerKey;
    private String consumerSecret;
        
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        consumerKey = getString(R.string.consumer_key);
        consumerSecret = getString(R.string.consumer_secret);
        
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret))
        {
            throw new IllegalArgumentException("Please define your API consumer key and secret in string.xml");
        }
        
        TokenManager.init(this);
        loadLoginView();
    }
    
    private void loadLoginView()
    {
        setContentView(R.layout.activity_concur_login);        
        WebView loginView = (WebView) findViewById(R.id.loginWebView);
        loginView.loadUrl(String.format(LOGIN_URL_TEMPLATE, consumerKey, 
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
                    if (result.isLoginSuccessfully)
                    {
                        new AuthenticationTask().execute(result.code, consumerKey, consumerSecret);
                    }
                    else
                    {
                        loginFinish(false);
                    }
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
       
    private void loginFinish(boolean b)
    {
        Intent i = new Intent();
        i.putExtra(LOGIN_RESULT_KEY, b);
        this.setResult(LOGIN_RESULT_REQUEST_CODE, i);
        this.finish();
        
    }
    
    private class AuthenticationTask extends SimpleAsyncTask
    {
        public AuthenticationTask()
        {
            super(ConcurLoginActivity.this, "Authenticating...");
        }

        @Override
        protected Void doInBackground(String... params)
        {
            new TokenService().requestToken(params[0], params[1], params[2]);
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            loginFinish(TokenManager.hasVaildToken());
        }
    }
}
