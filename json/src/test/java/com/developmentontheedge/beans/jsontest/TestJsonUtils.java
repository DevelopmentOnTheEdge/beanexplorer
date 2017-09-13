package com.developmentontheedge.beans.jsontest;

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


public class TestJsonUtils extends TestUtils
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

//    public static class TestBeanBeanInfo extends BeanInfoEx2<TestBean>
//    {
//        public TestBeanBeanInfo()
//        {
//            super(TestBean.class);
//        }
//
//        @Override
//        public void initProperties() throws Exception
//        {
//            add("str");
//            findPropertyDescriptor("str").setExpert(true);
//            property( "input" ).inputElement( TableDataCollection.class ).add();
//            property( "output" ).outputElement( Diagram.class ).add();
//            add("color", ColorEditor.class);
//            add("select", TestSelector.class);
//            property( "interval" ).simple().editor( TestIntervalSelector.class ).add();
//        }
//    }

    @Test
    public void testGetModelAsJSON()
    {
        TestBean bean = new TestBean();
        bean.setStr("string value");
//        bean.setInput(DataElementPath.create("test/path"));
//        bean.setOutput(DataElementPath.create("test/path output"));
        bean.setColor(Color.BLACK);
        bean.setSelect("one");
        bean.setInterval(new Interval(0,100));

        ComponentModel model = ComponentFactory.getModel(bean);
        JsonObject json = JsonFactory.bean(bean);
        assertNotNull(json);

        assertEquals(model.getPropertyCount()-1, json.getJsonArray("order").getValuesAs(JsonValue.class).size());

        assertEquals("{" +
                "'interval':{" +
                    "'center':50," +
                    "'from':0," +
                    "'length':101," +
                    "'pointPos':[0.0,0.009900990099009901,0.019801980198019802,0.0297029702970297,0.039603960396039604,0.04950495049504951,0.0594059405940594,0.06930693069306931,0.07920792079207921,0.0891089108910891,0.09900990099009901,0.10891089108910891,0.1188118811881188,0.12871287128712872,0.13861386138613863,0.1485148514851485,0.15841584158415842,0.16831683168316833,0.1782178217821782,0.18811881188118812,0.19801980198019803,0.2079207920792079,0.21782178217821782,0.22772277227722773,0.2376237623762376,0.24752475247524752,0.25742574257425743,0.26732673267326734,0.27722772277227725,0.2871287128712871,0.297029702970297,0.3069306930693069,0.31683168316831684,0.32673267326732675,0.33663366336633666,0.3465346534653465,0.3564356435643564,0.36633663366336633,0.37623762376237624,0.38613861386138615,0.39603960396039606,0.40594059405940597,0.4158415841584158,0.42574257425742573,0.43564356435643564,0.44554455445544555,0.45544554455445546,0.46534653465346537,0.4752475247524752,0.48514851485148514,0.49504950495049505,0.504950495049505,0.5148514851485149,0.5247524752475248,0.5346534653465347,0.5445544554455446,0.5544554455445545,0.5643564356435643,0.5742574257425742,0.5841584158415841,0.594059405940594,0.6039603960396039,0.6138613861386139,0.6237623762376238,0.6336633663366337,0.6435643564356436,0.6534653465346535,0.6633663366336634,0.6732673267326733,0.6831683168316832,0.693069306930693,0.7029702970297029,0.7128712871287128,0.7227722772277227,0.7326732673267327,0.7425742574257426,0.7524752475247525,0.7623762376237624,0.7722772277227723,0.7821782178217822,0.7920792079207921,0.801980198019802,0.8118811881188119,0.8217821782178217,0.8316831683168316,0.8415841584158416,0.8514851485148515,0.8613861386138614,0.8712871287128713,0.8811881188118812,0.8910891089108911,0.900990099009901,0.9108910891089109,0.9207920792079208,0.9306930693069307,0.9405940594059405,0.9504950495049505,0.9603960396039604,0.9702970297029703,0.9801980198019802]," +
                    "'to':100}," +
                "'select':'one'," +
                "'str':'string value'" +
            "}", oneQuotes(JsonFactory.beanValues(bean).toString()));

        assertEquals("{" +
                "'interval':{" +
                "'center':50," +
                "'from':0," +
                "'length':101," +
                "'pointPos':null," +
                "'to':100}," +
                "'select':'one'," +
                "'str':'string value'" +
                "}", oneQuotes(json.getJsonObject("values").toString()));

//        json = JSONUtils.getModelAsJSON(model, FieldMap.ALL, Property.SHOW_EXPERT);
//        assertNotNull(json);
//        assertEquals(model.getPropertyCount(), json.length());

//        JSONObject property = json.getJSONObject(0);
//        assertEquals("str", property.getString("name"));
//        assertEquals("String", property.getString("displayName"));
//        assertEquals("Test string property", property.getString("description"));
//        assertEquals("string value", property.getString("value"));
//        assertFalse(property.getBoolean("readOnly"));
//        assertEquals("code-string", property.getString("type"));
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
//        property = json.getJSONObject(3);
//        assertEquals("color", property.getString("name"));
//        // TODO: something wrong with color; need to investigate
//        /*assertEquals("color-selector", property.getString("type"));
//        assertEquals("[0,0,0]", property.getJSONArray("value").get(0));*/
//
//        property = json.getJSONObject(4);
//        assertEquals("select", property.getString("name"));
//        assertEquals("code-string", property.getString("type"));
//        assertEquals("one", property.getString("value"));
//        assertEquals("[[\"one\",\"one\"],[\"two\",\"two\"]]", property.getJSONArray("dictionary").toString());
//
        //json.getJsonObject(5);
        JsonObject property = json.getJsonObject("meta").getJsonObject("/interval");

//        assertEquals("interval", property.getString("name"));
        assertEquals("Interval", property.getString("type"));
        assertEquals("{'center':50,'from':0,'length':101,'pointPos':null,'to':100}",
                oneQuotes(json.getJsonObject("values").getJsonObject("interval").toString()));

//        JSONArray expectedDictionary = new JSONArray();
//        expectedDictionary.put(Arrays.asList(new Interval(0,100).toString(),new Interval(0,100).toString()));
//        expectedDictionary.put(Arrays.asList(new Interval(100,200).toString(),new Interval(100,200).toString()));
//        expectedDictionary.put(Arrays.asList(new Interval(200,300).toString(),new Interval(200,300).toString()));
//        assertEquals(expectedDictionary.toString(), property.getJSONArray("dictionary").toString());
        // TODO: test more types
    }
}
