package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
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

        return dpsValuesBuilder(dps).build();
    }

    public static JsonObject dpsMeta(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonObjectBuilder json = Json.createObjectBuilder();
        dpsMeta(dps, json, "");

        return json.build();
    }

    public static JsonArray dpsOrder(DynamicPropertySet dps)
    {
        requireNonNull(dps);

        JsonArrayBuilder json = Json.createArrayBuilder();
        dpsOrder(dps, json, "");

        return json.build();
    }

    public static JsonObject beanValues(Object bean)
    {
        requireNonNull(bean);
        return JsonBeanFactory.beanValues(bean).build();
    }

    public static JsonObject beanMeta(Object bean)
    {
        requireNonNull(bean);
        return JsonBeanFactory.beanMeta(bean).build();
    }
    
    public static JsonObject dictionaryValues(Object obj)
    {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static void dpsOrder(DynamicPropertySet dps, JsonArrayBuilder json, String path)
    {
        for (Map.Entry<String, Object> entry :dps.asMap().entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            json.add((path.equals("") ? "" : path + "/") + key);
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, json, path + "/" + key);
        }
    }

    private static void dpsMeta(DynamicPropertySet dps, JsonObjectBuilder json, String path)
    {
        for( DynamicProperty dynamicProperty : dps )
        {
            json.add( (path.equals("") ? "" : path + "/") + dynamicProperty.getName(), getMeta(dynamicProperty) );

            if(dynamicProperty.getValue() instanceof DynamicPropertySet)
            {
                dpsMeta((DynamicPropertySet)dynamicProperty.getValue(), json, path + "/" + dynamicProperty.getName());
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

        if( value instanceof DynamicPropertySet){json.add(name, dpsValuesBuilder((DynamicPropertySet)value));}
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

        if( value instanceof DynamicPropertySet){json.add(dpsValuesBuilder((DynamicPropertySet)value));}
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
    public static JsonObject getMeta(DynamicProperty property)
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

        if( property.getBooleanAttribute( BeanInfoConstants.RAW_VALUE ))
        {
            json.add(RAW_VALUE_ATTR, true );
        }

        return json.build();
    }

    static String getTypeName(Class<?> klass) {
        return klass.getSimpleName();
    }
}
