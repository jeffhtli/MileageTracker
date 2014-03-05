package com.telenav.concur.vo;

public class ReportSummary
{
    /**
     * The name of the report. Maximum 40 characters.
     */
    public static final String ELEMENT_ReportName = "ReportName";

    /**
     * The unique identifier for the report, which appears in the Concur Expense UI. Maximum 40 character varchar.
     */
    public static final String ELEMENT_ReportID = "ReportID";

    /**
     * The 3-letter ISO 4217 currency code for the expense report currency. Maximum 3 characters.
     */
    public static final String ELEMENT_ReportCurrency = "ReportCurrency";

    /**
     * The total amount of the report. Maximum 23 characters.
     */
    public static final String ELEMENT_ReportTotal = "ReportTotal";

    /**
     * The create date of the report. Maximum 10 characters.
     */
    public static final String ELEMENT_ReportDate = "ReportDate";

    /**
     * The text of the most recent comment on the report.
     */
    public static final String ELEMENT_LastComment = "LastComment";

    /**
     * The URL to access the report details.
     */
    public static final String ELEMENT_ReportDetailsURL = "ReportDetailsURL";

    /**
     * The Login ID of the user this report belongs to. Maximum 128 characters.
     */
    public static final String ELEMENT_ExpenseUserLoginID = "ExpenseUserLoginID";

    /**
     * The Login ID of the user's expense approver. Maximum 128 characters.
     */
    public static final String ELEMENT_ApproverLoginID = "ApproverLoginID";

    /**
     * The name of the expense report owner. Maximum 66 characters.
     */
    public static final String ELEMENT_EmployeeName = "EmployeeName";

    /**
     * The report's approval status, in the OAuth consumer's language.
     */
    public static final String ELEMENT_ApprovalStatus = "ApprovalStatus";

    /**
     * The report's payment status, in the OAuth consumer's language.
     */
    public static final String ELEMENT_PaymentStatus = "PaymentStatus";

    /**
     * The URL the user can use to log in to Concur to approve the report. This element appears when requesting the
     * TOAPPROVE view, and does not appear when using the Jobkey or BatchID search terms.
     */
    public static final String ELEMENT_ApprovalURL = "ApprovalURL";

    private String reportName;

    private String reportID;

    private String reportCurrency;

    private String reportTotal;

    private String reportDate;

    private String lastComment;

    private String reportDetailsURL;

    private String expenseUserLogin;

    private String approverLoginID;

    private String employeeName;

    private String approvalStatus;

    private String paymentStatus;

    private String approvalURL;

    public String getReportName()
    {
        return reportName;
    }

    public void setReportName(String reportName)
    {
        this.reportName = reportName;
    }

    public String getReportID()
    {
        return reportID;
    }

    public void setReportID(String reportID)
    {
        this.reportID = reportID;
    }

    public String getReportCurrency()
    {
        return reportCurrency;
    }

    public void setReportCurrency(String reportCurrency)
    {
        this.reportCurrency = reportCurrency;
    }

    public String getReportTotal()
    {
        return reportTotal;
    }

    public void setReportTotal(String reportTotal)
    {
        this.reportTotal = reportTotal;
    }

    public String getReportDate()
    {
        return reportDate;
    }

    public void setReportDate(String reportDate)
    {
        this.reportDate = reportDate;
    }

    public String getLastComment()
    {
        return lastComment;
    }

    public void setLastComment(String lastComment)
    {
        this.lastComment = lastComment;
    }

    public String getReportDetailsURL()
    {
        return reportDetailsURL;
    }

    public void setReportDetailsURL(String reportDetailsURL)
    {
        this.reportDetailsURL = reportDetailsURL;
    }

    public String getExpenseUserLogin()
    {
        return expenseUserLogin;
    }

    public void setExpenseUserLogin(String expenseUserLogin)
    {
        this.expenseUserLogin = expenseUserLogin;
    }

    public String getApproverLoginID()
    {
        return approverLoginID;
    }

    public void setApproverLoginID(String approverLoginID)
    {
        this.approverLoginID = approverLoginID;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public String getApprovalStatus()
    {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus)
    {
        this.approvalStatus = approvalStatus;
    }

    public String getPaymentStatus()
    {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }

    public String getApprovalURL()
    {
        return approvalURL;
    }

    public void setApprovalURL(String approvalURL)
    {
        this.approvalURL = approvalURL;
    }
    
    @Override
    public String toString()
    {
        return this.reportName;
    }

}
