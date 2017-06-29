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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class JsonFactoryTestBeanList
{
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final ObjectMapper jackson = new ObjectMapper();

    public static class BeanWithListObj
    {
        private List<InnerBeanClass> field;

        BeanWithListObj(List<InnerBeanClass> field)
        {
            this.field = field;
        }

        public List<InnerBeanClass> getField()
        {
            return field;
        }
    }

    public static class InnerBeanClass
    {
        String name;
        InnerBeanClass(String name) {this.name = name;}
        public String getName() {return name;}
    }

    private static final int simpleBeansCount = 100;

    @Benchmark
    public void jsonFactory() {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = JsonFactory.beanValues(new BeanWithListObj(innerBeanClasses)).toString();
    }

    @Benchmark
    public void jsonB() {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = jsonb.toJson(new BeanWithListObj(innerBeanClasses));
    }

    @Benchmark
    public void gson() {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = gson.toJson(new BeanWithListObj(innerBeanClasses));
    }

    @Benchmark
    public void jackson() throws JsonProcessingException {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = jackson.writeValueAsString(new BeanWithListObj(innerBeanClasses));
    }

}
