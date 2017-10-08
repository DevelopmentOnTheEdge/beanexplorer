package com.developmentontheedge.beans.jsontest.beans;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.StringTagEditor;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.json.JsonSerializable;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.test.TestUtils;
import org.junit.Test;


import static org.junit.Assert.assertEquals;


public class JsonSerializableTest extends TestUtils
{
//    public static class InnerBean implements JsonSerializable
//    {
//        String value;
//        @Override
//        public Object getJsonValues()
//        {
//            return value;
//        }
//
//        @Override
//        public void setJsonValues(Object jsonValues)
//        {
//
//        }
//
//        @Override
//        public Object getJsonMeta()
//        {
//            return null;
//        }
//    }
//
//    public static class TestBean
//    {
//        private long count;
//    }
//
//    public static class TestBeanBeanInfo extends BeanInfoEx
//    {
//        public TestBeanBeanInfo()
//        {
//            super(TestBean.class);
//        }
//
//        @Override
//        public void initProperties() throws Exception
//        {
//            add("count");
//        }
//    }
//
//    @Test
//    public void testGetModelAsJSON()
//    {
//        TestBean bean = new TestBean(100L, "one", true);
//
//        assertEquals("{" +
//                "'values':{'count':100,'select':'one','status':true}," +
//                "'meta':{" +
//                    "'/count':{'displayName':'count','description':'count','type':'Long'}," +
//                    "'/select':{'displayName':'select','description':'select','dictionary':[['one','one'],['two','two']]}," +
//                    "'/status':{'displayName':'status','description':'status','type':'Boolean'}}," +
//                "'order':['/count','/select','/status']" +
//            "}", oneQuotes(JsonFactory.bean(bean)));
//    }
//
//    @Test
//    public void testExpert() throws Exception
//    {
//        TestBean bean = new TestBean(100L, "one", true);
//
//        JsonFactory.setBeanValues(bean, doubleQuotes("{'count':200,'select':'two','status':false}"), FieldMap.ALL, Property.SHOW_EXPERT);
//
//        assertEquals(200L, bean.getCount());
//        assertEquals("two", bean.getSelect());
//        assertEquals(false, bean.isStatus());
//    }

}
