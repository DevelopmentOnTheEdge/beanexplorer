package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.editors.ColorEditor;
import com.developmentontheedge.beans.editors.GenericComboBoxEditor;
import com.developmentontheedge.beans.editors.StringTagEditor;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.jsontest.testbeans.Interval;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.test.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonObject;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class BeanTest extends TestUtils
{
    public static class TestBean
    {
        private long count;
        private String select;

        public TestBean(long count, String select)
        {
            this.count = count;
            this.select = select;
        }

        public long getCount()
        {
            return count;
        }

        public void setCount(long count)
        {
            this.count = count;
        }

        public String getSelect()
        {
            return select;
        }

        public void setSelect(String select)
        {
            this.select = select;
        }
    }

    public static class TestSelector extends StringTagEditor
    {
        @Override
        public String[] getTags()
        {
            return new String[] {"one", "two"};
        }
    }

    public static class TestBeanBeanInfo extends BeanInfoEx
    {
        public TestBeanBeanInfo()
        {
            super(TestBean.class);
        }

        @Override
        public void initProperties() throws Exception
        {
            add("count");
            add("select", TestSelector.class);
        }
    }

    @Test
    public void testGetModelAsJSON()
    {
        TestBean bean = new TestBean(100L, "one");

        assertEquals("{" +
                "'values':{'count':100,'select':'one'}," +
                "'meta':{" +
                    "'/count':{'displayName':'count','description':'count','type':'Long'}," +
                    "'/select':{'displayName':'select','description':'select','dictionary':[['one','one'],['two','two']]}}," +
                "'order':['/count','/select']" +
            "}", oneQuotes(JsonFactory.bean(bean)));

        //
        //TestBean o = JsonFactory.setBeanValues(TestBean.class, "{'count':200,'select':'two'}");
    }

    @Test
    public void test() throws Exception
    {
        TestBean bean = new TestBean(100L, "one");

        JsonFactory.setBeanValues(bean, doubleQuotes("{'count':200,'select':'two'}"));

        assertEquals(200L, bean.getCount());
        assertEquals("two", bean.getSelect());
    }

}
