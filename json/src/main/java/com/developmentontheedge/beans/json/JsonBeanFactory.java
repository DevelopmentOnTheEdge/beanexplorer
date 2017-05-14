package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.editors.CustomEditorSupport;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.editors.StringTagEditorSupport;
import com.developmentontheedge.beans.editors.TagEditorSupport;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.model.SimpleProperty;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import static com.developmentontheedge.beans.json.JsonFactory.getTypeName;

class JsonBeanFactory implements JsonPropertyAttributes
{
    static JsonObjectBuilder beanValues(Object bean)
    {
        CompositeProperty model = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return getJsonValues(model);
    }

    public static JsonObjectBuilder beanMeta(Object bean)
    {
        CompositeProperty model = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return getModelAsJson(model);
    }

    private static JsonObjectBuilder getJsonValues(CompositeProperty properties) {
        JsonObjectBuilder json = Json.createObjectBuilder();

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);

            if(property.getClass() == SimpleProperty.class) {
                JsonFactory.addToJsonObject(json, property.getName(), property.getValue(), property.getValueClass());
                continue;
            }

            if(property.getClass() == CompositeProperty.class) {
                json.add(property.getName(), getJsonValues((CompositeProperty)property) );
                continue;
            }

            if(property.getClass() == ArrayProperty.class) {
                json.add(property.getName(), getJsonValues((ArrayProperty) property));
            }

            //CompositeProperty model = ComponentFactory.getModel(p, ComponentFactory.Policy.DEFAULT);
        }

        return json;
    }

    private static JsonArrayBuilder getJsonValues(ArrayProperty properties) {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (int i = 0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);

            if(property.getClass() == CompositeProperty.class) {
                json.add(getJsonValues((CompositeProperty)property) );
                continue;
            }
            JsonFactory.addToJsonArray(json, properties.getPropertyAt(i).getValue());
        }
        return json;
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     */
    public static JsonObjectBuilder getModelAsJson(CompositeProperty properties)
    {
        return getModelAsJson(properties, FieldMap.ALL, Property.SHOW_USUAL);
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static JsonObjectBuilder getModelAsJson(CompositeProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            try
            {
                JsonObjectBuilder object = convertSingleProperty( fieldMap, showMode, property );
                if(object != null)
                    json.add(property.getName(), object);
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Unable to convert property: #" + i + ": "
                        + ( property == null ? null : property.getName() ), e);
            }
        }
        return json;
    }

    private static JsonObjectBuilder convertSingleProperty(FieldMap fieldMap, int showMode, Property property) throws Exception
    {
        String name = property.getName();
        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
            return null;
        JsonObjectBuilder json = Json.createObjectBuilder();

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

        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )
        {
            return fillCompositeProperty( fieldMap, showMode, property, json );
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            return fillArrayProperty( fieldMap, showMode, property, json );
        }
        return fillSimpleProperty( property, json );
    }

    private static JsonObjectBuilder fillSimpleProperty(Property property, JsonObjectBuilder json) throws InstantiationException, IllegalAccessException,
            JsonException
    {
        Class<?> editorClass = property.getPropertyEditorClass();
        if( editorClass != null )
        {
            if( JsonSerializable.class.isAssignableFrom(editorClass) )
            {
                JsonSerializable editor = (JsonSerializable)editorClass.newInstance();
                if( editor instanceof PropertyEditorEx)
                {
                    initEditor( property, (PropertyEditorEx)editor );
                    JsonObject p1 = editor.toJson();
                    if( p1 != null )
                    {
                        for (String key : p1.keySet())
                        {

                            if( key.equals("dictionary") )
                            {
                                JsonArray array = p1.getJsonArray("dictionary");
                                if( array != null )
                                {
                                    String[] elements = new String[array.size()];
                                    for( int index = 0; index < array.size(); index++ )
                                        elements[index] = array.getString(index);
                                    json.add(DICTIONARY_ATTR, createDictionary(elements, false));
                                }
                            }
                            else
                            {
                                json.add(key, p1.get(key));
                            }
                        }
                        return json;
                    }
                }
            }
            else if( TagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                TagEditorSupport editor = (TagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    json.add(DICTIONARY_ATTR, createDictionary(tags, true));
                }
            }
            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    json.add(DICTIONARY_ATTR, createDictionary(tags, false));
                }
            }
            else if( CustomEditorSupport.class.isAssignableFrom(editorClass) )
            {
                CustomEditorSupport editor;
                //TODO: support or correctly process some editors
                //Some editors like biouml.model.util.ReactionEditor, biouml.model.util.FormulaEditor
                //use Application.getApplicationFrame(), so we got a NullPointerException here
                editor = (CustomEditorSupport)editorClass.newInstance();
                initEditor( property, editor );
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    json.add(DICTIONARY_ATTR, createDictionary(tags, false));
                }
            }
        }

        Object value = property.getValue();
        if( value != null )
        {
            json.add(TYPE_ATTR, getTypeName(value.getClass()));
            json.add(VALUE_ATTR, value.toString());
        }
        return json;
    }

    private static JsonObjectBuilder fillArrayProperty(FieldMap fieldMap, int showMode, Property property, JsonObjectBuilder json)
            throws Exception
    {
        Class<?> c = property.getPropertyEditorClass();
        if( c != null )
        {
            if( CustomEditorSupport.class.isAssignableFrom(c) )
            {
                CustomEditorSupport editor = (CustomEditorSupport)c.newInstance();
                initEditor( property, editor );
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    json.add(DICTIONARY_ATTR, createDictionary(tags, false));
                    json.add(TYPE_ATTR, "multi-select");
                    Object[] vals = (Object[])property.getValue();
                    JsonArrayBuilder value = Json.createArrayBuilder();
                    if( vals != null )
                    {
                        for( Object val : vals )
                        {
                            value.add(val.toString());
                        }
                    }
                    json.add(VALUE_ATTR, value);
                    return json;
                }
//TODO                if( editor instanceof JsonCompatibleEditor )
//                {
//                    ( (JsonCompatibleEditor)editor ).addAsJson(property, p, fieldMap, showMode);
//                    return p.build();
//                }
            }
        }
        JsonArrayBuilder value = Json.createArrayBuilder();
        ArrayProperty array = (ArrayProperty)property;
        for( int j = 0; j < array.getPropertyCount(); j++ )
        {
            Property element = array.getPropertyAt(j);
            if( element instanceof CompositeProperty )
            {
                value.add(getModelAsJson((CompositeProperty)element, fieldMap.get(property), showMode));
            }
            else
            {
                JsonObjectBuilder pCh = Json.createObjectBuilder();
                Object val = element.getValue();
                if( val != null )
                {
                    pCh.add(TYPE_ATTR, getTypeName(value.getClass()));
                    pCh.add(NAME_ATTR, element.getName());
                    pCh.add(DISPLAY_NAME_ATTR, element.getName());
                    pCh.add(VALUE_ATTR, val.toString());
                    pCh.add(READONLY_ATTR, element.isReadOnly());
                    value.add(pCh);
                }
            }
        }
        json.add(TYPE_ATTR, "collection");
        json.add(VALUE_ATTR, value);
        return json;
    }

    private static JsonObjectBuilder fillCompositeProperty(FieldMap fieldMap, int showMode, Property property, JsonObjectBuilder json)
            throws Exception
    {
        Object value = property.getValue();
        Class<?> valueClass = property.getValueClass();
//        if( GenericComboBoxItem.class.isAssignableFrom( valueClass ) )
//        {
//            p.add(TYPE_ATTR, "code-string");
//            p.add(VALUE_ATTR, ( (GenericComboBoxItem)value ).getValue());
//            ( (GenericComboBoxItem)value ).updateAvailableValues();
//            p.add(DICTIONARY_ATTR, createDictionary( ( (GenericComboBoxItem)value ).getAvailableValues(), false));
//        }
//        else if( GenericMultiSelectItem.class.isAssignableFrom( valueClass ) )
//        {
//            p.add(TYPE_ATTR, "multi-select");
//            p.add(VALUE_ATTR, ( (GenericMultiSelectItem)value ).getValues());
//            ( (GenericMultiSelectItem)value ).updateAvailableValues();
//            p.add(DICTIONARY_ATTR, createDictionary( ( (GenericMultiSelectItem)value ).getAvailableValues(), false));
//        }
//        else if( value != null && customBeans.containsKey( valueClass ) )
//        {
//            Object wrapper = customBeans.dpsValuesBuilder( valueClass ).getConstructor( valueClass ).newInstance( value );
//            p.add(TYPE_ATTR, "composite");
//            p.add(VALUE_ATTR, getModelAsJson(ComponentFactory.getModel( wrapper ), fieldMap.dpsValuesBuilder(property), showMode));
//        }
//        else
        if( Color.class.isAssignableFrom( valueClass ) )
        {
            json.add(TYPE_ATTR, "color-selector");
            json.add(VALUE_ATTR, encodeColor((Color)value));
        }
        else
        {
            json.add(TYPE_ATTR, "composite");
            json.add(VALUE_ATTR, getModelAsJson((CompositeProperty)property, fieldMap.get(property), showMode));
        }
        return json;
    }
//        private static JsonObjectBuilder getJson(SimpleProperty model) {
//        JsonObjectBuilder json = Json.createObjectBuilder();
//        return json;
//    }

    static JsonArrayBuilder createDictionary(Object[] strings, boolean byPosition)
    {
        if( strings == null )
            strings = new Object[] {};
        int position = 0;
        JsonArrayBuilder values = Json.createArrayBuilder();
        for( Object tagObj : strings )
        {
            String tag = tagObj.toString();
            if( byPosition )
                values.add(jsonArrayBuilderFromList(Arrays.asList(String.valueOf(position), tag)));
            else
                values.add(jsonArrayBuilderFromList(Arrays.asList(tag, tag)));
            position++;
        }
        return values;
    }

    private static JsonArrayBuilder jsonArrayBuilderFromList(List<Object> list) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for(Object s: list) {
            json.add(s.toString());
        }
        return json;
    }

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

    private static void initEditor(Property property, PropertyEditorEx editor)
    {
        Object owner = property.getOwner();
        if( owner instanceof Property.PropWrapper )
            owner = ( (Property.PropWrapper)owner ).getOwner();
        editor.setValue(property.getValue());
        editor.setBean(owner);
        editor.setDescriptor(property.getDescriptor());
    }
}
