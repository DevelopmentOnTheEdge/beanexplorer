package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.awt.Color;

import static com.developmentontheedge.beans.json.JsonFactory.getTypeName;

class JsonBeanFactory implements JsonPropertyAttributes
{
    static JsonObjectBuilder beanValues(Object bean)
    {
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return getValues(property, FieldMap.ALL, Property.SHOW_USUAL);
    }

    static JsonObjectBuilder beanMeta(Object bean)
    {
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonObjectBuilder json = Json.createObjectBuilder();
        getMeta(property, FieldMap.ALL, Property.SHOW_USUAL, json, "");

        return json;
    }

    private static JsonObjectBuilder getValues(CompositeProperty properties, FieldMap fieldMap, int showMode) {
        JsonObjectBuilder json = Json.createObjectBuilder();

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property instanceof CompositeProperty) {
                json.add(property.getName(), getValues((CompositeProperty)property, fieldMap, showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(property.getName(), getValues((ArrayProperty) property, fieldMap, showMode));
                continue;
            }

            if("class".equals(property.getName())){
                json.add(property.getName(), property.getValue().toString());
                continue;
            }
            JsonFactory.addToJsonObject(json, property.getName(), property.getValue(), property.getValueClass());
        }

        return json;
    }

    private static JsonArrayBuilder getValues(ArrayProperty properties, FieldMap fieldMap, int showMode) {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (int i = 0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property instanceof CompositeProperty) {
                json.add(getValues((CompositeProperty)property, fieldMap, showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(getValues((ArrayProperty) property, fieldMap, showMode));
                continue;
            }

            JsonFactory.addToJsonArray(json, properties.getPropertyAt(i).getValue());
        }
        return json;
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    private static void getMeta(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                             JsonObjectBuilder json, String path)
    {

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            json.add(property.getName(), getPropertyMeta(property));

            if(property instanceof CompositeProperty) {
                getMeta((CompositeProperty) property, fieldMap, showMode, json, path);
                continue;
            }

            if(property instanceof ArrayProperty) {
                getMeta((ArrayProperty) property, fieldMap, showMode, json, path);
                continue;
            }
        }
    }

    private static void getMeta(ArrayProperty properties, FieldMap fieldMap, int showMode,
                                            JsonObjectBuilder json, String path)
    {
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            //json.add(property.getName(), getPropertyMeta(property));

            if(property instanceof CompositeProperty) {
                getMeta((CompositeProperty) property, fieldMap, showMode, json, path);
                continue;
            }

            if(property instanceof ArrayProperty) {
                getMeta((ArrayProperty) property, fieldMap, showMode, json, path);
            }
        }
    }

    public static JsonObject getPropertyMeta(Property property){
        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add(TYPE_ATTR, getTypeName(property.getValueClass()));

        if(!property.getName().equals(property.getDisplayName()))
        {
            json.add(DISPLAY_NAME_ATTR, property.getDisplayName());
        }

        String shortDescription = property.getShortDescription().split("\n")[0];
        if(!property.getName().equals(shortDescription))
        {
            json.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        }

        if(property.isReadOnly())
        {
            json.add(READONLY_ATTR, true);
        }

        return json.build();
    }

//    private static JsonObjectBuilder getJsonPropertyMeta(Property property, FieldMap fieldMap, int showMode) throws Exception
//    {
//        String name = property.getName();
//        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
//            return null;
//        JsonObjectBuilder json = Json.createObjectBuilder();
//
//        if(!property.getName().equals(property.getDisplayName()))
//        {
//            json.add(DISPLAY_NAME_ATTR, property.getDisplayName());
//        }
//
//        String shortDescription = property.getShortDescription().split("\n")[0];
//        if(!property.getName().equals(shortDescription))
//        {
//            json.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
//        }
//
//        if(property.isReadOnly())
//        {
//            json.add(READONLY_ATTR, true);
//        }
//
//        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )
//        {
//            return getMeta( (CompositeProperty)property, fieldMap, showMode);
//        }
//        if( property instanceof ArrayProperty && !property.isHideChildren() )
//        {
//            getMeta( (ArrayProperty)property, fieldMap, showMode);
//        }
//        return fillSimpleProperty( property, json );
//    }
//
//    private static JsonObjectBuilder fillSimpleProperty(Property property, JsonObjectBuilder json) throws InstantiationException, IllegalAccessException,
//            JsonException
//    {
//        Class<?> editorClass = property.getPropertyEditorClass();
//        if( editorClass != null )
//        {
//            if( JsonSerializable.class.isAssignableFrom(editorClass) )
//            {
//                JsonSerializable editor = (JsonSerializable)editorClass.newInstance();
//                if( editor instanceof PropertyEditorEx)
//                {
//                    initEditor( property, (PropertyEditorEx)editor );
//                    JsonObject p1 = editor.toJson();
//                    if( p1 != null )
//                    {
//                        for (String key : p1.keySet())
//                        {
//
//                            if( key.equals("dictionary") )
//                            {
//                                JsonArray array = p1.getJsonArray("dictionary");
//                                if( array != null )
//                                {
//                                    String[] elements = new String[array.size()];
//                                    for( int index = 0; index < array.size(); index++ )
//                                        elements[index] = array.getString(index);
//                                    json.add(DICTIONARY_ATTR, createDictionary(elements, false));
//                                }
//                            }
//                            else
//                            {
//                                json.add(key, p1.get(key));
//                            }
//                        }
//                        return json;
//                    }
//                }
//            }
//            else if( TagEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                TagEditorSupport editor = (TagEditorSupport)editorClass.newInstance();
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    json.add(DICTIONARY_ATTR, createDictionary(tags, true));
//                }
//            }
//            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    json.add(DICTIONARY_ATTR, createDictionary(tags, false));
//                }
//            }
//            else if( CustomEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                CustomEditorSupport editor;
//                //TODO: support or correctly process some editors
//                //Some editors like biouml.model.util.ReactionEditor, biouml.model.util.FormulaEditor
//                //use Application.getApplicationFrame(), so we got a NullPointerException here
//                editor = (CustomEditorSupport)editorClass.newInstance();
//                initEditor( property, editor );
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    json.add(DICTIONARY_ATTR, createDictionary(tags, false));
//                }
//            }
//        }
//
//        Object value = property.getValue();
//        if( value != null )
//        {
//            json.add(TYPE_ATTR, getTypeName(value.getClass()));
//            json.add(VALUE_ATTR, value.toString());
//        }
//        return json;
//    }
//
//
//    static JsonArrayBuilder createDictionary(Object[] strings, boolean byPosition)
//    {
//        if( strings == null )
//            strings = new Object[] {};
//        int position = 0;
//        JsonArrayBuilder values = Json.createArrayBuilder();
//        for( Object tagObj : strings )
//        {
//            String tag = tagObj.toString();
//            if( byPosition )
//                values.add(jsonArrayBuilderFromList(Arrays.asList(String.valueOf(position), tag)));
//            else
//                values.add(jsonArrayBuilderFromList(Arrays.asList(tag, tag)));
//            position++;
//        }
//        return values;
//    }
//
//    private static JsonArrayBuilder jsonArrayBuilderFromList(List<Object> list) {
//        JsonArrayBuilder json = Json.createArrayBuilder();
//        for(Object s: list) {
//            json.add(s.toString());
//        }
//        return json;
//    }

    /**
     * Counterpart for parseColor
     * @param color color to encode
     * @return array of color components
     */
    public static JsonArrayBuilder encodeColor(Color color)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();
        if(color == null || color.getAlpha() == 0) return json;

        return json.add(color.getRed()).add(color.getGreen()).add(color.getBlue());
    }
//
//    private static void initEditor(Property property, PropertyEditorEx editor)
//    {
//        Object owner = property.getOwner();
//        if( owner instanceof Property.PropWrapper )
//            owner = ( (Property.PropWrapper)owner ).getOwner();
//        editor.setValue(property.getValue());
//        editor.setBean(owner);
//        editor.setDescriptor(property.getDescriptor());
//    }
}
