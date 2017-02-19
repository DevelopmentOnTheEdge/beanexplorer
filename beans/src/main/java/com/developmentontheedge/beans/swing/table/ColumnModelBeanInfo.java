package com.developmentontheedge.beans.swing.table;

import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.IndexedPropertyDescriptorEx;

/**
 * Bean info for {@link ColumnModel}.
 * @todo move text constants into MessageBundle
 */
public class ColumnModelBeanInfo extends BeanInfoEx
{
    public ColumnModelBeanInfo()
    {
        super( ColumnModel.class, ColumnModelMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString( "CN_CLASS" ) );
        beanDescriptor.setShortDescription( getResourceString( "CD_CLASS" ) );
    }

    @Override
    public void initProperties() throws IntrospectionException, NoSuchMethodException
    {
        IndexedPropertyDescriptorEx pdx = new IndexedPropertyDescriptorEx("columns", beanClass);
        pdx.setChildDisplayName(beanClass.getMethod("calcEntryNameInArray",
               new Class[] { Integer.class, Object.class } ) );

        add(pdx, getResourceString("PN_COLUMNS"),
                 getResourceString("PD_COLUMNS"));

        setSubstituteByChild(true);
    }
}