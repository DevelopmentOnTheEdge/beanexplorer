package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import static org.junit.Assert.*;

public class JsonFactoryTest
{
    @Test
    public void testCreateDictionary() throws Exception
    {
        JsonArrayBuilder dictionary = JsonFactory.createDictionary(
                new String[]{"foo", "bar"}, true);
        assertEquals("[['0','foo'],['1','bar']]", oneQuotes(dictionary.build().toString()));
    }

    @Test
    public void testDpsValues() throws Exception
    {
        DynamicPropertySet dpsP2 = new DynamicPropertySetSupport();
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p21"));
        dpsP2.add(new DynamicProperty("c2", "c2", String.class, "p22"));

        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("p1", "p1", String.class, "a"));
        dps.add(new DynamicProperty("p2", "p2", DynamicPropertySetSupport.class, dpsP2));
        dps.getProperty("p1").setReadOnly(true);
        dps.getProperty("p2").setReadOnly(true);

        JsonObject dpsValues = JsonFactory.dpsValues(dps);
        assertEquals("{"+
                   "'values':{'p1':'a'," +
                             "'p2':{'c1':'p21','c2':'p22'}" +
                   "}" +
        "}", oneQuotes(dpsValues.toString()));
    }

    @Test
    @Ignore
    public void testDps() throws Exception
    {
        DynamicPropertySet dpsP2 = new DynamicPropertySetSupport();
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p21"));
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p22"));

        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("p1", "p1", String.class, "a"));
        dps.add(new DynamicProperty("p2", "p2", DynamicPropertySetSupport.class, dpsP2));
        dps.getProperty("p1").setReadOnly(true);
        dps.getProperty("p2").setReadOnly(true);

        JsonObject dictionary = JsonFactory.dpsValues(dps);
        assertEquals("{"+
                "'values':{'p1':'a'," +
                          "'p2':{'c1':'p21','c2':'p22'}" +
                "}," +
                "'meta':[{'p1':{'title':'p1','readOnly':true}}," +
                        "{'p2':{'title':'p2','readOnly':true}}," +
                        "{'/p2/c1':{title:'c1','readOnly':true}}" +
                "]," +
                "'dictionaries':{'p1':{'key1':'value1','key2':'value2'}" +
                "}", oneQuotes(dictionary.toString()));
    }

    private String oneQuotes(String s)
    {
        return s.replace("\"", "'");
    }

}