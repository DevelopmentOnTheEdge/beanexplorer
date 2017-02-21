package com.developmentontheedge.beans.lesson12.barchart;

import java.beans.IntrospectionException;

import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import com.developmentontheedge.beans.lesson12.editors.OrientationEditor;

public class Preferences extends DynamicPropertySetSupport
{
    public Preferences()
    {
        try
        {
            DynamicProperty title = new DynamicProperty("title", String.class, "Quarter report");
            add(title);

            PropertyDescriptorEx orientationDesc = new PropertyDescriptorEx("orientation");
            orientationDesc.setPropertyEditorClass(OrientationEditor.class);
            DynamicProperty orientation = new DynamicProperty(orientationDesc, Integer.class, new Integer(BarChart.HORIZONTAL));
            add(orientation);

            DynamicProperty barSpacing = new DynamicProperty("barSpacing", "bar spacing", Integer.class, new Integer(10));
            add(barSpacing);

            DynamicProperty scale = new DynamicProperty("scale", Integer.class);
            scale.setValue(new Integer(10));
            add(scale);
        }
        catch ( IntrospectionException e )
        {
            System.out.println( e );
        }
    }
}
