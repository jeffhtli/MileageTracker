package com.telenav.jeff;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.telenav.jeff.service.concur.ExpenseService;
import com.telenav.jeff.service.concur.TokenService;
import com.telenav.jeff.vo.concur.ReportSummary;

public class ExportToConcurActivity extends Activity
{
    private SimpleAsyncTask simpleAsyncTask;
    private Spinner reporterSpinner;
    private List<ReportSummary> reportList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_concur);
        
        reporterSpinner = (Spinner)findViewById(R.id.reporter_spinner);
        
        startBackgroudService();
    }
    
    private void startBackgroudService()
    {
        simpleAsyncTask = new SimpleAsyncTask();
        boolean isLogin = getIntent().getExtras().getBoolean("isLogin");
        if (!isLogin)
        {
            simpleAsyncTask.execute(this.getAuthenticateCommand(), this.getReporterListCommand());
        }
        else
        {
            simpleAsyncTask.execute(this.getReporterListCommand());
        }
    }

    private class SimpleAsyncTask extends AsyncTask<Command, String, Void>
    {
        private ProgressDialog progress;
        
        public SimpleAsyncTask()
        {
            progress = new ProgressDialog(ExportToConcurActivity.this);
            progress.setTitle("");
        }
        
        @Override
        protected void onPreExecute()
        {
            progress.show();
            super.onPreExecute();
        }
        
        @Override
        protected Void doInBackground(Command... cmds)
        {
            for(Command cmd : cmds )
            {
                publishProgress(cmd.getPrompt());
                cmd.execute();
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (progress != null)
            {
                progress.dismiss();
            }
            
            if (reportList != null )
            {
                ArrayAdapter<ReportSummary> adapter = new ArrayAdapter<ReportSummary>(
                        ExportToConcurActivity.this,
                        android.R.layout.simple_spinner_item, reportList);  
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                reporterSpinner.setAdapter(adapter);
            }
            
        }
        
        @Override
        protected void onProgressUpdate(String... values)
        {
            progress.setTitle(values[0] + "...");
        }
        
    }
    
    private class Command
    {
        private String prompt;
        
        public Command(String prompt)
        {
            this.prompt = prompt;
        }
        
        public Void execute()
        {
            return null;
        }
        
        public String getPrompt()
        {
            return this.prompt;
        }
    }
    
    private Command getAuthenticateCommand()
    {
        final String code = getIntent().getExtras().getString("code");
        return new Command("Authenticating")
        {
            @Override
            public Void execute()
            {
                new TokenService().requestToken(code);
                return null;
            }
        };
    }
    
    private Command getReporterListCommand()
    {
        return new Command("Getting Reporter")
        {
            @Override
            public Void execute()
            {
                reportList = new ExpenseService().getReportList();
                return null;
            }
        }; 
    }
}
