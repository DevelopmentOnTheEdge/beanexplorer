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


public class JsonFactoryTestBeanSimple
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

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
    public void jsonFactory() {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = JsonFactory.beanValues(bean).toString();
    }

    @Benchmark
    public void jsonB() {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = jsonb.toJson(bean);
    }

    @Benchmark
    public void gson() {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = gson.toJson(bean);
    }

    @Benchmark
    public void jackson() throws JsonProcessingException {
        SimpleBean bean = new SimpleBean("bean", 1);
        String json = jackson.writeValueAsString(bean);
    }

}
