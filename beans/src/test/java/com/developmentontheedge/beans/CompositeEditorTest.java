/** $Id: CompositeEditorTest.java,v 1.1 2001/06/01 07:36:31 fedor Exp $ */
package com.developmentontheedge.beans;

import junit.framework.Test;

/**
 *
 */
public class CompositeEditorTest extends BeanTest
{
    public CompositeEditorTest(String name)
    {
        super(name);
    }

    public void testModelProperties() throws Exception
    {
        cat.info(model.getNiceDescription());
        //TODO assertEquals("Property count", new Integer(2), new Integer(model.getPropertyCount()));
    }

    public static Test suite()
    {
        beanClass = CompositeEditorBean.class;
        testClass = CompositeEditorTest.class;

        return BeanTest.suite();
    }

    public static void main (String[] args)
    {
        CompositeEditorTest test = new CompositeEditorTest("CompositeEditorTest");
        test.beanClass = CompositeEditorBean.class;
        test.testClass = CompositeEditorTest.class;

        try
        {
            test.testCreateBeanInstance();
            test.testCreateModel();
            test.testModelProperties();
            test.testViewModel();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
