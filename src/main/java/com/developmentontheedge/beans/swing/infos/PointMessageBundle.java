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

public class PointMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Point" },
        { "SHORT_DESCRIPTION",        "Point property" },

        { "X_NAME",                "x" },
        { "X_DESCRIPTION",         "X coordinate of the point" },

        { "Y_NAME",                "y" },
        { "Y_DESCRIPTION",         "Y coordinate of the point" },
    };
}// end of class MessagesBundle
