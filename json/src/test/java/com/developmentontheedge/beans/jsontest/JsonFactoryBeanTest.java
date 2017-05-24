package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.jsontest.TestBeans.BeanWithInnerClass;
import org.junit.Test;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanTest
{
    @Test
    public void simpleBeanValues() throws Exception
    {
        TestBeans.SimpleBean rowHeaderBean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{'arr':[1,2,3]," +
                        "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$SimpleBean'," +
                        "'name':'bean'," +
                        "'number':5}",
                oneQuotes(JsonFactory.beanValues(rowHeaderBean).toString()));
    }

    @Test
    public void beanWithInnerClass() throws Exception
    {
        BeanWithInnerClass rowHeaderBean = new BeanWithInnerClass(
                new TestBeans.InnerBeanClass("foo"),
                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
        );
        assertEquals("{'arr':[" +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo1'}," +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo2'}" +
                "]," +
                "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithInnerClass'," +
                "'field1':{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo'}}",
            oneQuotes(JsonFactory.beanValues(rowHeaderBean).toString()));
    }

    @Test
    public void simpleBeanMeta()
    {
        TestBeans.SimpleBean rowHeaderBean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'/arr':{'type':'long[]','readOnly':true}," +
                        "'/class':{'type':'Class','readOnly':true}," +
                        "'/name':{'type':'String','readOnly':true}," +
                        "'/number':{'type':'Integer','readOnly':true}}",
                oneQuotes(JsonFactory.beanMeta(rowHeaderBean).toString()));
    }

    @Test
    public void beanWithDps()
    {
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class, ""));
        TestBeans.FormPresentation bean = new TestBeans.FormPresentation("TestBean", dps);
        assertEquals("{" +
                        "'values':{" +
                            "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$FormPresentation'," +
                            "'dps':{'name':''},'title':'TestBean'}," +
                        "'meta':{" +
                            "'/class':{'type':'Class','readOnly':true}," +
                            "'/dps':{'type':'DynamicPropertySetSupport'}," +
                            "'/dps/name':{'type':'String'}," +
                            "'/title':{'type':'String','readOnly':true}}," +
                        "'order':['/class','/dps','/dps/name','/title']" +
            "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

}