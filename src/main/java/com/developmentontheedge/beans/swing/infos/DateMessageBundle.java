package com.developmentontheedge.beans.swing.infos;

import java.util.ListResourceBundle;

public class DateMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Date (YYYY-MM-DD)" },
        { "SHORT_DESCRIPTION",        "Date in YYYY-MM-DD format" },
    };
}// end of class MessagesBundle
