package com.telenav.jeff.service.concur;

import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.telenav.jeff.vo.concur.ReportSummary;

public class ExpenseService extends ConcurService
{
    private static final String URI_EXPENSE_REPORT_GET = "https://www.concursolutions.com/api/expense/expensereport/v2.0/Reports";
    
    private static final String LOG_TAG = "ExpenseService";
    
    public List<ReportSummary> getReportList()
    {
        byte[] data = get(URI_EXPENSE_REPORT_GET);
        
        Log.d(LOG_TAG, new String(data));
        
        ReportListResponseHandler handler = new ReportListResponseHandler();
        parse(data, handler);
        
        return  handler.getReportList();
    }
    
    private class ReportListResponseHandler extends DefaultHandler
    {
        private String content;
        private List<ReportSummary> reportList;
        private ReportSummary summary;
        
        public ReportListResponseHandler()
        {
            reportList = new Vector<ReportSummary>();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            content = new String(ch, start, length);
            super.characters(ch, start, length);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if ("ReportSummary".equalsIgnoreCase(localName))
            {
                summary = new ReportSummary();
            }
            
            super.startElement(uri, localName, qName, attributes);
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if ("ReportSummary".equalsIgnoreCase(localName))
            {
                reportList.add(summary);
            }
            else if (ReportSummary.ELEMENT_ReportName.equalsIgnoreCase(localName))
            {
                summary.setReportName(content);
            }
            else if (ReportSummary.ELEMENT_ReportID.equalsIgnoreCase(localName))
            {
                summary.setReportID(content);
            }
            else if (ReportSummary.ELEMENT_ReportCurrency.equalsIgnoreCase(localName))
            {
                summary.setReportCurrency(content);
            }
            else if (ReportSummary.ELEMENT_ReportTotal.equalsIgnoreCase(localName))
            {
                summary.setReportTotal(content);
            }
            else if (ReportSummary.ELEMENT_ReportDate.equalsIgnoreCase(localName))
            {
                summary.setReportDate(content);
            }
            else if (ReportSummary.ELEMENT_LastComment.equalsIgnoreCase(localName))
            {
                summary.setLastComment(content);
            }
            else if (ReportSummary.ELEMENT_ReportDetailsURL.equalsIgnoreCase(localName))
            {
                summary.setReportDetailsURL(content);
            }
            else if (ReportSummary.ELEMENT_ExpenseUserLoginID.equalsIgnoreCase(localName))
            {
                summary.setExpenseUserLogin(content);
            }
            else if (ReportSummary.ELEMENT_ApproverLoginID.equalsIgnoreCase(localName))
            {
                summary.setApproverLoginID(content);
            }
            else if (ReportSummary.ELEMENT_EmployeeName.equalsIgnoreCase(localName))
            {
                summary.setEmployeeName(content);
            }
            else if (ReportSummary.ELEMENT_ApprovalStatus.equalsIgnoreCase(localName))
            {
                summary.setApprovalStatus(content);
            }
            else if (ReportSummary.ELEMENT_PaymentStatus.equalsIgnoreCase(localName))
            {
                summary.setPaymentStatus(content);
            }
            else if (ReportSummary.ELEMENT_ApprovalURL.equalsIgnoreCase(localName))
            {
                summary.setApprovalURL(content);
            }
            super.endElement(uri, localName, qName);
        }
        
        public List<ReportSummary> getReportList()
        {
            return this.reportList;
        }

    }

}
