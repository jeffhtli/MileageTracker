package com.telenav.jeff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class SimpleAsyncTask extends AsyncTask<String, Void, Void>
{
    private ProgressDialog progress;
    protected Context context;

    public SimpleAsyncTask(Context context, String msg)
    {
        this.context = context;
        if (msg != null)
        {
            progress = new ProgressDialog(context); 
            progress.setMessage(msg);
        }
    }
    
    @Override
    protected void onPreExecute()
    {
        if (progress != null)
        {
            progress.show();
        }
        
        super.onPreExecute();
    }
    
    @Override
    protected void onPostExecute(Void result)
    {
        if (progress != null)
        {
            progress.dismiss();
        }
        super.onPostExecute(result);
    }
    
}
