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

public class BasicStrokeMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        { "DISPLAY_NAME",             "Stroke" },
        { "SHORT_DESCRIPTION",        "Stroke property" },
    };
}// end of class MessagesBundle
