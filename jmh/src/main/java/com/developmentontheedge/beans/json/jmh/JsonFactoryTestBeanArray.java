package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openjdk.jmh.annotations.Benchmark;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Random;

public class JsonFactoryTestBeanArray
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

    public static class BeanWithInnerClass
    {
        private SimpleBean field1;
        private SimpleBean arr[];

        BeanWithInnerClass(SimpleBean field1, SimpleBean[] arr) {
            this.field1 = field1;
            this.arr = arr;
        }

        public SimpleBean getField1() {return field1;}
        public SimpleBean[] getArr() {return arr;}
    }

    public static class SimpleBean
    {
        private String name;
        private int number;
        private long arr[];

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
    }

    private static final int simpleBeansCount = 100;

    @Benchmark
    public void jsonFactory() {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});

        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = JsonFactory.beanValues(bean).toString();
    }

    @Benchmark
    public void jsonB() {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});

        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = jsonb.toJson(bean);
    }

    @Benchmark
    public void gson() {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});

        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = gson.toJson(bean);
    }

    @Benchmark
    public void jackson() throws JsonProcessingException {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});
        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = jackson.writeValueAsString(bean);
    }

}
