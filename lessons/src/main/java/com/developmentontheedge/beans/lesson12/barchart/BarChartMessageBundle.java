package com.developmentontheedge.beans.lesson12.barchart;

import java.util.ListResourceBundle;

public class BarChartMessageBundle extends ListResourceBundle
{
    public Object[][] getContents() { return contents; }
    private final static Object[][] contents =
    {
        { "CN_CLASS", "BarChart"},
        { "CD_CLASS", "BarChart"},
        { "PN_PREF",  "Preferences"},
        { "PD_PREF",  "Preferences"},
        { "PN_COLUMNS", "columns"},
        { "PD_COLUMNS", "columns"},
    };
}
