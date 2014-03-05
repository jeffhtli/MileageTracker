package com.telenav.concur.vo;

public class Field
{
    /**
     * The form field ID.
     */
    public static final String ELEMENT_Id = "Id";

    /**
     * The form field label.
     */
    public static final String ELEMENT_Label = "Label";

    /**
     * The type of field.
     */
    public static final String ELEMENT_ControlType = "ControlType";

    /**
     * The type of data accepted by the field.
     */
    public static final String ELEMENT_DataType = "DataType";

    /**
     * The maximum length of the field value.
     */
    public static final String ELEMENT_MaxLength = "MaxLength";

    /**
     * Whether the field is required.
     */
    public static final String ELEMENT_Required = "Required";

    /**
     * The number of columns the field contains.
     */
    public static final String ELEMENT_Cols = "Cols";

    /**
     * The access level the specified user has to the field.
     */
    public static final String ELEMENT_Access = "Access";

    /**
     * The width of the field.
     */
    public static final String ELEMENT_Width = "Width";

    /**
     * Whether the field is custom.
     */
    public static final String ELEMENT_Custom = "Custom";

    /**
     * The field order on the form.
     */
    public static final String ELEMENT_Sequence = "Sequence";

    private String id;

    private String label;

    private String controlType;

    private String dataType;

    private int maxLength;

    private boolean required;

    private int cols;

    private String access;

    private int width;

    private boolean custom;

    private int sequence;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getControlType()
    {
        return controlType;
    }

    public void setControlType(String controlType)
    {
        this.controlType = controlType;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public int getCols()
    {
        return cols;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
    }

    public String getAccess()
    {
        return access;
    }

    public void setAccess(String access)
    {
        this.access = access;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public boolean isCustom()
    {
        return custom;
    }

    public void setCustom(boolean custom)
    {
        this.custom = custom;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

}
