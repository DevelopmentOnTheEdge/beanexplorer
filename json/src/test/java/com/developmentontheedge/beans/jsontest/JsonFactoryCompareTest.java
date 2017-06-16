package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonObject;

import static org.junit.Assert.*;

public class JsonFactoryCompareTest
{

    @Test
    public void testSimpleBean() throws Exception
    {
        test(new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3}));
    }

    @Test
    public void testBeanWithInnerClass() throws Exception
    {
        test(new TestBeans.BeanWithInnerClass(
                new TestBeans.InnerBeanClass("foo"),
                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
        ));
    }

    @Test
    public void testList() throws Exception
    {
        test(new TestBeans.BeanWithList(ImmutableList.of("item1", "item2")));
    }

    @Test
    public void testListOfObject() throws Exception
    {
        test(new TestBeans.BeanWithListObj(ImmutableList.of(
                new TestBeans.InnerBeanClass("test1"),
                new TestBeans.InnerBeanClass("test2")
        )));
    }

    @Test
    public void testListWithNull() throws Exception
    {
        test(new TestBeans.BeanWithListObj(null));
    }

    @Test
    public void testMap() throws Exception
    {
        test(new TestBeans.BeanWithMap(ImmutableMap.of("key","value", "key2","value2")));
    }


    @Test
    public void testInnerJson() throws Exception
    {
        TestBeans.InnerBeanClass bean = new TestBeans.InnerBeanClass("foo");
        JsonObject jsonObject = JsonFactory.beanValues(bean);
        JsonElement jsonElement = new GsonBuilder().serializeNulls().create().toJsonTree(bean);

        assertEquals(new Gson().toJson(new TestBeans.TypedResponse("form", jsonElement)),
                JsonFactory.beanValues(new TestBeans.TypedResponse("form", jsonObject)).toString());
    }

    void test(Object o){
        String gson = new GsonBuilder().serializeNulls().create().toJson(o);
        String jackson = "";
        try
        {
            jackson = new ObjectMapper().writeValueAsString(o);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        String json = JsonFactory.beanValues(o).toString();

        System.out.println("jack: "+jackson);
        System.out.println("gson: "+gson);
        System.out.println("json: "+json);
        System.out.println();

        assertEquals(jackson, gson);
        assertEquals(gson, json);
    }


}