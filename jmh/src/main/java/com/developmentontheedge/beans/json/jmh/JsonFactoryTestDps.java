package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.Benchmark;


public class JsonFactoryTestDps {

    public static class SimpleBean
    {
        private String name;
        private Long number;

        SimpleBean(String name, Long number) {

            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public Long getNumber() {
            return number;
        }
    }

    @Benchmark
    public void simpleJsonFactory() throws InterruptedException{
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", "Name", String.class, "testName"));
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));

        String json = JsonFactory.dpsValues(dps).toString();
    }

    @Benchmark
    public void simpleGsonForSimilarBean() throws InterruptedException {
        SimpleBean bean = new SimpleBean("testName", 1L);

        String json = new Gson().toJson(bean);
    }

    @Benchmark
    public void simpleJacksonForSimilarBean() throws JsonProcessingException, InterruptedException {
        SimpleBean bean = new SimpleBean("testName", 1L);

        String json = new ObjectMapper().writeValueAsString(bean);
    }

}
