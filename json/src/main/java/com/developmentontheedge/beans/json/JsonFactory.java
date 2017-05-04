package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.editors.CustomEditorSupport;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.editors.StringTagEditorSupport;
import com.developmentontheedge.beans.editors.TagEditorSupport;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Provides API to serialize beans and dynamic property sets to JSON. 
 */
public class JsonFactory
{
    public static final String NAME_ATTR = "name";
    public static final String TYPE_ATTR = "type";
    public static final String VALUE_ATTR = "value";
    public static final String DISPLAYNAME_ATTR = "displayName";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String READONLY_ATTR = "readOnly";
    public static final String CHILDREN_ATTR = "children";
    public static final String DICTIONARY_ATTR = "dictionary";

    ///////////////////////////////////////////////////////////////////////////
    // public API
    //
    
    public static JsonObject beanValues(Object bean, int showMode)
    {
        Objects.requireNonNull(bean, "Bean should be not null.");
        CompositeProperty model = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return null;
    }
    
    public static JsonObject dpsValues(DynamicPropertySet dps)
    {
        return null;
    }
    
    public static JsonObject beanMeta(Object bean)
    {
        return null;
    }

    public static JsonObject dpsMeta(DynamicPropertySet dps)
    {
        return null;
    }
    
    public static JsonObject dictionaryValues(Object obj)
    {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    
    protected static void addBeanValues(CompositeProperty properties, int showMode)
    {

    }
    
    /**
     * Convert model to JSON
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static JsonArray getModelAsJSON(CompositeProperty properties, FieldMap fieldMap, int showMode)
            throws Exception
    {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            try
            {
                JsonObject object = convertSingleProperty( fieldMap, showMode, property );
                if(object != null)
                    result.add(object);
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Unable to convert property: #" + i + ": "
                        + ( property == null ? null : property.getName() ), e);
            }
        }
        return result.build();
    }

    private static JsonObject convertSingleProperty(FieldMap fieldMap, int showMode, Property property) throws Exception
    {
        String name = property.getName();
        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
            return null;
        JsonObjectBuilder p = Json.createObjectBuilder();
        p.add(NAME_ATTR, name);
        p.add(DISPLAYNAME_ATTR, property.getDisplayName());
        p.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        p.add(READONLY_ATTR, property.isReadOnly());
        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )
        {
            return fillCompositeProperty( fieldMap, showMode, property, p.build() );
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            return fillArrayProperty( fieldMap, showMode, property, p.build() );
        }
        return fillSimpleProperty( property, p.build() );
    }

    private static JsonObject fillSimpleProperty(Property property, JsonObject p) throws InstantiationException, IllegalAccessException,
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
                    JsonObject p1 = editor.toJSON();
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
                                    p.put(DICTIONARY_ATTR, createDictionary(elements, false));
                                }
                            }
                            else
                            {
                                p.put(key, p1.get(key));
                            }
                        }
                        return p;
                    }
                }
            }
            else if( TagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                TagEditorSupport editor = (TagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.put(DICTIONARY_ATTR, createDictionary(tags, true));
                }
            }
            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.put(DICTIONARY_ATTR, createDictionary(tags, false));
                }
            }
            else if( CustomEditorSupport.class.isAssignableFrom(editorClass) )
            {
                CustomEditorSupport editor = null;
                //TODO: support or correctly process some editors
                //Some editors like biouml.model.util.ReactionEditor, biouml.model.util.FormulaEditor
                //use Application.getApplicationFrame(), so we got a NullPointerException here
                try
                {
                    editor = (CustomEditorSupport)editorClass.newInstance();
                    initEditor( property, editor );
                    String[] tags = editor.getTags();
                    if( tags != null )
                    {
                        p.put(DICTIONARY_ATTR, createDictionary(tags, false));
                    }
                }
                catch( Exception e )
                {
                }
            }
        }

        Object value = property.getValue();
        if( value != null )
        {
            p.put(TYPE_ATTR, (value instanceof Boolean) ? "bool" : "code-string");
            p.put(VALUE_ATTR, value.toString());
        }
        return p;
    }

    private static JsonObject fillArrayProperty(FieldMap fieldMap, int showMode, Property property, JsonObject p)
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
                    p.put(DICTIONARY_ATTR, createDictionary(tags, false));
                    p.put(TYPE_ATTR, "multi-select");
                    Object[] vals = (Object[])property.getValue();
                    JsonArray value = new JsonArray();
                    if( vals != null )
                    {
                        for( Object val : vals )
                        {
                            value.put(val.toString());
                        }
                    }
                    p.put(VALUE_ATTR, value);
                    return p;
                }
                if( editor instanceof JSONCompatibleEditor )
                {
                    ( (JSONCompatibleEditor)editor ).addAsJSON(property, p, fieldMap, showMode);
                    return p;
                }
            }
        }
        JsonArray value = new JsonArray();
        ArrayProperty array = (ArrayProperty)property;
        for( int j = 0; j < array.getPropertyCount(); j++ )
        {
            Property element = array.getPropertyAt(j);
            if( element instanceof CompositeProperty )
            {
                value.put(getModelAsJSON((CompositeProperty)element, fieldMap.get(property), showMode));
            }
            else
            {
                JsonObject pCh = new JsonObject();
                Object val = element.getValue();
                if( val != null )
                {
                    pCh.put(TYPE_ATTR, (val instanceof Boolean) ? "bool" : "code-string");
                    pCh.put(NAME_ATTR, element.getName());
                    pCh.put(DISPLAYNAME_ATTR, element.getName());
                    pCh.put(VALUE_ATTR, val.toString());
                    pCh.put(READONLY_ATTR, element.isReadOnly());
                    value.put(new JsonArray().put(pCh));
                }
            }
        }
        p.put(TYPE_ATTR, "collection");
        p.put(VALUE_ATTR, value);
        return p;
    }

    private static JsonObject fillCompositeProperty(FieldMap fieldMap, int showMode, Property property, JsonObject p)
            throws Exception
    {
        Object value = property.getValue();
        Class<?> valueClass = property.getValueClass();
        if( GenericComboBoxItem.class.isAssignableFrom( valueClass ) )
        {
            p.put(TYPE_ATTR, "code-string");
            p.put(VALUE_ATTR, ( (GenericComboBoxItem)value ).getValue());
            ( (GenericComboBoxItem)value ).updateAvailableValues();
            p.put(DICTIONARY_ATTR, createDictionary( ( (GenericComboBoxItem)value ).getAvailableValues(), false));
        }
        else if( GenericMultiSelectItem.class.isAssignableFrom( valueClass ) )
        {
            p.put(TYPE_ATTR, "multi-select");
            p.put(VALUE_ATTR, ( (GenericMultiSelectItem)value ).getValues());
            ( (GenericMultiSelectItem)value ).updateAvailableValues();
            p.put(DICTIONARY_ATTR, createDictionary( ( (GenericMultiSelectItem)value ).getAvailableValues(), false));
        }
        else if( value != null && customBeans.containsKey( valueClass ) )
        {
            Object wrapper = customBeans.get( valueClass ).getConstructor( valueClass ).newInstance( value );
            p.put(TYPE_ATTR, "composite");
            p.put(VALUE_ATTR, getModelAsJSON(ComponentFactory.getModel( wrapper ), fieldMap.get(property), showMode));
        }
        else if( Color.class.isAssignableFrom( valueClass ) )
        {
            p.put(TYPE_ATTR, "color-selector");
            JsonArray valueEl = new JsonArray();
            valueEl.put(encodeColor((Color)value));
            p.put(VALUE_ATTR, valueEl);
        }
        else
        {
            p.put(TYPE_ATTR, "composite");
            p.put(VALUE_ATTR, getModelAsJSON((CompositeProperty)property, fieldMap.get(property), showMode));
        }
        return p;
    }

    protected static JsonArray createDictionary(Object[] strings, boolean byPosition)
    {
        if( strings == null )
            strings = new Object[] {};
        int position = 0;
        JsonArrayBuilder values = Json.createArrayBuilder();
        for( Object tagObj : strings )
        {
            String tag = tagObj.toString();
            if( byPosition )
                values.add(new JsonArray(Arrays.asList(String.valueOf(position), tag)));
            else
                values.add(new JSONArray(Arrays.asList(tag, tag)));
            position++;
        }
        return values.build();
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

//    public JsonArray createJsonArrayFromList(List list) {
//        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//        for(T person : list) {
//            jsonArray.add(Json.createObjectBuilder()
//                    .add("firstname", person.getFirstName())
//                    .add("lastname", person.getLastName()));
//        }
//        return arrayBuilder.build();
//    }
}
