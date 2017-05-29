package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.Benchmark;


public class JsonFactoryTestBeanSimple {

    public static class SimpleBean
    {
        private String name;
        private int number;

        SimpleBean(String name, int number) {

            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }
    }

    @Benchmark
    public void simpleJsonFactory() {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = JsonFactory.beanValues(bean).toString();
    }

    @Benchmark
    public void simpleGson() {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = new Gson().toJson(bean);
    }

    @Benchmark
    public void simpleJackson() throws JsonProcessingException {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = new ObjectMapper().writeValueAsString(bean);
    }

}
