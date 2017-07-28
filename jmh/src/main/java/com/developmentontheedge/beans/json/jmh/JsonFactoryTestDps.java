package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openjdk.jmh.annotations.Benchmark;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

public class JsonFactoryTestDps
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

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
    public void jsonFactory() throws InterruptedException{
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", "Name", String.class, "testName"));
        dps.add(new DynamicProperty("number", "Number", Long.class, 1L));

        String json = JsonFactory.dpsValues(dps).toString();
    }

    @Benchmark
    public void jsonBForSimilarBean() throws InterruptedException {
        SimpleBean bean = new SimpleBean("testName", 1L);

        String json = jsonb.toJson(bean);
    }

    @Benchmark
    public void gsonForSimilarBean() throws InterruptedException {
        SimpleBean bean = new SimpleBean("testName", 1L);

        String json = gson.toJson(bean);
    }

    @Benchmark
    public void jacksonForSimilarBean() throws JsonProcessingException, InterruptedException {
        SimpleBean bean = new SimpleBean("testName", 1L);

        String json = jackson.writeValueAsString(bean);
    }

}
