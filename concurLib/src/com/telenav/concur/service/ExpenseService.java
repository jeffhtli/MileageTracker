package com.telenav.concur.service;

import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.telenav.concur.vo.Field;
import com.telenav.concur.vo.ReportSummary;

/**
 * Service for query report and report fields
 * @author htli
 *
 */
public class ExpenseService extends ConcurService
{
    private static final String URI_EXPENSE_REPORT_GET = "https://www.concursolutions.com/api/expense/expensereport/v2.0/Reports";
    private static final String URI_FORM_FIELD_GET = "https://www.concursolutions.com/api/expense/expensereport/v1.1/report/Form/%s/Fields";
    private static final String URI_REPORT_HEADER_POST = "https://www.concursolutions.com/api/expense/expensereport/v1.1/report";
    
    public static final String FORM_ID_DEFAULT = "nMWt0JheSaVjNFezXBZFcumkYNhCj127U";
    
    private static final String LOG_TAG = "ExpenseService";
    
    public List<ReportSummary> getReportList()
    {
        byte[] data = get(URI_EXPENSE_REPORT_GET);
        
        if (data != null)
        {
            Log.d(LOG_TAG, new String(data));
            
            ReportListResponseHandler handler = new ReportListResponseHandler();
            parse(data, handler); 
            return  handler.getReportList();
        }
        
        return null;
    }
    
    public List<Field> getReportFieldList(String formId)
    {
        byte[] data = get(String.format(URI_FORM_FIELD_GET, formId));
        
        if (data != null)
        {
            Log.d(LOG_TAG, new String(data));
            
            FieldResponseHandler handler = new FieldResponseHandler();
            parse(data, handler);
            return handler.getFeildList();
        }

        return null;
    }
    
    public void postReportHeader(String xml)
    {
        byte[] data = post(URI_REPORT_HEADER_POST, xml);
        
        Log.d(LOG_TAG, new String(data));
    }
    
    
    
    private class FieldResponseHandler extends DefaultHandler
    {
        
        private String content;
        private List<Field> fieldList = new Vector<Field>();
        private Field field;
        
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            content = new String(ch, start, length);
            super.characters(ch, start, length);
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if ("FormField".equalsIgnoreCase(localName))
            {
                field = new Field();
            }
            
            super.startElement(uri, localName, qName, attributes);
        }
        
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if ("FormField".equalsIgnoreCase(localName))
            {
                fieldList.add(field);
            }
            else if (Field.ELEMENT_Id.equalsIgnoreCase(localName))
            {
                field.setId(content);
            }
            else if (Field.ELEMENT_Label.equalsIgnoreCase(localName))
            {
                field.setLabel(content);
            }
            else if (Field.ELEMENT_ControlType.equalsIgnoreCase(localName))
            {
                field.setControlType(content);
            }
            else if (Field.ELEMENT_DataType.equalsIgnoreCase(localName))
            {
                field.setDataType(content);
            }
            else if (Field.ELEMENT_MaxLength.equalsIgnoreCase(localName))
            {
                field.setMaxLength(getIntValue());
            }
            else if (Field.ELEMENT_Required.equalsIgnoreCase(localName))
            {
                field.setRequired(getBooleanValue());
            }
            else if (Field.ELEMENT_Cols.equalsIgnoreCase(localName))
            {
                field.setCols(getIntValue());
            }
            else if (Field.ELEMENT_Access.equalsIgnoreCase(localName))
            {
                field.setAccess(content);
            }
            else if (Field.ELEMENT_Width.equalsIgnoreCase(localName))
            {
                field.setWidth(getIntValue());
            }
            else if (Field.ELEMENT_Custom.equalsIgnoreCase(localName))
            {
                field.setCustom(getBooleanValue());
            }
            else if (Field.ELEMENT_Sequence.equalsIgnoreCase(localName))
            {
                field.setSequence(getIntValue());
            }
            
            content = null;
        }
        
        private int getIntValue()
        {
            int value = 0;
            try
            {
               value =  Integer.valueOf(content);
            }
            catch (Exception e)
            {
            }
            
            return value;
        }
        
        private boolean getBooleanValue()
        {
            return "Y".equalsIgnoreCase(content);
        }
        
        
        public List<Field> getFeildList()
        {
            return fieldList;
        }
        
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
