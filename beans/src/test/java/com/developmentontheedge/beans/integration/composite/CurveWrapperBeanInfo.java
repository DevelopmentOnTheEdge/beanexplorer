package com.developmentontheedge.beans.integration.composite;

public class CurveWrapperBeanInfo extends BeanInfoExGeneric<CurveWrapper> {

    public CurveWrapperBeanInfo() {
        super(CurveWrapper.class);
    }

    @Override
    public void initProperties() throws Exception {
        add("singleCurve");
        add("test");
        add("curves");
    }
}
