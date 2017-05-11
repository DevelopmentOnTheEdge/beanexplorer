package com.developmentontheedge.beans.model;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ComponentFactoryTest
{
    @Test
    public void testPropertyIdentity()
    {
        BorderLayout borderLayout = new BorderLayout(1,1);
        ComponentModel model = ComponentFactory.getModel( borderLayout );
        ComponentModel filteredModel = ComponentFactory.filterByRemovingParentProperties( model );

        Property hgap1 = model.findProperty("hgap");
        assertNotNull( hgap1 );
        Property hgap2 = filteredModel.findProperty("hgap");
        assertNotNull( hgap2 );
        assertSame( "Properties are not identical.",hgap1,hgap2 );
    }
}