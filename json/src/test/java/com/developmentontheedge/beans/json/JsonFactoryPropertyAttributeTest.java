package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class JsonFactoryPropertyAttributeTest
{
    private DynamicPropertySetSupport dps;
    private String name = "name";
    @Before
    public void init()
    {
        dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty(name, "Name", String.class, "testName"));
    }

    @Test
    @Ignore
    public void test() throws Exception
    {
        dps.getAsBuilder(name).nullable();
        assertEquals(Boolean.TRUE.toString(), JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getJsonString("canBeNull").getString());
    }

    public static String oneQuotes(String s)
    {
        return s.replace("\"", "'");
    }

}