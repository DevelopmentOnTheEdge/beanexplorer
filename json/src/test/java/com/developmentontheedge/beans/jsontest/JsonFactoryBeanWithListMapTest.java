package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.json.JsonFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.assertEquals;

public class JsonFactoryBeanWithListMapTest
{
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

    @Test
    public void beanWithListObjValues()
    {
        TestBeans.BeanWithListObj bean = new TestBeans.BeanWithListObj(ImmutableList.of(
                new TestBeans.InnerBeanClass("test1"),
                new TestBeans.InnerBeanClass("test2")
        ));
        assertEquals(
                "{" +
                    "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithListObj'," +
                    "'field':[" +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'test1'}," +
                        "{'class':'class com.developmentontheedge.beans.jsontest.TestBeans$InnerBeanClass','name':'test2'}" +
                    "]" +
                "}", oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void beanWithListObjValuesNull()
    {
        TestBeans.BeanWithListObj bean = new TestBeans.BeanWithListObj(null);
        assertEquals(
                "{" +
                        "'class':'class com.developmentontheedge.beans.jsontest.TestBeans$BeanWithListObj'," +
                        "'field':null" +
                        "}", oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    @Ignore
    public void beanWithListObjMeta()
    {
        TestBeans.BeanWithListObj bean = new TestBeans.BeanWithListObj(ImmutableList.of(
                new TestBeans.InnerBeanClass("test1"),
                new TestBeans.InnerBeanClass("test2")
        ));
        assertEquals("{" +
                "'/class':{'type':'Class','readOnly':true}," +
                "'/field':{'type':'List','readOnly':true}," +
                "'/field/class':{'type':'Class','readOnly':true}," +
                "'/field/name':{'type':'String','readOnly':true}" +
                "}", oneQuotes(JsonFactory.beanMeta(bean).toString()));
    }
}