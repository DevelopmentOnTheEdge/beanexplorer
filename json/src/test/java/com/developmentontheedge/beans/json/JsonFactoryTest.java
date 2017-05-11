package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class JsonFactoryTest
{

    @Test
    public void testDpsValues() throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", "Name", String.class, "testName"));
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));

        assertEquals("{'name':'testName','number':1}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    @Ignore
    public void testDpsMeta() throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", "Name", String.class, "Value"));
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));

        assertEquals("",
                oneQuotes(JsonFactory.dpsMeta(dps).toString()));
    }


    @Test
    public void testDpsValues2() throws Exception
    {
        DynamicPropertySet dpsP2 = new DynamicPropertySetSupport();
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p21"));
        dpsP2.add(new DynamicProperty("c2", "c2", String.class, "p22"));

        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("p1", "p1", String.class, "a"));
        dps.add(new DynamicProperty("p2", "p2", DynamicPropertySet.class, dpsP2));
        dps.getProperty("p1").setReadOnly(true);
        dps.getProperty("p2").setAttribute("","");

        assertEquals("{'p1':'a','p2':{'c1':'p21','c2':'p22'}}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    @Ignore
    public void testDps() throws Exception
    {
        DynamicPropertySet dpsP2 = new DynamicPropertySetSupport();
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p21"));
        dpsP2.add(new DynamicProperty("c2", "c2", String.class, "p22"));

        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("p1", "p1", String.class, "a"));
        dps.add(new DynamicProperty("p2", "p2", DynamicPropertySetSupport.class, dpsP2));
        dps.getProperty("p1").setReadOnly(true);
        dps.getProperty("p2").setAttribute("","");

        assertEquals("{"+
                "'values':{'p1':'a'," +
                          "'p2':{'c1':'p21','c2':'p22'}" +
                "}," +
                "'meta':[{'p1':{'title':'p1','readOnly':true}}," +
                        "{'p2':{'title':'p2','readOnly':true}}," +
                        "{'/p2/c1':{title:'c1','readOnly':true}}" +
                "]," +
                "}", oneQuotes(JsonFactory.dps(dps).toString()));
    }

    public static String oneQuotes(String s)
    {
        return s.replace("\"", "'");
    }

    @Test
    public void testEncodeColor() throws Exception
    {
        Color color = Color.CYAN;
        assertEquals("["+color.getRed()+","+color.getGreen()+","+color.getBlue()+"]",
                JsonFactory.encodeColor(color).build().toString());
    }

    @Test
    public void testEncodeColorNull() throws Exception
    {
        Color color = new Color(128,128,128,0);
        assertEquals("[]", JsonFactory.encodeColor(color).build().toString());

        assertEquals("[]", JsonFactory.encodeColor(null).build().toString());
    }


}