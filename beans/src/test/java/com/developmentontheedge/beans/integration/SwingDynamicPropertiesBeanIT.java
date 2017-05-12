/** $Id: DynamicPropertiesBeanTest.java,v 1.3 2001/10/04 09:03:15 champ Exp $ */
package com.developmentontheedge.beans.integration;

import com.developmentontheedge.beans.DynamicPropertiesBean;
import junit.framework.Test;

public class SwingDynamicPropertiesBeanIT extends SwingBeanIT
{
    public SwingDynamicPropertiesBeanIT(String name)
    {
        super(name);
    }

    @Override
    public void testModelProperties() throws Exception
    {

    }


    public static Test suite()
    {
        SwingBeanIT.beanClass = DynamicPropertiesBean.class;
        SwingBeanIT.testClass = SwingDynamicPropertiesBeanIT.class;

        return SwingBeanIT.suite();
    }

    public static void main(String[] args)
    {
        SwingBeanIT.beanClass = DynamicPropertiesBean.class;
        SwingBeanIT.testClass = SwingDynamicPropertiesBeanIT.class;

        try
        {
            SwingBeanIT beanTest = new SwingDynamicPropertiesBeanIT("XXX");
            beanTest.testCreateBeanInstance();
            beanTest.testCreateModel();
            beanTest.testViewModel();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
