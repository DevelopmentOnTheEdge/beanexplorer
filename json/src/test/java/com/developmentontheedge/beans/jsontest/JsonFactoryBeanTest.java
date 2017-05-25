package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.jsontest.TestBeans.BeanWithInnerClass;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanTest
{
    @Test
    public void simpleBeanValues() throws Exception
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{'arr':[1,2,3]," +
                        "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$SimpleBean'," +
                        "'name':'bean'," +
                        "'number':5}",
                oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void beanWithInnerClass() throws Exception
    {
        BeanWithInnerClass bean = new BeanWithInnerClass(
                new TestBeans.InnerBeanClass("foo"),
                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
        );
        assertEquals("{'arr':[" +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo1'}," +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo2'}" +
                "]," +
                "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithInnerClass'," +
                "'field1':{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'foo'}}",
            oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void simpleBeanMeta()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'/arr':{'type':'long[]','readOnly':true}," +
                        "'/class':{'type':'Class','readOnly':true}," +
                        "'/name':{'type':'String','readOnly':true}," +
                        "'/number':{'type':'Integer','readOnly':true}}",
                oneQuotes(JsonFactory.beanMeta(bean).toString()));
    }

    @Test
    public void simpleBeanOrder()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("['/arr','/class','/name','/number']",
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

    @Test
    public void beanWithMap()
    {
        TestBeans.BeanWithMap bean = new TestBeans.BeanWithMap(ImmutableMap.of("key","value", "key2","value2"));
        assertEquals("{" +
                        "'values':{" +
                            "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithMap'," +
                            "'field':[{'key':'value'},{'key2':'value2'}]}," +
                        "'meta':{" +
                            "'/class':{'type':'Class','readOnly':true}," +
                            "'/field':{'type':'Map','readOnly':true}}," +
                        "'order':['/class','/field']" +
            "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

    @Test
    public void beanWithMapLong()
    {
        TestBeans.BeanWithMapLong bean = new TestBeans.BeanWithMapLong(ImmutableMap.of(1L,2L,3L,4L));
        assertEquals("{" +
                "'values':{" +
                "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithMapLong'," +
                "'field':[{'1':2},{'3':4}]}," +
                "'meta':{" +
                "'/class':{'type':'Class','readOnly':true}," +
                "'/field':{'type':'Map','readOnly':true}}," +
                "'order':['/class','/field']" +
                "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

    @Test
    public void beanWithList()
    {
        TestBeans.BeanWithList bean = new TestBeans.BeanWithList(ImmutableList.of("item1", "item2"));
        assertEquals("{" +
                        "'values':{" +
                            "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithList'," +
                            "'field':['item1','item2']}," +
                        "'meta':{" +
                            "'/class':{'type':'Class','readOnly':true}," +
                            "'/field':{'type':'List','readOnly':true}}," +
                        "'order':['/class','/field']" +
                "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

    @Test
    public void beanWithListLong()
    {
        TestBeans.BeanWithListLong bean = new TestBeans.BeanWithListLong(ImmutableList.of(1L, 2L));
        assertEquals("{" +
                "'values':{" +
                "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithListLong'," +
                "'field':[1,2]}," +
                "'meta':{" +
                "'/class':{'type':'Class','readOnly':true}," +
                "'/field':{'type':'List','readOnly':true}}," +
                "'order':['/class','/field']" +
                "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }
}