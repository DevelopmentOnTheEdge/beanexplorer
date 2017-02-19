/*
 * Author:  Igor V. Tyazhev  (champ@developmentontheedge.com)
 *
 * Created: 27.03.2001
 *
 * Description:
 *
 * Copyright (C) 2000, 2001 DevelopmentOnTheEdge.com. All rights reserved.
 */
package com.developmentontheedge.beans.swing.infos;

import java.util.ListResourceBundle;

public class ColorMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Color" },
        { "SHORT_DESCRIPTION",        "Color property" },

        { "RED_NAME",                 "red" },
        { "RED_DESCRIPTION",          "Red component of the color" },

        { "GREEN_NAME",               "green" },
        { "GREEN_DESCRIPTION",        "Green component of the color" },

        { "BLUE_NAME",                "blue" },
        { "BLUE_DESCRIPTION",         "Blue component of the color" },
    };
}// end of class MessagesBundle
