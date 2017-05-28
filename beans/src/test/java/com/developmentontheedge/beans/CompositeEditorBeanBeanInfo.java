/** $Id: CompositeEditorBeanBeanInfo.java,v 1.2 2001/06/05 06:05:32 fedor Exp $ */

package com.developmentontheedge.beans;

import java.awt.*;
import java.beans.IntrospectionException;

public class CompositeEditorBeanBeanInfo extends BeanInfoEx
{
    public CompositeEditorBeanBeanInfo()
    {
        super( CompositeEditorBean.class, "com.developmentontheedge.beans._test.MessageBundle" );
        beanDescriptor.setDisplayName( getResourceString("CN_CLASS") );
        beanDescriptor.setShortDescription( getResourceString("CD_CLASS") );
    }

    public void initProperties() throws IntrospectionException
    {
        PropertyDescriptorEx pde;
        IndexedPropertyDescriptorEx ipde;

        pde = new PropertyDescriptorEx( "hiddenBean", beanClass );
        pde.setCompositeEditor("booleanProperty;choiceProperty", new GridLayout(1, 2));
        add( pde, getResourceString( "PN_HIDDEN_BEAN" ), getResourceString( "PD_HIDDEN_BEAN" ) );

        pde = new PropertyDescriptorEx( "bean", beanClass );
        pde.setCompositeEditor("booleanProperty;integerProperty;stringProperty", new FlowLayout(FlowLayout.LEFT, 5, 0));
        add( pde, getResourceString( "PN_BEAN" ), getResourceString( "PD_BEAN" ) );
    }
}
