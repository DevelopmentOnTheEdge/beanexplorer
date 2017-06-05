package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

import java.awt.Color;
import java.util.List;
import java.util.Map;

class TestBeans
{
    public static class BeanWithMap
    {
        final Map<String, String> field;

        BeanWithMap(Map<String, String> field)
        {
            this.field = field;
        }

        public Map<String, String> getField()
        {
            return field;
        }
    }

    public static class BeanWithMapLong
    {
        final Map<Long, Long> field;

        BeanWithMapLong(Map<Long, Long> field)
        {
            this.field = field;
        }

        public Map<Long, Long> getField()
        {
            return field;
        }
    }

    public static class BeanWithList
    {
        private List<String> field;

        BeanWithList(List<String> field)
        {
            this.field = field;
        }

        public List<String> getField()
        {
            return field;
        }
    }

    public static class BeanWithListLong
    {
        private List<Long> field;

        BeanWithListLong(List<Long> field)
        {
            this.field = field;
        }

        public List<Long> getField()
        {
            return field;
        }
    }

    public static class BeanWithListObj
    {
        private List<InnerBeanClass> field;

        BeanWithListObj(List<InnerBeanClass> field)
        {
            this.field = field;
        }

        public List<InnerBeanClass> getField()
        {
            return field;
        }
    }

    public static class FormPresentation
    {
        final String title;
        final DynamicPropertySet dps;

        FormPresentation(String title, DynamicPropertySet dps)
        {
            this.title = title;
            this.dps = dps;
        }

        public String getTitle()
        {
            return title;
        }

        public DynamicPropertySet getDps()
        {
            return dps;
        }
    }

    public static class SimpleBean
    {
        private long arr[];
        private String name;
        private int number;

        SimpleBean(String name, int number, long[] arr) {

            this.name = name;
            this.number = number;
            this.arr = arr;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        public long[] getArr() {
            return arr;
        }
    }

    public static class BeanWithInnerClass
    {
        private InnerBeanClass arr[];
        private InnerBeanClass field1;

        BeanWithInnerClass(InnerBeanClass field1, InnerBeanClass[] arr) {
            this.field1 = field1;
            this.arr = arr;
        }

        public InnerBeanClass getField1() {return field1;}
        public InnerBeanClass[] getArr() {return arr;}
    }

    public static class InnerBeanClass
    {
        String name;
        InnerBeanClass(String name) {this.name = name;}
        public String getName() {return name;}
    }

    public static class TypedResponse {
        final String type;
        final Object value;

        TypedResponse(String type, Object value)
        {
            this.type = type;
            this.value = value;
        }

        public String getType()
        {
            return type;
        }

        public Object getValue()
        {
            return value;
        }
    }

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

    enum EnumClass{
        test,test2
    }

    public static class ClassWithEnum{
        EnumClass status;

        ClassWithEnum(EnumClass enumClass) {
            this.status = enumClass;
        }

        public EnumClass getStatus() {
            return status;
        }
    }


//    @Test
//    public void testGetModelAsJson()
//    {
//        TestBean bean = new TestBean();
//        bean.setStr("string value");
//        bean.setColor(Color.BLACK);
//        bean.setSelect("one");
//
//        ComponentModel model = ComponentFactory.getModel(bean);
//        JsonArray json = JsonFactory.getJsonMeta(model).build();
//        assertNotNull(json);
//        assertEquals(model.getPropertyCount(), json.size());
//        assertEquals("[" +
//            "{'name':'class','displayName':'class','description':'class','readOnly':true,'type':'code-string','value':'class com.developmentontheedge.beans.jsontest.JsonFactoryBeanTest$TestBean'}," +
//            "{'name':'color','displayName':'color','description':'Color property','readOnly':false,'type':'code-string','value':'java.awt.Color[r=0,g=0,b=0]'}," +
//            "{'name':'select','displayName':'select','description':'select','readOnly':false,'type':'code-string','value':'one'}," +
//            "{'name':'str','displayName':'str','description':'str','readOnly':false,'type':'code-string','value':'string value'}]", oneQuotes(json.toString()));
//
////        json = JsonFactory.getJsonMeta(model, FieldMap.ALL, Property.SHOW_EXPERT);
////        assertNotNull(json);
////        assertEquals("", json.build().toString());
//
//        JsonObject property = json.getJsonObject(3);
//        assertEquals("str", property.getString("name"));
//        //assertEquals("String", property.getString("displayName"));
//        //assertEquals("Test string property", property.getString("description"));
//        assertEquals("string value", property.getString("value"));
//        assertFalse(property.getBoolean("readOnly"));
//        assertEquals("code-string", property.getString("type"));
////
////        property = json.getJsonObject(1);
////        assertEquals("input", property.getString("name"));
////        assertEquals(TableDataCollection.class.getName(), property.getString("elementClass"));
////        assertTrue(property.getBoolean("elementMustExist"));
////        assertFalse(property.getBoolean("canBeNull"));
////        assertFalse(property.getBoolean("multiSelect"));
////        assertEquals("test/path", property.getString("value"));
////        assertEquals("data-element-path", property.getString("type"));
////
////        property = json.getJsonObject(2);
////        assertEquals("output", property.getString("name"));
////        assertEquals(Diagram.class.getName(), property.getString("elementClass"));
////        assertFalse(property.getBoolean("elementMustExist"));
////        assertFalse(property.getBoolean("canBeNull"));
////        assertFalse(property.getBoolean("multiSelect"));
////        assertEquals("test/path output", property.getString("value"));
////        assertEquals("data-element-path", property.getString("type"));
////
//        property = json.getJsonObject(1);
//        assertEquals("color", property.getString("name"));
//        // TODO: something wrong with color; need to investigate
//        //assertEquals("color-selector", property.getString("type"));
//        //assertEquals("[0,0,0]", property.getJsonArray("value").get(0));
//
//        property = json.getJsonObject(2);
//        assertEquals("select", property.getString("name"));
//        assertEquals("code-string", property.getString("type"));
//        assertEquals("one", property.getString("value"));
//        //assertEquals("[[\"one\",\"one\"],[\"two\",\"two\"]]", property.getJsonArray("dictionary").toString());
////
////        property = json.getJsonObject(5);
////        assertEquals("interval", property.getString("name"));
////        assertEquals("code-string", property.getString("type"));
////        assertEquals(new Interval(0,100).toString(), property.getString("value"));
////        JsonArray expectedDictionary = new JsonArray();
////        expectedDictionary.put(Arrays.asList(new Interval(0,100).toString(),new Interval(0,100).toString()));
////        expectedDictionary.put(Arrays.asList(new Interval(100,200).toString(),new Interval(100,200).toString()));
////        expectedDictionary.put(Arrays.asList(new Interval(200,300).toString(),new Interval(200,300).toString()));
////        assertEquals(expectedDictionary.toString(), property.getJsonArray("dictionary").toString());
////        // TODO: test more types
//    }
}
