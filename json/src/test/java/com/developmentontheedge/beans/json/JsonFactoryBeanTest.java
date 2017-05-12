package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;

import static com.developmentontheedge.beans.json.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryBeanTest
{
    public static class TestBean
    {
        private String str;
        //private DataElementPath input, output;
        private Color color = Color.BLUE;
        private String select;
        //private Interval interval;

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
        //public DataElementPath getInput()
//        {
//            return input;
//        }
//        public void setInput(DataElementPath input)
//        {
//            this.input = input;
//        }
//        //public DataElementPath getOutput()
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
//        public Interval getInterval()
//        {
//            return interval;
//        }
//        public void setInterval(Interval interval)
//        {
//            this.interval = interval;
//        }
    }

    @Test
    public void testGetModelAsJson() throws Exception
    {
        TestBean bean = new TestBean();
        bean.setStr("string value");
        bean.setColor(Color.BLACK);
        bean.setSelect("one");

        ComponentModel model = ComponentFactory.getModel(bean);
        JsonArray json = JsonFactory.getModelAsJson(model).build();
        assertNotNull(json);
        assertEquals(model.getPropertyCount(), json.size());
        assertEquals("[" +
            "{'name':'class','displayName':'class','description':'class','readOnly':true,'type':'code-string','value':'class com.developmentontheedge.beans.json.JsonFactoryBeanTest$TestBean'}," +
            "{'name':'color','displayName':'color','description':'Color property','readOnly':false,'type':'code-string','value':'java.awt.Color[r=0,g=0,b=0]'}," +
            "{'name':'select','displayName':'select','description':'select','readOnly':false,'type':'code-string','value':'one'}," +
            "{'name':'str','displayName':'str','description':'str','readOnly':false,'type':'code-string','value':'string value'}]", oneQuotes(json.toString()));

//        json = JsonFactory.getModelAsJson(model, FieldMap.ALL, Property.SHOW_EXPERT);
//        assertNotNull(json);
//        assertEquals("", json.build().toString());

        JsonObject property = json.getJsonObject(3);
        assertEquals("str", property.getString("name"));
        //assertEquals("String", property.getString("displayName"));
        //assertEquals("Test string property", property.getString("description"));
        assertEquals("string value", property.getString("value"));
        assertFalse(property.getBoolean("readOnly"));
        assertEquals("code-string", property.getString("type"));
//
//        property = json.getJsonObject(1);
//        assertEquals("input", property.getString("name"));
//        assertEquals(TableDataCollection.class.getName(), property.getString("elementClass"));
//        assertTrue(property.getBoolean("elementMustExist"));
//        assertFalse(property.getBoolean("canBeNull"));
//        assertFalse(property.getBoolean("multiSelect"));
//        assertEquals("test/path", property.getString("value"));
//        assertEquals("data-element-path", property.getString("type"));
//
//        property = json.getJsonObject(2);
//        assertEquals("output", property.getString("name"));
//        assertEquals(Diagram.class.getName(), property.getString("elementClass"));
//        assertFalse(property.getBoolean("elementMustExist"));
//        assertFalse(property.getBoolean("canBeNull"));
//        assertFalse(property.getBoolean("multiSelect"));
//        assertEquals("test/path output", property.getString("value"));
//        assertEquals("data-element-path", property.getString("type"));
//
        property = json.getJsonObject(1);
        assertEquals("color", property.getString("name"));
        // TODO: something wrong with color; need to investigate
        //assertEquals("color-selector", property.getString("type"));
        //assertEquals("[0,0,0]", property.getJsonArray("value").get(0));

        property = json.getJsonObject(2);
        assertEquals("select", property.getString("name"));
        assertEquals("code-string", property.getString("type"));
        assertEquals("one", property.getString("value"));
        //assertEquals("[[\"one\",\"one\"],[\"two\",\"two\"]]", property.getJsonArray("dictionary").toString());
//
//        property = json.getJsonObject(5);
//        assertEquals("interval", property.getString("name"));
//        assertEquals("code-string", property.getString("type"));
//        assertEquals(new Interval(0,100).toString(), property.getString("value"));
//        JsonArray expectedDictionary = new JsonArray();
//        expectedDictionary.put(Arrays.asList(new Interval(0,100).toString(),new Interval(0,100).toString()));
//        expectedDictionary.put(Arrays.asList(new Interval(100,200).toString(),new Interval(100,200).toString()));
//        expectedDictionary.put(Arrays.asList(new Interval(200,300).toString(),new Interval(200,300).toString()));
//        assertEquals(expectedDictionary.toString(), property.getJsonArray("dictionary").toString());
//        // TODO: test more types
    }


    public class RowHeaderBean
    {
        private int number;
        public int getNumber()
        {
            return number;
        }
        public void setNumber( int number )
        {
            this.number = number;
        }
    }

    @Test
    @Ignore
    public void beanValues() throws Exception
    {
        RowHeaderBean rowHeaderBean = new RowHeaderBean();
        rowHeaderBean.setNumber( 5 );

        assertEquals("", JsonFactory.beanValues(rowHeaderBean).toString());
//        ComponentModel rowHeaderModel = ComponentFactory.
//                getModel( rowHeaderBean, ComponentFactory.Policy.DEFAULT);
//
//        rowHeaderModel.findProperty( "number" );
    }

}