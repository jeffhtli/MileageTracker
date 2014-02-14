package com.telenav.jeff;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.telenav.jeff.service.concur.ExpenseService;
import com.telenav.jeff.service.concur.TokenService;
import com.telenav.jeff.vo.concur.ReportSummary;

public class ConcurSelectReportActivity extends Activity
{
    private SimpleAsyncTask simpleAsyncTask;
    private Spinner reporterSpinner;
    private List<ReportSummary> reportList;
    private Button nextBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_concur);
        
        reporterSpinner = (Spinner)findViewById(R.id.reporter_spinner);
        nextBtn = (Button)findViewById(R.id.concur_select_report_next_btn);
        nextBtn.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                int pos = reporterSpinner.getSelectedItemPosition();
                if (pos == reportList.size() - 1)
                {
                    startActivity(new Intent(ConcurSelectReportActivity.this, ConcurCreateReportActivity.class));
                }
                else
                {
                    ReportSummary summary = reportList.get(pos);
                    String id = summary.getReportID();
                    String name = summary.getReportName();
                    
                    Intent intent = new Intent(ConcurSelectReportActivity.this, ConcurCreateMileageReport.class);
                    intent.putExtra("reportName", name);
                    intent.putExtra("reportId", id);
                    startActivity(intent);
                }
                
                
            }
        });
        
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
            progress = new ProgressDialog(ConcurSelectReportActivity.this);
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
                        ConcurSelectReportActivity.this,
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
        return new Command("Getting Report")
        {
            @Override
            public Void execute()
            {
                reportList = new ExpenseService().getReportList();
                
                if (reportList == null)
                {
                    reportList = new Vector<ReportSummary>();
                }
                
                ReportSummary summary = new ReportSummary();
                summary.setReportName("Create new report...");
                reportList.add(summary);
                
                return null;
            }
        }; 
    }
}
