/** $Id: HiddenBean.java,v 1.2 2001/06/05 06:05:32 fedor Exp $ */
package com.developmentontheedge.beans;

public class HiddenBean extends SimpleBean
{
    int choiceProperty = 0;
    public int getChoiceProperty()                     { return choiceProperty; }
    public void setChoiceProperty(int value)           { choiceProperty = value; }

}


