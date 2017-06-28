package com.developmentontheedge.beans.jsontest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.Assert.assertEquals;

public class CompareDeserializeTest
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

    @Test
    public void testSimpleBean() throws Exception
    {
        test("{\"arr\":[1,2,3],\"name\":\"bean\",\"number\":5}", TestBeans.SimpleBean.class);
    }
//
//    @Test
//    public void testBeanWithInnerClass() throws Exception
//    {
//        test(new TestBeans.BeanWithInnerClass(
//                new TestBeans.InnerBeanClass("foo"),
//                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
//        ));
//    }
//
    @Test
    public void testList() throws Exception
    {
        test("{\"field\":[\"item1\",\"item2\"]}", TestBeans.BeanWithList.class);
    }

//    @Test
//    public void testListOfObject() throws Exception
//    {
//        test(new TestBeans.BeanWithListObj(ImmutableList.of(
//                new TestBeans.InnerBeanClass("test1"),
//                new TestBeans.InnerBeanClass("test2")
//        )));
//    }
//
//    @Test
//    public void testListWithNull() throws Exception
//    {
//        test(new TestBeans.BeanWithListObj(null));
//    }
//
    @Test
    public void testMap() throws Exception
    {
        test("{\"field\":{\"key\":\"value\",\"key2\":\"value2\"}}", TestBeans.BeanWithMap.class);
    }
//
//
//    @Test
//    public void testInnerJson() throws Exception
//    {
//        TestBeans.InnerBeanClass bean = new TestBeans.InnerBeanClass("foo");
//        JsonObject jsonObject = JsonFactory.beanValues(bean);
//        JsonElement jsonElement = new GsonBuilder().serializeNulls().create().toJsonTree(bean);
//
//        assertEquals(new Gson().toJson(new TestBeans.TypedResponse("form", jsonElement)),
//                JsonFactory.beanValues(new TestBeans.TypedResponse("form", jsonObject)).toString());
//    }
//
    void test(String json, Class<?> klass) throws Exception
    {
        Object jsonb = CompareDeserializeTest.jsonb.fromJson(json, klass);
        Object gson = CompareDeserializeTest.gson.fromJson(json, klass);
        Object jackson = CompareDeserializeTest.jackson.readValue(json, klass);

        assertEquals(jackson, gson);
        assertEquals(gson, jsonb);
    }


}