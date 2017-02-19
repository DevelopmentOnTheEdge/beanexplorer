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

public class FontMessageBundle extends ListResourceBundle
{
    @Override
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
