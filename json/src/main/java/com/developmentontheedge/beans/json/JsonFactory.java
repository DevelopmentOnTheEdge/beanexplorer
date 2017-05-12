package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
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

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import static java.util.Objects.requireNonNull;

/**
 * Provides API to serialize beans and dynamic property sets to Json. 
 */
public class JsonFactory implements JsonPropertyAttributes
{
    ///////////////////////////////////////////////////////////////////////////
    // public API
    //
    
    public static JsonObject dps(DynamicPropertySet dps){
        requireNonNull(dps);

        JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("values", dpsValues(dps));
        result.add("meta", dpsMeta(dps));
        result.add("order", dpsOrder(dps));
        return result.build();
    }

    public static JsonObject dpsValues(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        return get(dps).build();
    }

    public static JsonObject dpsMeta(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonObjectBuilder metaBuilder = Json.createObjectBuilder();
        dpsMeta(dps, metaBuilder, "");

        return metaBuilder.build();
    }

    public static JsonArray dpsOrder(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonArrayBuilder metaBuilder = Json.createArrayBuilder();
        dpsOrder(dps, metaBuilder, "");

        return metaBuilder.build();
    }

    public static JsonObject beanValues(Object bean)
    {
        requireNonNull(bean);

        CompositeProperty model = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        return objectBuilder.build();
    }

    public static JsonObjectBuilder beanMeta(Object bean)
    {
        return null;
    }
    
    public static JsonObjectBuilder dictionaryValues(Object obj)
    {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static void dpsOrder(DynamicPropertySet dps, JsonArrayBuilder metaBuilder, String path)
    {
        for (Map.Entry<String, Object> entry :dps.asMap().entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            metaBuilder.add((path.equals("") ? "" : path + "/") + key);
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, metaBuilder, path + "/" + key);
        }
    }

    private static void dpsMeta(DynamicPropertySet dps, JsonObjectBuilder metaBuilder, String path)
    {
        for( DynamicProperty property : dps )
        {
            metaBuilder.add( (path.equals("") ? "" : path + "/") + property.getName(), toJson(property) );

            if(property.getValue() instanceof DynamicPropertySet)
            {
                dpsMeta((DynamicPropertySet)property.getValue(), metaBuilder, path + "/" + property.getName());
            }
        }
    }

    private static JsonObjectBuilder get(DynamicPropertySet dps)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();
        for (Map.Entry<String, Object> entry :dps.asMap().entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            Class<?> type = dps.getProperty(key).getType();

            if( value == null )json.addNull(key);

            if( type == String.class ){    json.add(key, (String) value); continue;}
            if( type == Double.class ){    json.add(key, (double)value ); continue;}
            if( type == Long.class ){      json.add(key, (long)value ); continue;}
            if( type == Integer.class ){   json.add(key, (int)value ); continue;}
            if( type == Boolean.class ){   json.add(key, (boolean)value ); continue;}
            if( type == Float.class ){     json.add(key, (float)value ); continue;}
            if( type == BigInteger.class ){json.add(key, (BigInteger) value ); continue;}
            if( type == BigDecimal.class ){json.add(key, (BigDecimal) value ); continue;}

            if( type == JsonValue.class ){json.add(key, (JsonValue)value); continue;}
            if( type == JsonObjectBuilder.class ){json.add(key, (JsonObjectBuilder)value ); continue;}
            if( type == JsonArrayBuilder.class ){json.add(key, (JsonArrayBuilder)value ); continue;}

            if( value instanceof DynamicPropertySet){json.add(key, get((DynamicPropertySet)value));}
        }
        return json;
    }

    /**
     * Converts DynamicProperty parameter to JsonObject.
     *
     * @param property Operation parameters
     */
    public static JsonObject toJson(DynamicProperty property)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add(DISPLAY_NAME_ATTR, property.getDisplayName() );

        if(property.getType() != String.class)
        {
            json.add(TYPE_ATTR, property.getType().getSimpleName() );
        }

        if(property.isHidden())
        {
            json.add(HIDDEN_ATTR, true );
        }

        if(property.isReadOnly())
        {
            json.add(READONLY_ATTR, true);
        }

        if(property.isCanBeNull())
        {
            json.add(CAN_BE_NULL_ATTR, true);
        }

        //json.add( "extraAttrs", property.getAttribute( BeanInfoConstants.EXTRA_ATTRS ) );

        Object columnSizeAttr = property.getAttribute( BeanInfoConstants.COLUMN_SIZE_ATTR );
        if( columnSizeAttr != null )
        {
            json.add( "columnSizeAttr", "" + columnSizeAttr );
        }

//        if( !Boolean.TRUE.equals( property.getAttribute( BeanInfoConstants.NO_TAG_LIST ) ) )
//        {
//            Object tags = WebFormPropertyInspector.normalizeTags( property.getAttribute( BeanInfoConstants.TAG_LIST_ATTR ) );
////                if( tags != null )
////                {
////                    tags = OperationFragmentHelper.customizeTagsCommon( connector, ui, ( String[] )tags, property.getName(), messages, true );
////                }
//            if( tags != null )
//            {
//                json.add( "tagList", tags );
//            }
//        }

        if( property.getBooleanAttribute( BeanInfoConstants.RELOAD_ON_CHANGE ))
        {
            json.add(RELOAD_ON_CHANGE_ATTR, true );
        }

        if( property.getBooleanAttribute( BeanInfoConstants.RELOAD_ON_CLICK ))
        {
            json.add( "reloadOnClick", true );
        }

        if( property.getBooleanAttribute( BeanInfoConstants.RAW_VALUE ))
        {
            json.add(RAW_VALUE_ATTR, true );
        }

        return json.build();
    }
    
    protected static void addBeanValues(CompositeProperty properties, int showMode)
    {

    }

    /**
     * Convert model to Json
     * @param properties model to convert
     */
    public static JsonArrayBuilder getModelAsJson(CompositeProperty properties) throws Exception
    {
        return getModelAsJson(properties, FieldMap.ALL, Property.SHOW_USUAL);
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static JsonArrayBuilder getModelAsJson(CompositeProperty properties, FieldMap fieldMap, int showMode)
            throws Exception
    {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            try
            {
                JsonObjectBuilder object = convertSingleProperty( fieldMap, showMode, property );
                if(object != null)
                    result.add(object);
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Unable to convert property: #" + i + ": "
                        + ( property == null ? null : property.getName() ), e);
            }
        }
        return result;
    }

    private static JsonObjectBuilder convertSingleProperty(FieldMap fieldMap, int showMode, Property property) throws Exception
    {
        String name = property.getName();
        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
            return null;
        JsonObjectBuilder p = Json.createObjectBuilder();
        p.add(NAME_ATTR, name);
        p.add(DISPLAY_NAME_ATTR, property.getDisplayName());
        p.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        p.add(READONLY_ATTR, property.isReadOnly());

        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )
        {
            return fillCompositeProperty( fieldMap, showMode, property, p );
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            return fillArrayProperty( fieldMap, showMode, property, p );
        }
        return fillSimpleProperty( property, p );
    }

    private static JsonObjectBuilder fillSimpleProperty(Property property, JsonObjectBuilder p) throws InstantiationException, IllegalAccessException,
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
                                    p.add(DICTIONARY_ATTR, createDictionary(elements, false));
                                }
                            }
                            else
                            {
                                p.add(key, p1.get(key));
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
                    p.add(DICTIONARY_ATTR, createDictionary(tags, true));
                }
            }
            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.add(DICTIONARY_ATTR, createDictionary(tags, false));
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
                    p.add(DICTIONARY_ATTR, createDictionary(tags, false));
                }
            }
        }

        Object value = property.getValue();
        if( value != null )
        {
            p.add(TYPE_ATTR, (value instanceof Boolean) ? "bool" : "code-string");
            p.add(VALUE_ATTR, value.toString());
        }
        return p;
    }

    private static JsonObjectBuilder fillArrayProperty(FieldMap fieldMap, int showMode, Property property, JsonObjectBuilder p)
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
                    p.add(DICTIONARY_ATTR, createDictionary(tags, false));
                    p.add(TYPE_ATTR, "multi-select");
                    Object[] vals = (Object[])property.getValue();
                    JsonArrayBuilder value = Json.createArrayBuilder();
                    if( vals != null )
                    {
                        for( Object val : vals )
                        {
                            value.add(val.toString());
                        }
                    }
                    p.add(VALUE_ATTR, value);
                    return p;
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
                    pCh.add(TYPE_ATTR, (val instanceof Boolean) ? "bool" : "code-string");
                    pCh.add(NAME_ATTR, element.getName());
                    pCh.add(DISPLAY_NAME_ATTR, element.getName());
                    pCh.add(VALUE_ATTR, val.toString());
                    pCh.add(READONLY_ATTR, element.isReadOnly());
                    value.add(pCh);
                }
            }
        }
        p.add(TYPE_ATTR, "collection");
        p.add(VALUE_ATTR, value);
        return p;
    }

    private static JsonObjectBuilder fillCompositeProperty(FieldMap fieldMap, int showMode, Property property, JsonObjectBuilder p)
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
//            Object wrapper = customBeans.get( valueClass ).getConstructor( valueClass ).newInstance( value );
//            p.add(TYPE_ATTR, "composite");
//            p.add(VALUE_ATTR, getModelAsJson(ComponentFactory.getModel( wrapper ), fieldMap.get(property), showMode));
//        }
//        else
        if( Color.class.isAssignableFrom( valueClass ) )
        {
            p.add(TYPE_ATTR, "color-selector");
            p.add(VALUE_ATTR, encodeColor((Color)value));
        }
        else
        {
            p.add(TYPE_ATTR, "composite");
            p.add(VALUE_ATTR, getModelAsJson((CompositeProperty)property, fieldMap.get(property), showMode));
        }
        return p;
    }

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

    private static void initEditor(Property property, PropertyEditorEx editor)
    {
        Object owner = property.getOwner();
        if( owner instanceof Property.PropWrapper )
            owner = ( (Property.PropWrapper)owner ).getOwner();
        editor.setValue(property.getValue());
        editor.setBean(owner);
        editor.setDescriptor(property.getDescriptor());
    }

    private static JsonArrayBuilder jsonArrayBuilderFromList(List<Object> list) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(Object s: list) {
            arrayBuilder.add(s.toString());
        }
        return arrayBuilder;
    }

    /**
     * Counterpart for parseColor
     * @param color color to encode
     * @return array of color components
     */
    public static JsonArrayBuilder encodeColor(Color color)
    {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if(color == null || color.getAlpha() == 0) return arrayBuilder;

        return arrayBuilder.add(color.getRed()).add(color.getGreen()).add(color.getBlue());
    }
}
