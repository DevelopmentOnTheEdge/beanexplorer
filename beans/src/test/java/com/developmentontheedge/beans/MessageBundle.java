/** $Id: MessageBundle.java,v 1.2 2001/06/05 06:05:32 fedor Exp $ */

package com.developmentontheedge.beans;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle
{
    public Object[][] getContents() { return contents; }

    protected String[] choiceValues = {"apple", "banana", "orange"};

    private Object[][] contents =
    {
        { "CN_COMPOSITE_EDITOR_BEAN", "composite editor bean"},
        { "CD_COMPOSITE_EDITOR_BEAN", "composite editor bean"},

        { "CN_HIDDEN_BEAN", "hidden Bean"},
        { "CD_HIDDEN_BEAN", "hidden Bean"},

        { "CHOICE_VALUES", choiceValues},

        { "PN_BEAN", "bean"}, 
        { "PD_BEAN", "bean"}, 

        { "PN_HIDDEN_BEAN", "hidden Bean"}, 
        { "PD_HIDDEN_BEAN", "hidden Bean"}, 

        { "PN_INTEGER_PROPERTY", "integerProperty"},
        { "PD_INTEGER_PROPERTY", "integerProperty"}, 

        { "PN_BOOLEAN_PROPERTY", "booleanProperty"}, 
        { "PD_BOOLEAN_PROPERTY", "booleanProperty"}, 

        { "PN_COLOR_PROPERTY", "colorProperty"}, 
        { "PD_COLOR_PROPERTY", "Color property"}, 

        { "PN_STRING_PROPERTY", "stringProperty"}, 
        { "PD_STRING_PROPERTY", "stringProperty"}, 

    };
}
