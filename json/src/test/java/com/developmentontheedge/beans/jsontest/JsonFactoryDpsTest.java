package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonFactoryDpsTest
{
    private DynamicPropertySet dps;

    @Before
    public void init()
    {
        dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", "Name", String.class, "testName"));
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));
    }

    @Test
    public void testDpsValues() throws Exception
    {
        assertEquals("{'name':'testName','number':1}",
                oneQuotes(JsonFactory.dpsValues(dps).toString()));
    }

    @Test
    public void testDpsMeta() throws Exception
    {
        assertEquals("{" +
                        "'/name':{'displayName':'Name'}," +
                        "'/number':{'displayName':'Number','type':'Long'}" +
            "}",oneQuotes(JsonFactory.dpsMeta(dps).toString()));
    }

    @Test
    public void testDpsOrder() throws Exception
    {
        assertEquals("['/name','/number']",
                oneQuotes(JsonFactory.dpsOrder(dps).toString()));
    }

    @Test
    public void testDps() throws Exception
    {
        assertEquals("{" +
                        "'values':{'name':'testName','number':1}," +
                        "'meta':{" +
                            "'/name':{'displayName':'Name'}," +
                            "'/number':{'displayName':'Number','type':'Long'}" +
                        "}," +
                        "'order':['/name','/number']" +
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
                        "'/p2/c1':{'displayName':'c1'}," +
                        "'/p2/c2':{'displayName':'c2'}" +
                "}," +
                "'order':['/p1','/p2','/p2/c1','/p2/c2']" +
                "}", oneQuotes(JsonFactory.dps(dps).toString()));
    }

    public static String oneQuotes(String s)
    {
        return s.replace("\"", "'");
    }

    @Test
    public void beanWithDps()
    {
        BeanWithDps beanWithDps = new BeanWithDps("Title", dps);
        assertEquals("{" +
                        "'/class':{'type':'Class','readOnly':true}," +
                        "'/dps':{'type':'DynamicPropertySetSupport'}," +
                        "'/dps/name':{'type':'String','displayName':'Name','description':'Name'}," +
                        "'/dps/number':{'type':'Long','displayName':'Number','description':'Number'}," +
                        "'/title':{'type':'String','readOnly':true}" +
            "}", oneQuotes(JsonFactory.beanMeta(beanWithDps).toString()));
    }

    public class BeanWithDps
    {
        private String title;
        private DynamicPropertySet dps;

        public BeanWithDps(String title, DynamicPropertySet dps)
        {
            this.title = title;
            this.dps = dps;
        }

        public String getTitle()
        {
            return title;
        }

        public DynamicPropertySet getDps()
        {
            return dps;
        }
    }

}