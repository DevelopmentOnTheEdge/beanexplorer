package com.developmentontheedge.beans.lesson07.barchart;

import java.util.ListResourceBundle;

public class BarChartMessageBundle extends ListResourceBundle
{
    public Object[][] getContents() { return contents; }
    private final static Object[][] contents =
    {
        { "CN_CLASS", "BarChart"},
        { "CD_CLASS", "BarChart"},
        { "PN_TITLE", "title"},
        { "PD_TITLE", "title"},
        { "PN_ORIENTATION", "orientation"},
        { "PD_ORIENTATION", "orientation"},
        { "PN_FONT", "font"},
        { "PD_FONT", "font"},
        { "PN_PREFERRED_SIZE", "size"},
        { "PD_PREFERRED_SIZE", "preferred size"},
        { "PN_BAR_SPACING", "bar spacing"},
        { "PD_BAR_SPACING", "bar spacing"},
        { "PN_SCALE", "scale"},
        { "PD_SCALE", "scale"},
/*->*/
        { "PN_COLUMNS0", "Columns (basic view)"}, 
        { "PD_COLUMNS0", "columns"}, 
        { "PN_COLUMNS1", "Columns (calculated names)"}, 
        { "PD_COLUMNS1", "columns"}, 
        { "PN_COLUMNS2", "Columns (with composite editor)"}, 
        { "PD_COLUMNS2", "columns"}, 
/*--*/
    };
}
