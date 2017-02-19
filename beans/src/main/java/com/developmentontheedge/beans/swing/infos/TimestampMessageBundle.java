package com.developmentontheedge.beans.swing.infos;

import java.util.ListResourceBundle;

public class TimestampMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Timestamp" },
        { "SHORT_DESCRIPTION",        "Timestamp in yyyy-mm-dd hh:mm:ss.fffffffff format" },
    };
}// end of class MessagesBundle
