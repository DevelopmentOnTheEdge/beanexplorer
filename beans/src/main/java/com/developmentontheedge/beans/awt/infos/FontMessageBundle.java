package com.developmentontheedge.beans.awt.infos;

import java.util.ListResourceBundle;

public class FontMessageBundle extends ListResourceBundle
{
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Font" },
        { "SHORT_DESCRIPTION",        "Font property" },
    };
}// end of class MessagesBundle
