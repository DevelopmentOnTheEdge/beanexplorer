package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.jsontest.TestBeans.BeanWithInnerClass;
import org.junit.Test;

import javax.json.JsonObject;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanTest
{
    @Test
    public void simpleBeanValues() throws Exception
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'arr':[1,2,3]," +
                        "'name':'bean'," +
                        "'number':5" +
                "}", oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void simpleBeanValuesWithJsonValue()
    {
        TestBeans.InnerBeanClass bean = new TestBeans.InnerBeanClass("foo");
        JsonObject jsonObject = JsonFactory.beanValues(bean);
        TestBeans.TypedResponse typedResponse = new TestBeans.TypedResponse("form", jsonObject);

        assertEquals("{'name':'foo'}", oneQuotes(jsonObject.toString()));

        assertEquals("" +
                    "{" +
                        "'type':'form'," +
                        "'value':{'name':'foo'}" +
            "}" , oneQuotes(JsonFactory.beanValues(typedResponse).toString()));
    }

    @Test
    public void beanWithInnerClass() throws Exception
    {
        BeanWithInnerClass bean = new BeanWithInnerClass(
                new TestBeans.InnerBeanClass("foo"),
                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
        );
        assertEquals("{'arr':[" +
                        "{'name':'foo1'}," +
                        "{'name':'foo2'}" +
                "]," +
                "'field1':{'name':'foo'}}",
            oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void simpleBeanMeta()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'/arr':{'type':'long[]','readOnly':true}," +
                        "'/name':{'type':'String','readOnly':true}," +
                        "'/number':{'type':'Integer','readOnly':true}}",
                oneQuotes(JsonFactory.beanMeta(bean).toString()));
    }

    @Test
    public void simpleBeanOrder()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("['/arr','/name','/number']",
                oneQuotes(JsonFactory.beanOrder(bean).toString()));
    }

    @Test
    public void beanWithDps()
    {
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class, ""));
        TestBeans.FormPresentation bean = new TestBeans.FormPresentation("TestBean", dps);
        assertEquals("{" +
                        "'values':{" +
                            "'dps':{'name':''},'title':'TestBean'}," +
                        "'meta':{" +
                            "'/dps':{'type':'DynamicPropertySetSupport'}," +
                            "'/dps/name':{'type':'String'}," +
                            "'/title':{'type':'String','readOnly':true}}," +
                        "'order':['/dps','/dps/name','/title']" +
            "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

}