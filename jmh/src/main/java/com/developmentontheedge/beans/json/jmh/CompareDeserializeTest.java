package com.developmentontheedge.beans.json.jmh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//TODO
public class CompareDeserializeTest
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();


    public void testSimpleBean() throws Exception
    {
        test("{\"arr\":[1,2,3],\"name\":\"bean\",\"number\":5}", SimpleBean.class);
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

    public void testList() throws Exception
    {
        test("{\"field\":[\"item1\",\"item2\"]}", BeanWithList.class);
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

    public void testMap() throws Exception
    {
        test("{\"field\":{\"key\":\"value\",\"key2\":\"value2\"}}", BeanWithMap.class);
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

//        assertEquals(jackson, gson);
//        assertEquals(gson, jsonb);
    }

    public static class BeanWithMap
    {
        Map<String, String> field;

        public BeanWithMap() {}

        BeanWithMap(Map<String, String> field)
        {
            this.field = field;
        }

        public Map<String, String> getField()
        {
            return field;
        }

        public void setField(Map<String, String> field)
        {
            this.field = field;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof BeanWithMap)) return false;

            BeanWithMap that = (BeanWithMap) o;

            if (field != null ? !field.equals(that.field) : that.field != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            return field != null ? field.hashCode() : 0;
        }
    }

    public static class BeanWithList
    {
        private List<String> field;

        public BeanWithList(){}

        public BeanWithList(List<String> field)
        {
            this.field = field;
        }

        public List<String> getField()
        {
            return field;
        }

        public void setField(List<String> field)
        {
            this.field = field;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BeanWithList)) return false;

            BeanWithList that = (BeanWithList) o;

            if (field != null ? !field.equals(that.field) : that.field != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return field != null ? field.hashCode() : 0;
        }
    }

    public static class SimpleBean
    {
        private long arr[];
        private String name;
        private int number;

        public SimpleBean() {}

        SimpleBean(String name, int number, long[] arr) {

            this.name = name;
            this.number = number;
            this.arr = arr;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        public long[] getArr() {
            return arr;
        }

        public void setArr(long[] arr)
        {
            this.arr = arr;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setNumber(int number)
        {
            this.number = number;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof SimpleBean)) return false;

            SimpleBean that = (SimpleBean) o;

            if (number != that.number) return false;
            if (!Arrays.equals(arr, that.arr)) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = arr != null ? Arrays.hashCode(arr) : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + number;
            return result;
        }
    }
}