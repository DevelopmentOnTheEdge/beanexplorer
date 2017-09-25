package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.StringTagEditor;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.test.TestUtils;
import org.junit.Test;


import static org.junit.Assert.assertEquals;


public class BeanTest extends TestUtils
{
    public static class TestBean
    {
        private long count;
        private String select;
        private boolean status;

        public TestBean(long count, String select, boolean status)
        {
            this.count = count;
            this.select = select;
            this.status = status;
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

        public boolean isStatus()
        {
            return status;
        }

        public void setStatus(boolean status)
        {
            this.status = status;
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
            add("status");
        }
    }

    @Test
    public void testGetModelAsJSON()
    {
        TestBean bean = new TestBean(100L, "one", true);

        assertEquals("{" +
                "'values':{'count':100,'select':'one','status':true}," +
                "'meta':{" +
                    "'/count':{'displayName':'count','description':'count','type':'Long'}," +
                    "'/select':{'displayName':'select','description':'select','dictionary':[['one','one'],['two','two']]}," +
                    "'/status':{'displayName':'status','description':'status','type':'Boolean'}}," +
                "'order':['/count','/select','/status']" +
            "}", oneQuotes(JsonFactory.bean(bean)));
    }

    @Test
    public void test() throws Exception
    {
        TestBean bean = new TestBean(100L, "one", true);

        JsonFactory.setBeanValues(bean, doubleQuotes("{'count':200,'select':'two','status':false}"));

        assertEquals(200L, bean.getCount());
        assertEquals("two", bean.getSelect());
        assertEquals(false, bean.isStatus());
    }

}
