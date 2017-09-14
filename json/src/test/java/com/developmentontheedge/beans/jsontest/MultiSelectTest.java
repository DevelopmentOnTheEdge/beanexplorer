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
import org.junit.Test;

import javax.json.JsonObject;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class MultiSelectTest extends TestUtils
{
    public static class TestBean
    {
        private Color[] color;

        public Color[] getColor()
        {
            return color;
        }

        public void setColor(Color[] color)
        {
            this.color = color;
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
            add("color", ColorEditor.class);
        }
    }

    @Test
    public void testGetModelAsJSON()
    {
        TestBean bean = new TestBean();
//        bean.setInput(DataElementPath.create("test/path"));
//        bean.setOutput(DataElementPath.create("test/path output"));
        bean.setColor(new Color[] {Color.RED, Color.CYAN});

        ComponentModel model = ComponentFactory.getModel(bean);
        JsonObject json = JsonFactory.bean(bean, FieldMap.ALL, Property.SHOW_EXPERT);
        assertNotNull(json);

        assertEquals(model.getPropertyCount(), json.getJsonArray("order").size());

//        assertEquals("{" +
//                "'values':{'color':[[255,0,0],[0,255,255]]}," +
//                "'meta':{'/color':{'displayName':'color','description':'color','type':'Color','multipleSelectionList':true}}," +
//                "'order':['/color']" +
//            "}", oneQuotes(json.toString()));

        assertEquals("{" +
                        "'values':{'color':[[255,0,0],[0,255,255]]}," +
                        "'meta':{" +
                            "'/color':{'displayName':'color','description':'color','type':'Color','collection':true,'multipleSelectionList':true}," +
                            "'/color/[0]':{'displayName':'[0]','description':'Color property','type':'Color'}," +
                            "'/color/[1]':{'displayName':'[1]','description':'Color property','type':'Color'}}," +
                        "'order':['/color']}"
                , oneQuotes(json.toString()));
    }

}
