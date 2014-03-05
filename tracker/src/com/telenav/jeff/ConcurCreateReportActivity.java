package com.telenav.jeff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telenav.concur.service.ExpenseService;
import com.telenav.concur.vo.Field;

public class ConcurCreateReportActivity extends Activity
{
    private List<Field> fieldList;
    private LinearLayout rootLayout;
    private Map<String, View> viewMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_concur_create_report);
        
        rootLayout = (LinearLayout) findViewById(R.id.concur_create_report_root);
        
        viewMap = new HashMap<String, View>();
        
        new ReortFieldTask(this, "Getting report information").execute("");
    }
    
    private void addFields()
    {
        for (Field field : fieldList)
        {
            String access = field.getAccess();
            if (!"HD".equals(access) && !"RO".equals(access))
            {
                TextView textView = new TextView(this);
                textView.setText(field.getLabel());
                
                rootLayout.addView(textView); 
                
                View view = getViewById(field.getId());
                viewMap.put(field.getId(), view);
                rootLayout.addView(view);
            }
            
            if (field.isRequired())
            {
                Log.d("ConcurCreateReportActivity", field.getId());
            }
        }
        
        Button btn = new Button(this);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new SubmitReportHeaderTask(ConcurCreateReportActivity.this, "Save Report").execute("");
            }
        });
        btn.setText("Save");
        
        rootLayout.addView(btn);
    }
    
    
    private View getViewById(String id)
    {
        View view = null;
        
        if (id.equals("UserDefinedDate"))
        {
            TextView textView = new TextView(this);
            textView.setText(DateFormat.format("MM/dd yyyy", System.currentTimeMillis()));
            view = textView;
        }
        else if (id.equals("Name"))
        {
            EditText editText = new EditText(this);
            editText.setText("Report from Navigator " + DateFormat.format("MM/dd yyyy", System.currentTimeMillis()));
            view = editText;
        }
        else
        {
            view = new EditText(this);
        }
        
        android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 20);
        view.setLayoutParams(layoutParams);
        
        return view;
    }
    
    


    private class ReortFieldTask extends SimpleAsyncTask
    {
        public ReortFieldTask(Context context, String msg)
        {
            super(context, msg);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            fieldList = new ExpenseService().getReportFieldList(ExpenseService.FORM_ID_DEFAULT);
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            addFields();
            super.onPostExecute(result);
        }
        
    }
    
    private class SubmitReportHeaderTask extends SimpleAsyncTask
    {

        public SubmitReportHeaderTask(Context context, String msg)
        {
            super(context, msg);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            ReportHeaderParamer paramter = new ReportHeaderParamer();
            paramter.name = ((TextView)viewMap.get("Name")).getText().toString();
            paramter.comment = ((TextView)viewMap.get("Comment")).getText().toString();
            paramter.purpose = ((TextView)viewMap.get("Purpose")).getText().toString();
            paramter.project = ((TextView)viewMap.get("Custom11")).getText().toString();
            
            new ExpenseService().postReportHeader(paramter.toXMLString());
            
            return null;
        }
    }
    
    public class ReportHeaderParamer
    {
        public String name;
        public String purpose;
        public String comment;
        public String project;
        
        public String toXMLString()
        {
            StringBuilder sb = new StringBuilder("<Report xmlns=\"http://www.concursolutions.com/api/expense/expensereport/2011/03\">");
            sb.append(String.format("<Name>%s</Name>", name == null ? "" : name));
            sb.append(String.format("<UserDefinedDate>%s</UserDefinedDate>", 
                DateFormat.format("yyyy-MM-dd hh:mm:ss.0", System.currentTimeMillis())));
            sb.append(String.format("<Purpose>%s</Purpose>", purpose == null ? "" : purpose));
            sb.append(String.format("<Comment>%s</Comment>", comment == null ? "" : comment));
            sb.append(String.format("<Custom11>%s</Custom11>", project == null ? "" : project));
            sb.append("</Report>");
            
            String xml = sb.toString();
            Log.d("ReportHeaderParamer", xml);
            
            return xml;
        }
    }

}
