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

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.awt.*;
import java.util.Arrays;

import static org.junit.Assert.*;


public class TestBeanWithBeanInfoEx extends TestUtils
{
    public static class TestBean
    {
        private String str;
        //private DataElementPath input, output;
        private Color color = Color.BLUE;
        private String select;
        private Interval interval;

        @PropertyName("String")
        @PropertyDescription("Test string property")
        public String getStr()
        {
            return str;
        }
        public void setStr(String str)
        {
            this.str = str;
        }
//        public DataElementPath getInput()
//        {
//            return input;
//        }
//        public void setInput(DataElementPath input)
//        {
//            this.input = input;
//        }
//        public DataElementPath getOutput()
//        {
//            return output;
//        }
//        public void setOutput(DataElementPath output)
//        {
//            this.output = output;
//        }

        public Color getColor()
        {
            return color;
        }

        public void setColor(Color color)
        {
            this.color = color;
        }
        public String getSelect()
        {
            return select;
        }
        public void setSelect(String select)
        {
            this.select = select;
        }
        public Interval getInterval()
        {
            return interval;
        }
        public void setInterval(Interval interval)
        {
            this.interval = interval;
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

    public static class TestIntervalSelector extends GenericComboBoxEditor
    {
        private static Interval[] values = new Interval[] {
                new Interval(0,100),
                new Interval(100,200),
                new Interval(200,300),
        };

        @Override
        protected Object[] getAvailableValues()
        {
            return values;
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
            add("str");
            findPropertyDescriptor("str").setExpert(true);
//            property( "input" ).inputElement( TableDataCollection.class ).add();
//            property( "output" ).outputElement( Diagram.class ).add();
            add("color", ColorEditor.class);
            add("select", TestSelector.class);
            property( "interval" ).simple().editor( TestIntervalSelector.class ).add();
        }
    }

    @Test
    public void testGetModelAsJSON()
    {
        new TestBeanBeanInfo();

        TestBean bean = new TestBean();
        bean.setStr("string value");
//        bean.setInput(DataElementPath.create("test/path"));
//        bean.setOutput(DataElementPath.create("test/path output"));
        bean.setColor(Color.BLACK);
        bean.setSelect("one");
        bean.setInterval(new Interval(0,100));

        ComponentModel model = ComponentFactory.getModel(bean);
        JsonObject json = JsonFactory.bean(bean, FieldMap.ALL, Property.SHOW_EXPERT);
        assertNotNull(json);

        assertEquals(model.getPropertyCount(), json.getJsonArray("order").size());

        assertEquals("{" +
                "'values':{" +
                    "'str':'string value'," +
                    "'color':[0,0,0]," +
                    "'select':'one'," +
                    "'interval':'(0,100)'}," +
                "'meta':{" +
                    "'/str':{'displayName':'String','description':'Test string property','readOnly':false,'type':'code-string'}," +
                    "'/color':{'displayName':'color','description':'Color property','readOnly':false,'type':'color-selector'}," +
                    "'/select':{'displayName':'select','description':'select','readOnly':false,'dictionary':[['one','one'],['two','two']]," +
                        "'type':'code-string'}," +
                    "'/interval':{'displayName':'interval','description':'interval','readOnly':false,'dictionary':[['(0,100)','(0,100)'],['(100,200)','(100,200)'],['(200,300)','(200,300)']]," +
                        "'type':'code-string'}}," +
                "'order':['/str','/color','/select','/interval']" +
            "}", oneQuotes(json.toString()));

//
//        property = json.getJSONObject(1);
//        assertEquals("input", property.getString("name"));
//        assertEquals(TableDataCollection.class.getName(), property.getString("elementClass"));
//        assertTrue(property.getBoolean("elementMustExist"));
//        assertFalse(property.getBoolean("canBeNull"));
//        assertFalse(property.getBoolean("multiSelect"));
//        assertEquals("test/path", property.getString("value"));
//        assertEquals("data-element-path", property.getString("type"));
//
//        property = json.getJSONObject(2);
//        assertEquals("output", property.getString("name"));
//        assertEquals(Diagram.class.getName(), property.getString("elementClass"));
//        assertFalse(property.getBoolean("elementMustExist"));
//        assertFalse(property.getBoolean("canBeNull"));
//        assertFalse(property.getBoolean("multiSelect"));
//        assertEquals("test/path output", property.getString("value"));
//        assertEquals("data-element-path", property.getString("type"));
//
        // TODO: test more types
    }
}
