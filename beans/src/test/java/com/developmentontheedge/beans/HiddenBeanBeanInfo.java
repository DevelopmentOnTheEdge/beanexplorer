/** $Id: HiddenBeanBeanInfo.java,v 1.2 2001/06/05 06:05:32 fedor Exp $ */

package com.developmentontheedge.beans;

import java.beans.IntrospectionException;

public class HiddenBeanBeanInfo extends BeanInfoEx
{
    public HiddenBeanBeanInfo()
    {
        super( HiddenBean.class, "com.developmentontheedge.beans._test.MessageBundle");
        beanDescriptor.setDisplayName( getResourceString("CN_HIDDEN_BEAN") );
        beanDescriptor.setShortDescription( getResourceString("CD_HIDDEN_BEAN") );
    }

    public void initProperties() throws IntrospectionException
    {
        addHidden(new ChoicePropertyDescriptorEx("choiceProperty", beanClass, ChoiceEditor.class));
        addHidden(new PropertyDescriptorEx("integerProperty", beanClass));
        addHidden(new PropertyDescriptorEx("booleanProperty", beanClass));
        addHidden(new PropertyDescriptorEx("colorProperty",   beanClass));
        addHidden(new PropertyDescriptorEx("stringProperty",  beanClass));
    }
}
