package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import static com.developmentontheedge.beans.json.JsonPropertyAttributes.*;
import static java.util.Objects.requireNonNull;

/**
 * Provides API to serialize beans and dynamic property sets to Json. 
 */
public class JsonFactory
{
    private static final Logger log = Logger.getLogger(JsonFactory.class.getName());

    ///////////////////////////////////////////////////////////////////////////
    // public API
    //
    
    public static JsonObject dps(DynamicPropertySet dps)
    {
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

        return dpsValuesBuilder(dps).build();
    }

    public static JsonObject dpsMeta(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonObjectBuilder json = Json.createObjectBuilder();
        dpsMeta(dps, json, new JsonPath());

        return json.build();
    }

    public static JsonArray dpsOrder(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonArrayBuilder json = Json.createArrayBuilder();
        dpsOrder(dps, json, new JsonPath());

        return json.build();
    }

    public static JsonObject bean(Object bean)
    {
        return bean(bean, FieldMap.ALL);
    }

    public static JsonObject bean(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);

        JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("values", beanValues(bean, fieldMap));
        result.add("meta", beanMeta(bean, fieldMap));
        result.add("order", beanOrder(bean, fieldMap));
        return result.build();
    }

    public static JsonObject beanValues(Object bean)
    {
        return beanValues(bean, FieldMap.ALL);
    }

    public static JsonObject beanValues(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return propertyValue(property, fieldMap, Property.SHOW_USUAL).build();
    }

    public static JsonObject beanMeta(Object bean)
    {
        return beanMeta(bean, FieldMap.ALL);
    }

    public static JsonObject beanMeta(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonObjectBuilder json = Json.createObjectBuilder();
        propertyMeta(property, fieldMap, Property.SHOW_USUAL, json, new JsonPath());

        return json.build();
    }

    public static JsonArray beanOrder(Object bean)
    {
        return beanOrder(bean, FieldMap.ALL);
    }

    public static JsonArray beanOrder(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonArrayBuilder json = Json.createArrayBuilder();
        beanOrder(property, fieldMap, Property.SHOW_USUAL, json, new JsonPath());

        return json.build();
    }

    //public static JsonObject dictionaryValues(Object obj){return null;}

    ///////////////////////////////////////////////////////////////////////////

    private static void dpsOrder(DynamicPropertySet dps, JsonArrayBuilder json, JsonPath path)
    {
        for (Map.Entry<String, Object> entry :dps.asMap().entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            JsonPath newPath = path.append(key);
            json.add(newPath.get());
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, json, newPath);
        }
    }

    private static void dpsMeta(DynamicPropertySet dps, JsonObjectBuilder json, JsonPath path)
    {
        for( DynamicProperty dynamicProperty : dps )
        {
            JsonPath newPath = path.append(dynamicProperty.getName());
            json.add( newPath.get(), dynamicPropertyMeta(dynamicProperty) );

            if(dynamicProperty.getValue() instanceof DynamicPropertySet)
            {
                dpsMeta((DynamicPropertySet)dynamicProperty.getValue(), json, newPath);
            }
        }
    }

    static void addToJsonObject(JsonObjectBuilder json, String name, Object value, Class<?> type)
    {
        requireNonNull(type);
        if( value == null ){ json.addNull(name);return;}

        if( type == String.class ){    json.add(name, (String) value); return;}
        if( type == Double.class ){    json.add(name, (double)value ); return;}
        if( type == Long.class ){      json.add(name, (long)value ); return;}
        if( type == Integer.class ){   json.add(name, (int)value ); return;}
        if( type == Boolean.class ){   json.add(name, (boolean)value ); return;}
        if( type == Float.class ){     json.add(name, (float)value ); return;}
        if( type == BigInteger.class ){json.add(name, (BigInteger) value ); return;}
        if( type == BigDecimal.class ){json.add(name, (BigDecimal) value ); return;}

        if( type == JsonValue.class ){json.add(name, (JsonValue)value); return;}
        if( type == JsonObjectBuilder.class ){json.add(name, (JsonObjectBuilder)value ); return;}
        if( type == JsonArrayBuilder.class ){json.add(name, (JsonArrayBuilder)value ); return;}

        if( value instanceof DynamicPropertySet){json.add(name, dpsValuesBuilder((DynamicPropertySet)value));return;}

        log.warning("Skipped value: " + name+": "+value);
    }

    static void addToJsonArray(JsonArrayBuilder json, Object value)
    {
        addToJsonArray(json, value, value.getClass());
    }

    private static void addToJsonArray(JsonArrayBuilder json, Object value, Class<?> type)
    {
        requireNonNull(type);
        if( value == null ){ json.addNull(); return; }

        if( type == String.class ){    json.add((String) value); return;}
        if( type == Double.class ){    json.add((double)value ); return;}
        if( type == Long.class ){      json.add((long)value ); return;}
        if( type == Integer.class ){   json.add((int)value ); return;}
        if( type == Boolean.class ){   json.add((boolean)value ); return;}
        if( type == Float.class ){     json.add((float)value ); return;}
        if( type == BigInteger.class ){json.add((BigInteger) value ); return;}
        if( type == BigDecimal.class ){json.add((BigDecimal) value ); return;}

        if( type == JsonValue.class ){json.add((JsonValue)value); return;}
        if( type == JsonObjectBuilder.class ){json.add((JsonObjectBuilder)value ); return;}
        if( type == JsonArrayBuilder.class ){json.add((JsonArrayBuilder)value ); return;}

        if( value instanceof DynamicPropertySet){json.add(dpsValuesBuilder((DynamicPropertySet)value));return;}

        log.warning("Skipped value: " + value);
    }

    private static JsonObjectBuilder dpsValuesBuilder(DynamicPropertySet dps)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();
        for (Map.Entry<String, Object> entry :dps.asMap().entrySet())
        {
            addToJsonObject(json, entry.getKey(), entry.getValue(), dps.getProperty(entry.getKey()).getType());
        }
        return json;
    }

    /**
     * Converts DynamicProperty parameter to JsonObject.
     *
     * @param property Operation parameter
     */
    public static JsonObject dynamicPropertyMeta(DynamicProperty property)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add(DISPLAY_NAME_ATTR, property.getDisplayName() );

        if(property.getType() != String.class)
        {
            json.add(TYPE_ATTR, getTypeName(property.getType()) );
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
        if(columnSizeAttr != null)
        {
            json.add( COLUMN_SIZE_ATTR, "" + columnSizeAttr );
        }

        if(!Boolean.TRUE.equals( property.getAttribute( BeanInfoConstants.NO_TAG_LIST ) ))
        {
            @SuppressWarnings("unchecked")
            Map<String, String> tags = (Map<String, String>)property.getAttribute( BeanInfoConstants.TAG_LIST_ATTR );

            if( tags != null )
            {
                json.add( "tagList", mapValues(tags) );
            }
        }

        if(property.getBooleanAttribute( BeanInfoConstants.RELOAD_ON_CHANGE ))
        {
            json.add(RELOAD_ON_CHANGE_ATTR, true );
        }

        if(property.getBooleanAttribute( BeanInfoConstants.RAW_VALUE ))
        {
            json.add(RAW_VALUE_ATTR, true );
        }

        if(property.getStringAttribute(BeanInfoConstants.GROUP_NAME) != null)
        {
            json.add(GROUP_NAME, property.getStringAttribute(BeanInfoConstants.GROUP_NAME) );
        }

        if(property.getAttribute(BeanInfoConstants.GROUP_ID) != null)
        {
            json.add(GROUP_ID, Long.parseLong(property.getAttribute(BeanInfoConstants.GROUP_ID).toString()) );
        }

        return json.build();
    }

    private static JsonArray mapValues(Map<?, ?> map)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (Map.Entry<?, ?> item : map.entrySet())
        {
            json.add(Json.createObjectBuilder()
                    .add(item.getKey().toString(), item.getValue().toString())
                    .build());
        }

        return json.build();
    }

    private static String getTypeName(Class<?> klass)
    {
        return klass.getSimpleName();
    }

    private static JsonObjectBuilder propertyValue(CompositeProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property instanceof CompositeProperty) {
                if(property.getValue() instanceof Map){
                    json.add(property.getName(), mapValues((Map)property.getValue()) );
                    continue;
                }
                json.add(property.getName(), propertyValue((CompositeProperty)property, fieldMap.get(property), showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(property.getName(), propertyValue((ArrayProperty) property, fieldMap.get(property), showMode));
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

    private static JsonArrayBuilder propertyValue(ArrayProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (int i = 0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property instanceof CompositeProperty) {
                if(property.getValue() instanceof Map){
                    json.add(mapValues((Map)property.getValue()) );
                    continue;
                }
                json.add(propertyValue((CompositeProperty)property, fieldMap.get(property), showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(propertyValue((ArrayProperty) property, fieldMap.get(property), showMode));
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
    private static void propertyMeta(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                     JsonObjectBuilder json, JsonPath path)
    {

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }
            JsonPath newPath = path.append(property.getName());
            json.add(newPath.get(), propertyMeta(property));

            if(property instanceof CompositeProperty) {
                if(property.getValue() instanceof Map){
                    continue;
                }
                propertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty) {
                propertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
            }
        }
    }

    private static void propertyMeta(ArrayProperty properties, FieldMap fieldMap, int showMode,
                                     JsonObjectBuilder json, JsonPath path)
    {
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            //json.add(property.getName(), propertyMeta(property));

            if(property instanceof CompositeProperty) {
                propertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, path);
                continue;
            }

            if(property instanceof ArrayProperty) {
                propertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, path);
            }
        }
    }

    private static void beanOrder(CompositeProperty properties, FieldMap fieldMap, int showMode, JsonArrayBuilder json, JsonPath path)
    {
        for (int i=0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }
            String key = property.getName();
            Object value = property.getValue();

            JsonPath newPath = path.append(key);
            json.add(newPath.get());
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, json, newPath);
        }
    }

    private static JsonObject propertyMeta(Property property)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        if(property.getValue() instanceof Map){
            json.add(TYPE_ATTR, "Map");
        }else{
            json.add(TYPE_ATTR, getTypeName(property.getValueClass()));
        }

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
//            return propertyMeta( (CompositeProperty)property, fieldMap, showMode);
//        }
//        if( property instanceof ArrayProperty && !property.isHideChildren() )
//        {
//            propertyMeta( (ArrayProperty)property, fieldMap, showMode);
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
//                    JsonObject p1 = editor.mapValues();
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
