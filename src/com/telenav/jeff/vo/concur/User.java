package com.telenav.jeff.vo.concur;

import java.util.List;

public class User
{
    /* 
     * The user's logon ID. 
     */
    public static final String ELEMENT_LOGINID = "LoginId";

    /* 
     * Whether the user is currently active. Format: Y/N. 
     */
    public static final String ELEMENT_ACTIVE = "Active";

    /* 
     * The user's first name. 
     */
    public static final String ELEMENT_FIRSTNAME = "FirstName";

    /* 
     * The user's last name.
     */
    public static final String ELEMENT_LASTNAME = "LastName";

    /*
     * The user's middle initial.
     */
    public static final String ELEMENT_MINAME = "Mi";

    /*
     * The user's email address.
     */
    public static final String ELEMENT_EMAIL = "EmailAddress";

    /*
     * The unique identifier for the user.
     */
    public static final String ELEMENT_EMPID = "EmpId";

    /*
     * The user's assigned account code ledger.
     */
    public static final String ELEMENT_LEDGERNAME = "LedgerName";

    /*
     * The user's language locale code. One of the Supported Locales. Example: United States English is en_US.
     */
    public static final String ELEMENT_LOCALENAME = "LocaleName";

    /*
     * Varies depending on configuration.
     */
    public static final String ELEMENT_ORGUNIT = "OrgUnit";

    /*
     * Varies depending on configuration. If the custom field is a list field,
     * the data will be returned as: (list item short code) list item name.
     * List Field Example: <Custom1>(1234) Project 1234</Custom1>
     */
    public static final String ELEMENT_CUSTOM = "Custom";

    /*
     * The user's two-digit country code.
     */
    public static final String ELEMENT_CTRYCODE = "CtryCode";

    /*
     * The user's account code for cash advances.
     */
    public static final String ELEMENT_CASHADVANCEACCOUNTCODE = "CashAdvanceAccountCode";

    /*
     * The user's three digit reimbursement currency. Example: United States Dollar is USD.
     */
    public static final String ELEMENT_CRNCODE = "CrnCode";

    /*
     * The user's two-digit country code and two-digit state or province code. Example: Washington State, United States
     * is US-WA.
     */
    public static final String ELEMENT_CTRYSUBCODE = "CtrySubCode";

    /*
     * Whether the user has access to Expense. Format: Y/N.
     */
    public static final String ELEMENT_EXPENSEUSER = "ExpenseUser";

    /*
     * Whether the user is an Expense approver. Format: Y/N.
     */
    public static final String ELEMENT_EXPENSEAPPROVER = "ExpenseApprover";

    /*
     * Whether the user has access to Travel. Format: Y/N.
     */
    public static final String ELEMENT_TRIPUSER = "TripUser";

    /*
     * Whether the user has access to Invoice. Format: Y/N.
     */
    public static final String ELEMENT_INVOICEUSER = "InvoiceUser";

    /*
     * Whether the user is an Invoice approver. Format: Y/N.
     */
    public static final String ELEMENT_INVOICEAPPROVER = "InvoiceApprover";

    /*
     * The employee ID of the user's Expense approver. 
     * If you are importing both a user and their approver, the approver
     * should be listed before the user in the batch.
     */
    public static final String ELEMENT_EXPENSEAPPROVEREMPLOYEEID = "ExpenseApproverEmployeeID";

    /*
     * Whether the user is a test user. Format: Y/N.
     */
    public static final String ELEMENT_ISTESTEMP = "IsTestEmp";

    private String loginId;

    private boolean active;

    private String firstName;

    private String lastName;

    private String miName;

    private String email;

    private String empId;

    private String leaderName;

    private String localName;

    private List<String> orgUnits;

    private List<String> customs;

    private String countryCode;

    private String cashAdvanceAccountCode;

    private String currencyCode;

    private String countrySubCode;

    private boolean expenseUser;

    private boolean expenseApprover;

    private boolean tripUser;

    private boolean invoiceUser;

    private boolean invoiceApprover;

    private String invoiceApproverEmployeeId;

    private boolean testEmployee;

    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getMiName()
    {
        return miName;
    }

    public void setMiName(String miName)
    {
        this.miName = miName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmpId()
    {
        return empId;
    }

    public void setEmpId(String empId)
    {
        this.empId = empId;
    }

    public String getLeaderName()
    {
        return leaderName;
    }

    public void setLeaderName(String leaderName)
    {
        this.leaderName = leaderName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName(String localName)
    {
        this.localName = localName;
    }

    public List<String> getOrgUnits()
    {
        return orgUnits;
    }

    public void setOrgUnits(List<String> orgUnits)
    {
        this.orgUnits = orgUnits;
    }

    public List<String> getCustoms()
    {
        return customs;
    }

    public void setCustoms(List<String> customs)
    {
        this.customs = customs;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCashAdvanceAccountCode()
    {
        return cashAdvanceAccountCode;
    }

    public void setCashAdvanceAccountCode(String cashAdvanceAccountCode)
    {
        this.cashAdvanceAccountCode = cashAdvanceAccountCode;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getCountrySubCode()
    {
        return countrySubCode;
    }

    public void setCountrySubCode(String countrySubCode)
    {
        this.countrySubCode = countrySubCode;
    }

    public boolean isExpenseUser()
    {
        return expenseUser;
    }

    public void setExpenseUser(boolean expenseUser)
    {
        this.expenseUser = expenseUser;
    }

    public boolean isExpenseApprover()
    {
        return expenseApprover;
    }

    public void setExpenseApprover(boolean expenseApprover)
    {
        this.expenseApprover = expenseApprover;
    }

    public boolean isTripUser()
    {
        return tripUser;
    }

    public void setTripUser(boolean tripUser)
    {
        this.tripUser = tripUser;
    }

    public boolean isInvoiceUser()
    {
        return invoiceUser;
    }

    public void setInvoiceUser(boolean invoiceUser)
    {
        this.invoiceUser = invoiceUser;
    }

    public boolean isInvoiceApprover()
    {
        return invoiceApprover;
    }

    public void setInvoiceApprover(boolean invoiceApprover)
    {
        this.invoiceApprover = invoiceApprover;
    }

    public String getInvoiceApproverEmployeeId()
    {
        return invoiceApproverEmployeeId;
    }

    public void setInvoiceApproverEmployeeId(String invoiceApproverEmployeeId)
    {
        this.invoiceApproverEmployeeId = invoiceApproverEmployeeId;
    }

    public boolean isTestEmployee()
    {
        return testEmployee;
    }

    public void setTestEmployee(boolean testEmployee)
    {
        this.testEmployee = testEmployee;
    }
}
