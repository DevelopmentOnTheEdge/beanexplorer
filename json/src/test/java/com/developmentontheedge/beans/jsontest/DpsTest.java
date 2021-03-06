package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.test.TestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class DpsTest extends TestUtils
{
    private DynamicPropertySet dps;

    @Before
    public void init()
    {
        dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));
        dps.add(new DynamicProperty("name", "Name", String.class, "testName"));
    }

    @Test
    public void test() throws Exception
    {
        JsonFactory.setDpsValues(dps, doubleQuotes("{'number':2,'name':'testName2'}"));

        assertEquals(2L, dps.getValue("number"));
        assertEquals("testName2", dps.getValue("name"));
    }

    @Test
    public void testDpsValues() throws Exception
    {
        assertEquals("{'number':1,'name':'testName'}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    public void testDpsMeta() throws Exception
    {
        assertEquals("{" +
                        "'/number':{'displayName':'Number','type':'Long'}," +
                        "'/name':{'displayName':'Name'}" +
            "}",oneQuotes(JsonFactory.dpsMeta(dps).toString()));
    }

    @Test
    public void testDpsOrder() throws Exception
    {
        assertEquals("['/number','/name']",
                oneQuotes(JsonFactory.dpsOrder(dps).toString()));
    }

    @Test
    public void testDpsValueDate() throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date date = new Date(df.parse("2017-07-24").getTime());
        dps.add(new DynamicProperty("a", "a", Date.class, date.toString()));
        assertEquals("{'a':'2017-01-24'}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));

        dps.getProperty("a").setValue(date);
        assertEquals("{'a':'2017-01-24'}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    public void testDpsValueArray() throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();

        dps.add(new DynamicProperty("a", "a", String.class, new String[]{"vacation","sick"}));
        dps.getProperty("a").setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, true);

        assertEquals("{'a':['vacation','sick']}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    public void testDpsDate() throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();

        java.util.Date utilDate = new java.util.Date();
        dps.add(new DynamicProperty("a", "a", java.util.Date.class, utilDate));

        Date date = new Date(utilDate.getTime());
        assertEquals("{'a':'" + date.toString() + "'}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    public void testDpsMetaAttr() throws Exception
    {
        dps.remove("number");
        dps.getProperty("name").setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, true);
        dps.getProperty("name").setAttribute(BeanInfoConstants.MESSAGE, "Invalid type");
        dps.getProperty("name").setAttribute(BeanInfoConstants.STATUS, "error");
        assertEquals("{" +
                        "'/name':{" +
                            "'displayName':'Name'," +
                            "'multipleSelectionList':true," +
                            "'status':'error'," +
                            "'message':'Invalid type'" +
                        "}" +
            "}", oneQuotes(JsonFactory.dpsMeta(dps).toString()));
    }

    @Test
    public void testDps() throws Exception
    {
        assertEquals("{" +
                        "'values':{'number':1,'name':'testName'}," +
                        "'meta':{" +
                            "'/number':{'displayName':'Number','type':'Long'}," +
                            "'/name':{'displayName':'Name'}" +
                        "}," +
                        "'order':['/number','/name']" +
                    "}",
                oneQuotes(JsonFactory.dps(dps).toString()));
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
    public void testDpsNestedDps() throws Exception
    {
        DynamicPropertySet dpsP2 = new DynamicPropertySetSupport();
        dpsP2.add(new DynamicProperty("c1", "c1", String.class, "p21"));
        dpsP2.add(new DynamicProperty("c2", "c2", String.class, "p22"));

        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("p1", "p1", String.class, "a"));
        dps.add(new DynamicProperty("p2", "p2", DynamicPropertySetSupport.class, dpsP2));
        dps.getProperty("p1").setReadOnly(true);

        assertEquals("{"+
                "'values':{'p1':'a'," +
                          "'p2':{'c1':'p21','c2':'p22'}" +
                "}," +
                "'meta':{" +
                        "'/p1':{'displayName':'p1','readOnly':true}," +
                        "'/p2':{'displayName':'p2','type':'DynamicPropertySetSupport'}," +
                        "'/p2/c1':{'displayName':'c1','parent':'p2'}," +
                        "'/p2/c2':{'displayName':'c2','parent':'p2'}" +
                "}," +
                "'order':['/p1','/p2','/p2/c1','/p2/c2']" +
                "}", oneQuotes(JsonFactory.dps(dps).toString()));
    }


}