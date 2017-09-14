package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.jsontest.testbeans.OrientationEditor;
import com.developmentontheedge.beans.test.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagEditorSupportTest extends TestUtils
{
    public static class SimpleBeanWithTags
    {
        private String orientation;

        public SimpleBeanWithTags(String orientation)
        {
            this.orientation = orientation;
        }

        public String getOrientation()
        {
            return orientation;
        }

        public void setOrientation(String orientation)
        {
            this.orientation = orientation;
        }
    }

    public static class SimpleBeanWithTagsBeanInfo extends BeanInfoEx
    {
        public SimpleBeanWithTagsBeanInfo()
        {
            super(SimpleBeanWithTags.class);
        }

        public void initProperties() throws Exception
        {
            add("orientation", OrientationEditor.class);
        }
    }

    @Test
    public void test() throws Exception
    {
        assertEquals("{" +
                "'values':{'orientation':'Horizontal'}," +
                "'meta':{'/orientation':{'displayName':'orientation','description':'orientation'," +
                "'dictionary':[[0,'Vertical'],[1,'Horizontal']],'type':'code-string'}}," +
                "'order':['/orientation']" +
            "}", oneQuotes(JsonFactory.bean(new SimpleBeanWithTags(OrientationEditor.Horizontal)).toString()));
    }
}
