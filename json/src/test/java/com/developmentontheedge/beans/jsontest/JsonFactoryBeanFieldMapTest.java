package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.model.FieldMap;
import org.junit.Test;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanFieldMapTest
{
    private TestBeans.InnerBeanClass bean = new TestBeans.InnerBeanClass("test");
    private FieldMap fieldMap = new FieldMap("notContainField");

    @Test
    public void bean() throws Exception
    {
        assertEquals("{'values':{},'meta':{},'order':[]}", oneQuotes(JsonFactory.bean(bean, fieldMap).toString()));
    }

    @Test
    public void beanValues() throws Exception
    {
        assertEquals("{}", oneQuotes(JsonFactory.beanValues(bean, fieldMap).toString()));
    }

    @Test
    public void beanMeta() throws Exception
    {
        assertEquals("{}", oneQuotes(JsonFactory.beanMeta(bean, fieldMap).toString()));
    }

    @Test
    public void beanOrder() throws Exception
    {
        assertEquals("[]", oneQuotes(JsonFactory.beanOrder(bean, fieldMap).toString()));
    }

}