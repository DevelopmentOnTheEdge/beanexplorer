package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.json.JsonFactoryCopy;
import com.developmentontheedge.beans.jsontest.TestBeans.BeanWithInnerClass;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonArray;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanTest
{
    @Test
    public void simpleBeanValues() throws Exception
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'arr':[1,2,3]," +
                        "'name':'bean'," +
                        "'number':5" +
                "}", oneQuotes(JsonFactory.beanValues(bean).toString()));
    }

    @Test
    public void beanWithInnerClass() throws Exception
    {
        BeanWithInnerClass bean = new BeanWithInnerClass(
                new TestBeans.InnerBeanClass("foo"),
                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
        );
        assertEquals("{'arr':[" +
                        "{'name':'foo1'}," +
                        "{'name':'foo2'}" +
                "]," +
                "'field1':{'name':'foo'}}",
            oneQuotes(JsonFactory.beanValues(bean).toString()));
    }
//
//    @Test
//    public void beanWithInnerClassAll() throws Exception
//    {
//        BeanWithInnerClass bean = new BeanWithInnerClass(
//                new TestBeans.InnerBeanClass("foo"),
//                new TestBeans.InnerBeanClass[]{new TestBeans.InnerBeanClass("foo1"),new TestBeans.InnerBeanClass("foo2")}
//        );
//        assertEquals("{'values':{'arr':[{'name':'foo1'},{'name':'foo2'}],'field1':{'name':'foo'}}," +
//                        "'meta':{" +
//                            "'/arr':{'type':'InnerBeanClass[]','readOnly':true}," +
//                            "'/arr/[0]/name':{'type':'String'}," +
//                            "'/arr/[1]/name':{'type':'String'}," +
//                            "'/field1':{'type':'InnerBeanClass','readOnly':true}," +
//                            "'/field1/name':{'type':'String','readOnly':true}}," +
//                        "'order':['/arr','/field1']}",
//                oneQuotes(JsonFactory.bean(bean).toString()));
//    }

    @Test
    @Ignore
    public void simpleBeanMeta()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("{" +
                        "'/arr':{'type':'long[]'}," +
                        "'/name':{'type':'String'}," +
                        "'/number':{'type':'Integer'}}",
                oneQuotes(JsonFactory.beanMeta(bean).toString()));
    }

    @Test
    @Ignore
    public void simpleBeanMetaCopy() throws Exception
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        ComponentModel model = ComponentFactory.getModel(bean);
        JsonArray json = JsonFactoryCopy.getModelAsJSON(model).build();
        assertEquals("{" +
                        "'/arr':{'type':'long[]'}," +
                        "'/name':{'type':'String'}," +
                        "'/number':{'type':'Integer'}}",
                oneQuotes(json.toString()));
    }

    @Test
    public void simpleBeanOrder()
    {
        TestBeans.SimpleBean bean = new TestBeans.SimpleBean("bean", 5, new long[]{1,2,3});
        assertEquals("['/arr','/name','/number']",
                oneQuotes(JsonFactory.beanOrder(bean).toString()));
    }

    @Test
    @Ignore
    public void beanWithDps()
    {
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class, ""));
        TestBeans.FormPresentation bean = new TestBeans.FormPresentation("TestBean", dps);
        assertEquals("{" +
                        "'values':{" +
                            "'dps':{'name':''},'title':'TestBean'}," +
                        "'meta':{" +
                            "'/dps':{'type':'DynamicPropertySetSupport'}," +
                            "'/dps/name':{'type':'String'}," +
                            "'/title':{'type':'String','readOnly':true}}," +
                        "'order':['/dps','/dps/name','/title']" +
            "}", oneQuotes(JsonFactory.bean(bean).toString()));
    }

    @Test
    public void notShowNullInJson() throws Exception
    {
        assertEquals("{'number':0}"
                , oneQuotes(JsonFactory.jsonb.toJson(new TestBeans.SimpleBean(null, 0, null))));
    }

//    @Test
//    public void beanInt()
//    {
//        assertEquals("{"
//                , oneQuotes(JsonFactory.bean(1).toString()));
//    }
//
//    @Test
//    public void beanString()
//    {
//        assertEquals("{"
//                , oneQuotes(JsonFactory.bean("string").toString()));
//    }
//
//    @Test
//    public void beanEnum()
//    {
//        assertEquals("{"
//                , oneQuotes(JsonFactory.bean(TestBeans.EnumClass.test).toString()));
//    }

//    not support in java beans
//    @Test
//    public void beanWithEnum()
//    {
//        assertEquals("{" +
//                        "'values':{" +
//                            "'status':'test2'}," +
//                        "'meta':{" +
//                            "'/status':{'type':'EnumClass','readOnly':true}" +
//                        "}," +
//                        "'order':['/status']}"
//                , oneQuotes(JsonFactory.bean(new TestBeans.ClassWithEnum(TestBeans.EnumClass.test2)).toString()));
//    }

}