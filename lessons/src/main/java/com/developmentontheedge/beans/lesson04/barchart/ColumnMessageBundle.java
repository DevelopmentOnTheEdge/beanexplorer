package com.developmentontheedge.beans.lesson04.barchart;

import java.util.ListResourceBundle;

public class ColumnMessageBundle extends ListResourceBundle
{
    public Object[][] getContents() { return contents; }
    private final static Object[][] contents =
    {
        { "CN_CLASS", "Column"},
        { "CD_CLASS", "Column"},
        { "PN_LABEL", "label"},
        { "PD_LABEL", "label"},
        { "PN_VALUE", "value"},
        { "PD_VALUE", "value"},
        { "PN_COLOR", "color"},
        { "PD_COLOR", "color"},
        { "PN_VISIBLE", "visible"},
        { "PD_VISIBLE", "visible"},
    };
}
