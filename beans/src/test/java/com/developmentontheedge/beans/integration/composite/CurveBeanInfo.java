package com.developmentontheedge.beans.integration.composite;

public class CurveBeanInfo extends BeanInfoExGeneric<Curve> {
    public CurveBeanInfo() {
        super(Curve.class);
        setHideChildren(true);
        setCompositeEditor("path;name;title", new java.awt.GridLayout(1, 3));
    }

    public void initProperties() {
        property("path").tags(bean -> bean.modules()).add();
        property("name").tags(bean -> bean.variables()).add();
        add("title");
    }

}
