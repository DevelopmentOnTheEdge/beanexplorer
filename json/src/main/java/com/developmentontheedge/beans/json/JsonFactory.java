package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static com.developmentontheedge.beans.json.JsonPropertyAttributes.*;
import static java.util.Objects.requireNonNull;

/**
 * Provides API to serialize beans and dynamic property sets to Json. 
 */
public class JsonFactory
{
    public static final Jsonb jsonb = JsonbBuilder.create();

    ///////////////////////////////////////////////////////////////////////////
    // public API
    //
    
    public static JsonObject dps(DynamicPropertySet dps)
    {
        requireNonNull(dps);
        return Json.createObjectBuilder()
                .add("values", dpsValues(dps))
                .add("meta", dpsMeta(dps))
                .add("order", dpsOrder(dps))
                .build();
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
        if (bean instanceof DynamicPropertySet)return dps((DynamicPropertySet) bean);

        return Json.createObjectBuilder()
                .add("values", beanValues(bean, fieldMap))
                .add("meta", beanMeta(bean, fieldMap))
                .add("order", beanOrder(bean, fieldMap))
                .build();
    }

    public static JsonObject beanValues(Object bean)
    {
        return beanValues(bean, FieldMap.ALL);
    }

    public static JsonObject beanValues(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        if (bean instanceof DynamicPropertySet)return dpsValues((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return propertiesValues(property, fieldMap, Property.SHOW_USUAL).build();
    }

    public static JsonObject beanMeta(Object bean)
    {
        return beanMeta(bean, FieldMap.ALL);
    }

    public static JsonObject beanMeta(Object bean, FieldMap fieldMap)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        if (bean instanceof DynamicPropertySet)return dpsMeta((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonObjectBuilder json = Json.createObjectBuilder();
        propertiesMeta(property, fieldMap, Property.SHOW_USUAL, json, new JsonPath());

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
        if (bean instanceof DynamicPropertySet)return dpsOrder((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonArrayBuilder json = Json.createArrayBuilder();
        beanOrder(property, fieldMap, Property.SHOW_USUAL, json, new JsonPath());

        return json.build();
    }

    //public static JsonObject dictionaryValues(Object obj){return null;}

    ///////////////////////////////////////////////////////////////////////////

    private static void dpsOrder(DynamicPropertySet dps, JsonArrayBuilder json, JsonPath path)
    {
        for (DynamicProperty property: dps)
        {
            Object value = property.getValue();

            JsonPath newPath = path.append(property.getName());
            json.add(newPath.get());
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, json, newPath);
        }
    }

    private static void dpsMeta(DynamicPropertySet dps, JsonObjectBuilder json, JsonPath path)
    {
        for(DynamicProperty property : dps)
        {
            JsonPath newPath = path.append(property.getName());
            json.add( newPath.get(), dynamicPropertyMeta(property) );

            if(property.getValue() instanceof DynamicPropertySet)
            {
                dpsMeta((DynamicPropertySet)property.getValue(), json, newPath);
            }
        }
    }

    private static void addValueToObject(JsonObjectBuilder json, String name, Object value)
    {
        if( value == null ){ json.addNull(name);return;}

        Class<?> klass = value.getClass();
        if( klass == String.class ){    json.add(name, (String) value); return;}
        if( klass == Double.class ){    json.add(name, (double)value ); return;}
        if( klass == Long.class ){      json.add(name, (long)value ); return;}
        if( klass == Integer.class ){   json.add(name, (int)value ); return;}
        if( klass == Boolean.class ){   json.add(name, (boolean)value ); return;}
        if( klass == Float.class ){     json.add(name, (float)value ); return;}
        if( klass == BigInteger.class ){json.add(name, (BigInteger) value ); return;}
        if( klass == BigDecimal.class ){json.add(name, (BigDecimal) value ); return;}

        if( klass == Date.class ){json.add(name, value.toString() ); return;}

        if( value instanceof JsonValue )
        {
            json.add(name, (JsonValue)value); return;
        }

        if( value instanceof DynamicPropertySet)
        {
            json.add(name, dpsValuesBuilder((DynamicPropertySet)value));return;
        }

        if( value instanceof Object[])
        {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Object aValueArr : (Object[]) value)addValueToArray(arrayBuilder, aValueArr);
            json.add(name, arrayBuilder.build());
            return;
        }

        CompositeProperty property = ComponentFactory.getModel(value, ComponentFactory.Policy.DEFAULT);
        json.add(name, propertiesValues(property, FieldMap.ALL, Property.SHOW_USUAL).build());
    }

    static void addValueToArray(JsonArrayBuilder json, Object value)
    {
        if( value == null ){ json.addNull(); return; }

        Class<?> klass = value.getClass();
        if( klass == String.class ){    json.add((String) value); return;}
        if( klass == Double.class ){    json.add((double)value ); return;}
        if( klass == Long.class ){      json.add((long)value ); return;}
        if( klass == Integer.class ){   json.add((int)value ); return;}
        if( klass == Boolean.class ){   json.add((boolean)value ); return;}
        if( klass == Float.class ){     json.add((float)value ); return;}
        if( klass == BigInteger.class ){json.add((BigInteger) value ); return;}
        if( klass == BigDecimal.class ){json.add((BigDecimal) value ); return;}

        if( klass == Date.class ){json.add(value.toString() ); return;}

        if( value instanceof JsonValue )
        {
            json.add((JsonValue)value); return;
        }

        if( value instanceof DynamicPropertySet)
        {
            json.add(dpsValuesBuilder((DynamicPropertySet)value));return;
        }

        CompositeProperty property = ComponentFactory.getModel(value, ComponentFactory.Policy.DEFAULT);
        json.add(propertiesValues(property, FieldMap.ALL, Property.SHOW_USUAL).build());
    }

    private static JsonObjectBuilder dpsValuesBuilder(DynamicPropertySet dps)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();
        for(DynamicProperty property : dps)
        {
            addValueToObject(json, property.getName(), property.getValue());
        }
        return json;
    }

    /**
     * Converts DynamicProperty parameter to JsonObject.
     *
     * @param property Operation parameter
     */
    static JsonObject dynamicPropertyMeta(DynamicProperty property)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add(displayName.name(), property.getDisplayName() );

        if(property.getType() != String.class)
        {
            json.add(type.name(), getTypeName(property.getType()) );
        }

        if(property.isHidden())
        {
            json.add(hidden.name(), true );
        }

        addAttr(json, property, reloadOnChange);
        addAttr(json, property, rawValue);
        addAttr(json, property, groupName);
        addAttr(json, property, groupId);
        addAttr(json, property, readOnly);
        addAttr(json, property, canBeNull);
        addAttr(json, property, multipleSelectionList);
        addAttr(json, property, passwordField);
        addAttr(json, property, labelField);
        addAttr(json, property, cssClasses);
        addAttr(json, property, columnSize);
        addAttr(json, property, status);
        addAttr(json, property, message);

        if(!property.getBooleanAttribute( BeanInfoConstants.NO_TAG_LIST ))
        {
            addAttr(json, property, tagList);
        }
        addAttr(json, property, extraAttrs);

        return json.build();
    }

    private static void addAttr(JsonObjectBuilder json, DynamicProperty property, JsonPropertyAttributes attr)
    {
        if(attr.beanInfoConstant != null )
        {
            if(attr.attrType == Boolean.class)
            {
                if(property.getBooleanAttribute(attr.beanInfoConstant))json.add(attr.name(), true );
            }
            else if(attr.attrType == String.class)
            {
                if(property.getStringAttribute(attr.beanInfoConstant) != null)
                    json.add(attr.name(), property.getStringAttribute(attr.beanInfoConstant) );
            }
            else if(attr.attrType == Array.class)
            {
                @SuppressWarnings("unchecked")
                String[][] tags = (String[][])property.getAttribute(attr.beanInfoConstant);
                if( tags != null )
                {
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    for (String[] tag : tags)
                    {
                        arrayBuilder.add(arrayBuilder2.add(tag[0]).add(tag[1]).build());
                    }
                    json.add(attr.name(), arrayBuilder.build());
                }
            }
            else if(property.getAttribute(attr.beanInfoConstant) != null)
            {
                json.add(attr.name(), property.getAttribute(attr.beanInfoConstant).toString() );
            }
        }
    }

    private static String getTypeName(Class<?> klass)
    {
        return klass.getSimpleName();
    }

    private static JsonObjectBuilder propertiesValues(CompositeProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property.getValue() == null){
                json.addNull(property.getName());
                continue;
            }

            if(property instanceof CompositeProperty) {
                json.add(property.getName(), propertiesValues((CompositeProperty)property, fieldMap.get(property), showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(property.getName(), propertiesValues((ArrayProperty) property, fieldMap.get(property), showMode));
                continue;
            }

            if("class".equals(property.getName())){
                continue;
            }

            addValueToObject(json, property.getName(), property.getValue());
        }

        return json;
    }

    private static JsonArrayBuilder propertiesValues(ArrayProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (int i = 0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

            if(property.getValue() == null){
                json.addNull();
                continue;
            }

            if(property instanceof CompositeProperty) {
                json.add(propertiesValues((CompositeProperty)property, fieldMap.get(property), showMode) );
                continue;
            }

            if(property instanceof ArrayProperty) {
                json.add(propertiesValues((ArrayProperty) property, fieldMap.get(property), showMode));
                continue;
            }

            addValueToArray(json, properties.getPropertyAt(i).getValue());
        }
        return json;
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    private static void propertiesMeta(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                       JsonObjectBuilder json, JsonPath path)
    {

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }
            JsonPath newPath = path.append(property.getName());
            if(!property.getName().equals("class"))json.add(newPath.get(), propertyMeta(property));

            if(property instanceof CompositeProperty) {
                propertiesMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty) {
                propertiesMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
            }
        }
    }

    private static void propertiesMeta(ArrayProperty properties, FieldMap fieldMap, int showMode,
                                       JsonObjectBuilder json, JsonPath path)
    {
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }
            JsonPath newPath = path.append(property.getName());
            //if(!property.getName().equals("class"))json.add(newPath.get(), propertyMeta(property));

            if(property instanceof CompositeProperty) {
                propertiesMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty) {
                propertiesMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
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
            if(!key.equals("class"))json.add(newPath.get());
            if( value instanceof DynamicPropertySet)dpsOrder((DynamicPropertySet)value, json, newPath);
        }
    }

    private static JsonObject propertyMeta(Property property)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add(type.name(), getTypeName(property.getValueClass()));

        if(!property.getName().equals(property.getDisplayName()))
        {
            json.add(displayName.name(), property.getDisplayName());
        }

        String shortDescription = property.getShortDescription().split("\n")[0];
        if(!property.getName().equals(shortDescription))
        {
            json.add(description.name(), property.getShortDescription().split("\n")[0]);
        }

        if(property.isReadOnly())
        {
            json.add(readOnly.name(), true);
        }

        return json.build();
    }
//
//    /**
//     * Counterpart for parseColor
//     * @param color color to encode
//     * @return array of color components
//     */
//    static JsonArrayBuilder encodeColor(Color color)
//    {
//        JsonArrayBuilder json = Json.createArrayBuilder();
//        if(color == null || color.getAlpha() == 0) return json;
//
//        return json.add(color.getRed()).add(color.getGreen()).add(color.getBlue());
//    }

}
