/** $Id: SimpleBean.java,v 1.3 2001/07/25 07:06:45 lesa Exp $ */
package com.developmentontheedge.beans;

import java.awt.*;

/**
 * SimpleBean
 */
public class SimpleBean
{
    //protected Category cat;

    public SimpleBean()
    {
        //PropertyConfigurator.configure("beantest.lcf");      //commented by lesa: the using not found  ( KAKOGO HUJA ) ?
        //cat = Category.getInstance(this.getClass());
    }

    String stringProperty = "stringValue";
    public String getStringProperty()                   { return stringProperty;  }
    public void   setStringProperty(String value)       { stringProperty = value; }

    boolean booleanProperty = true;
    public boolean getBooleanProperty()                 {return booleanProperty;   }
    public void    setBooleanProperty(boolean value)    { booleanProperty = value; }

    int integerProperty = 20;
    public int getIntegerProperty()                     { return integerProperty; }
    public void setIntegerProperty(int value)           { integerProperty = value; }

    Color colorProperty = Color.pink;
    public Color getColorProperty()                     { return colorProperty; }
    public void setColorProperty(Color value)           { colorProperty = value; }
}
