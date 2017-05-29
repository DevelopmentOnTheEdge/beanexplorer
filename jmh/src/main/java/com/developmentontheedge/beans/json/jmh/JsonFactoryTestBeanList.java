package com.developmentontheedge.beans.json.jmh;

import com.developmentontheedge.beans.json.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class JsonFactoryTestBeanList {

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
    public void simpleJsonFactory() {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = JsonFactory.beanValues(new BeanWithListObj(innerBeanClasses)).toString();
    }

    @Benchmark
    public void simpleGson() {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = new Gson().toJson(new BeanWithListObj(innerBeanClasses));
    }

    @Benchmark
    public void simpleJackson() throws JsonProcessingException {
        Random random = new Random(0);
        List<InnerBeanClass> innerBeanClasses = new ArrayList<>();
        JsonFactoryTestBeanArray.SimpleBean[] simpleBeans = new JsonFactoryTestBeanArray.SimpleBean[simpleBeansCount];
        for (int i = 0; i < simpleBeansCount; i++) {
            innerBeanClasses.add(new InnerBeanClass("" + random.nextInt()));
        }
        String json = new ObjectMapper().writeValueAsString(new BeanWithListObj(innerBeanClasses));
    }

}
