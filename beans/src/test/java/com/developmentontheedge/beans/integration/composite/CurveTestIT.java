package com.developmentontheedge.beans.integration.composite;

import com.developmentontheedge.beans.integration.SwingBeanIT;
import junit.framework.Test;

public class CurveTestIT extends SwingBeanIT {
    public CurveTestIT(String name) {
        super(name);
    }

    @Override
    public void testModelProperties() throws Exception {

    }


    public static Test suite() {
        SwingBeanIT.beanClass = CurveWrapper.class;
        SwingBeanIT.testClass = com.developmentontheedge.beans.integration.SwingDynamicPropertiesBeanIT.class;

        return SwingBeanIT.suite();
    }

    public static void main(String[] args) {
        SwingBeanIT.beanClass = CurveWrapper.class;
        SwingBeanIT.testClass = com.developmentontheedge.beans.integration.SwingDynamicPropertiesBeanIT.class;

        try {
            SwingBeanIT beanTest = new com.developmentontheedge.beans.integration.SwingDynamicPropertiesBeanIT("XXX");
            beanTest.testCreateBeanInstance();
            beanTest.testCreateModel();
            beanTest.testViewModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
