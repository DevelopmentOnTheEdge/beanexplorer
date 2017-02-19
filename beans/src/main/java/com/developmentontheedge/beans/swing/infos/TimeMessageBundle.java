package com.developmentontheedge.beans.swing.infos;

import java.util.ListResourceBundle;

public class TimeMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Time (HH:MI:SS)" },
        { "SHORT_DESCRIPTION",        "Time in HH:MI:SS format" },
    };
}// end of class MessagesBundle
