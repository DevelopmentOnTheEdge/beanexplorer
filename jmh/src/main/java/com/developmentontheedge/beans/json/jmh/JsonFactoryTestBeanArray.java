package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.Random;

public class JsonFactoryTestBeanArray {

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
    public void simpleJsonFactory() {
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
    public void simpleGson() {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});

        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = new Gson().toJson(bean);
    }

    @Benchmark
    public void simpleJackson() throws JsonProcessingException {
        Random random = new Random(0);
        SimpleBean[] simpleBeans = new SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            simpleBeans[i] = new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(),random.nextInt()});
        }
        BeanWithInnerClass bean = new BeanWithInnerClass(new SimpleBean("bean", random.nextInt(), new long[]{random.nextInt(), random.nextInt()}),
                simpleBeans);
        String json = new ObjectMapper().writeValueAsString(bean);
    }

}
