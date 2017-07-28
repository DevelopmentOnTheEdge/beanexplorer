package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.Assert.*;

public class CompareSerializeTest
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

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

    void test(Object o) throws Exception
    {
        String gsonJson = gson.toJson(o);
        String jacksonJson = jackson.writeValueAsString(o);
        String jsonbJson = jsonb.toJson(o);
        String jfJson = JsonFactory.beanValues(o).toString();

//        System.out.println("jack:  "+jackson);
//        System.out.println("gson:  "+gson);
//        System.out.println("jsonb: "+jsonb);
        System.out.println("json:  "+jfJson);
//        System.out.println();

        assertEquals(jacksonJson, gsonJson);
        assertEquals(gsonJson, jsonbJson);
        assertEquals(jsonbJson, jfJson);
    }


}