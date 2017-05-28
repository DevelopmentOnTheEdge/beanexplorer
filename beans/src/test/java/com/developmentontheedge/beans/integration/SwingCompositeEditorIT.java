/** $Id: CompositeEditorTest.java,v 1.1 2001/06/01 07:36:31 fedor Exp $ */
package com.developmentontheedge.beans.integration;

import com.developmentontheedge.beans.BeanTest;
import com.developmentontheedge.beans.CompositeEditorBean;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 *
 */
public class SwingCompositeEditorIT extends SwingBeanIT
{
    public SwingCompositeEditorIT(String name)
    {
        super(name);
    }

    public void testModelProperties() throws Exception
    {
        cat.info(BeanTest.model.getNiceDescription());
        TestCase.assertEquals("Property count", new Integer(2), new Integer(BeanTest.model.getPropertyCount()));
    }

    public static Test suite()
    {
        BeanTest.beanClass = CompositeEditorBean.class;
        BeanTest.testClass = SwingCompositeEditorIT.class;

        return SwingBeanIT.suite();
    }

    public static void main (String[] args)
    {
        SwingCompositeEditorIT test = new SwingCompositeEditorIT("CompositeEditorTest");
        test.beanClass = CompositeEditorBean.class;
        test.testClass = SwingCompositeEditorIT.class;

        try
        {
            test.testViewModel();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
