package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.editors.CustomEditorSupport;
import com.developmentontheedge.beans.editors.GenericMultiSelectEditor;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.editors.StringTagEditorSupport;
import com.developmentontheedge.beans.editors.TagEditorSupport;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.FieldMap;
import com.developmentontheedge.beans.model.Property;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.developmentontheedge.beans.model.SimpleProperty;
import org.eclipse.yasson.JsonBindingProvider;

import static com.developmentontheedge.beans.json.JsonPropertyAttributes.*;
import static java.util.Objects.requireNonNull;


/**
 * Provides API to serialize beans and dynamic property sets to Json.
 */
public class JsonFactory
{
    public static final Jsonb jsonb = JsonbBuilder.create();
    private JsonBindingProvider jsonBindingProvider = new JsonBindingProvider();//for manifest
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
        return bean(bean, FieldMap.ALL, Property.SHOW_USUAL);
    }

    public static JsonObject bean(Object bean, FieldMap fieldMap, int showMode)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        if (bean instanceof DynamicPropertySet)return dps((DynamicPropertySet) bean);

        return Json.createObjectBuilder()
                .add("values", beanValues(bean, fieldMap, showMode))
                .add("meta", beanMeta(bean, fieldMap, showMode))
                .add("order", beanOrder(bean, fieldMap, showMode))
                .build();
    }

    public static JsonObject beanValues(Object bean)
    {
        return beanValues(bean, FieldMap.ALL, Property.SHOW_USUAL);
    }

    public static JsonObject beanValues(Object bean, FieldMap fieldMap, int showMode)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
//for beanValuesInt, beanValuesString
//        JsonArrayBuilder test = Json.createArrayBuilder();
//        if(addValueToJsonArray(test, bean)){
//            return Json.createValue()
//        }

        if (bean instanceof DynamicPropertySet)return dpsValues((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        return propertiesValues(property, fieldMap, showMode).build();
    }

    public static JsonObject beanMeta(Object bean)
    {
        return beanMeta(bean, FieldMap.ALL, Property.SHOW_USUAL);
    }

    public static JsonObject beanMeta(Object bean, FieldMap fieldMap, int showMode)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        if (bean instanceof DynamicPropertySet)return dpsMeta((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonObjectBuilder json = Json.createObjectBuilder();
        try
        {
            fillCompositePropertyMeta(property, fieldMap, showMode, json, new JsonPath());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);//todo new error
        }

        return json.build();
    }

    public static JsonArray beanOrder(Object bean)
    {
        return beanOrder(bean, FieldMap.ALL, Property.SHOW_USUAL);
    }

    public static JsonArray beanOrder(Object bean, FieldMap fieldMap, int showMode)
    {
        requireNonNull(bean);
        requireNonNull(fieldMap);
        if (bean instanceof DynamicPropertySet)return dpsOrder((DynamicPropertySet) bean);
        CompositeProperty property = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT);

        JsonArrayBuilder json = Json.createArrayBuilder();
        beanOrder(property, fieldMap, showMode, json, new JsonPath());

        return json.build();
    }

    public static void setDpsValues(DynamicPropertySet dps, String json)
    {
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        JsonReader reader = Json.createReader(stream);
        JsonStructure jsonst = reader.read();

        setDpsValues(dps, jsonst, new JsonPath());
    }

    public static void setBeanValues(Object bean, String json)
    {
        setBeanValues(bean, json, FieldMap.ALL, Property.SHOW_USUAL);
    }

    public static void setBeanValues(Object bean, String json, FieldMap fieldMap, int showMode)
    {
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        JsonReader reader = Json.createReader(stream);
        JsonStructure jsonst = reader.read();

        CompositeProperty property = resolveModel(bean);

        try {
            setBeanValues(property, jsonst, new JsonPath(), fieldMap, showMode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private static boolean addValueToJsonObject(JsonObjectBuilder json, String name, Object value)
    {
        if( value == null ){return true;}

        Class<?> klass = value.getClass();
        if( klass == String.class ){    json.add(name, (String) value); return true;}
        if( klass == Double.class ){    json.add(name, (double)value ); return true;}
        if( klass == Long.class ){      json.add(name, (long)value ); return true;}
        if( klass == Integer.class ){   json.add(name, (int)value ); return true;}
        if( klass == Boolean.class ){   json.add(name, (boolean)value ); return true;}
        if( klass == Float.class ){     json.add(name, (float)value ); return true;}
        if( klass == BigInteger.class ){json.add(name, (BigInteger) value ); return true;}
        if( klass == BigDecimal.class ){json.add(name, (BigDecimal) value ); return true;}

        if( klass == Date.class ){json.add(name, value.toString() ); return true;}
        if( klass == java.util.Date.class ){json.add(name, new java.sql.Date(((java.util.Date)value).getTime()).toString() ); return true;}

        if( value instanceof JsonValue )
        {
            json.add(name, (JsonValue)value); return true;
        }

        if( value instanceof DynamicPropertySet)
        {
            json.add(name, dpsValuesBuilder((DynamicPropertySet)value));return true;
        }

        if( Color.class.isAssignableFrom( value.getClass() ) )
        {
            json.add(name, encodeColor((Color)value));return true;
        }

        if( value instanceof Object[])
        {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Object aValueArr : (Object[]) value) addValueToJsonArray(arrayBuilder, aValueArr);
            json.add(name, arrayBuilder.build());
            return true;
        }

        json.add(name, value.toString());
        return true;
    }

    static boolean addValueToJsonArray(JsonArrayBuilder json, Object value)
    {
        if( value == null ){return true; }

        Class<?> klass = value.getClass();
        if( klass == String.class ){    json.add((String) value); return true;}
        if( klass == Double.class ){    json.add((double)value ); return true;}
        if( klass == Long.class ){      json.add((long)value ); return true;}
        if( klass == Integer.class ){   json.add((int)value ); return true;}
        if( klass == Boolean.class ){   json.add((boolean)value ); return true;}
        if( klass == Float.class ){     json.add((float)value ); return true;}
        if( klass == BigInteger.class ){json.add((BigInteger) value ); return true;}
        if( klass == BigDecimal.class ){json.add((BigDecimal) value ); return true;}

        if( klass == Date.class ){json.add(value.toString() ); return true;}
        if( klass == java.util.Date.class ){json.add(new java.sql.Date(((java.util.Date)value).getTime()).toString() ); return true;}

        if( value instanceof JsonValue )
        {
            json.add((JsonValue)value); return true;
        }

        if( value instanceof DynamicPropertySet)
        {
            json.add(dpsValuesBuilder((DynamicPropertySet)value));return true;
        }

        if( Color.class.isAssignableFrom( value.getClass() ) )
        {
            json.add(encodeColor((Color)value));return true;
        }

        json.add(value.toString());
        return true;
    }

    private static JsonObjectBuilder dpsValuesBuilder(DynamicPropertySet dps)
    {
        JsonObjectBuilder json = Json.createObjectBuilder();
        for(DynamicProperty property : dps)
        {
            addValueToJsonObject(json, property.getName(), property.getValue());
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
        addAttr(json, property, reloadOnFocusOut);
        addAttr(json, property, rawValue);
        addAttr(json, property, groupId);
        addAttr(json, property, groupName);
        addAttr(json, property, groupClasses);
        addAttr(json, property, readOnly);
        addAttr(json, property, canBeNull);
        addAttr(json, property, multipleSelectionList);
        addAttr(json, property, passwordField);
        addAttr(json, property, labelField);
        addAttr(json, property, cssClasses);
        addAttr(json, property, columnSize);
        addAttr(json, property, inputSize);
        addAttr(json, property, placeholder);
        addAttr(json, property, status);
        addAttr(json, property, message);
        addAttr(json, property, defaultValue);

        if(!property.getBooleanAttribute( BeanInfoConstants.NO_TAG_LIST ))
        {
            addAttr(json, property, tagList);
        }
        addAttr(json, property, extraAttrs);
        addAttr(json, property, validationRules);

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
                Object tagsObject = property.getAttribute(attr.beanInfoConstant);
                if (tagsObject != null)
                {
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    if (tagsObject instanceof Object[][])
                    {
                        for (Object[] tag : ((Object[][]) tagsObject))
                        {
                            arrayBuilder.add(arrayBuilder2.add(tag[0].toString()).add(tag[1].toString()).build());
                        }
                    }
                    else if (tagsObject instanceof Object[])
                    {
                        for (Object tag : ((Object[]) tagsObject))
                        {
                            arrayBuilder.add(arrayBuilder2.add(tag.toString()).add(tag.toString()).build());
                        }
                    }
                    else if (tagsObject instanceof Map<?,?>)
                    {
                        for (Map.Entry tag : ((Map<?,?>) tagsObject).entrySet())
                        {
                            arrayBuilder.add(arrayBuilder2.add(tag.getKey().toString())
                                                          .add(tag.getValue().toString()).build());
                        }
                    }
                    json.add(attr.name(), arrayBuilder.build());
                }
            }
            else if(attr.attrType == JsonPropertyAttributes.POJOorListOfPOJO.class)
            {
                Object value = property.getAttribute(attr.beanInfoConstant);

                if(value instanceof List){
                    List list = (List)value;
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    for (Object valueItem : list){
                        if(valueItem != null)arrayBuilder.add(beanValues(valueItem));
                    }
                    json.add(attr.name(), arrayBuilder.build());
                }else{
                    if(value != null)json.add(attr.name(), beanValues(value));
                }
            }
            else if(property.getAttribute(attr.beanInfoConstant) != null)
            {
                json.add(attr.name(), property.getAttribute(attr.beanInfoConstant).toString() );
            }
        }
    }

    private static CompositeProperty resolveModel(Object bean)
    {
        CompositeProperty model;
        if( bean instanceof CompositeProperty )
        {
            model = (CompositeProperty)bean;
        }
        else
        {
            model = ComponentFactory.getModel(bean, ComponentFactory.Policy.UI, true);
        }
        return model;
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

//            if(property.getValue() == null){
//                json.addNull(property.getName());
//                continue;
//            }

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

            addValueToJsonObject(json, property.getName(), property.getValue());
        }

        return json;
    }

    private static JsonArrayBuilder propertiesValues(ArrayProperty properties, FieldMap fieldMap, int showMode)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();

        for (int i = 0; i < properties.getPropertyCount(); i++)
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) )
            {
                continue;
            }

//            if(property.getValue() == null)
//            {
//                json.addNull();
//                continue;
//            }

            if(property instanceof CompositeProperty)
            {
                json.add(propertiesValues((CompositeProperty)property, fieldMap.get(property), showMode) );
                continue;
            }

            if(property instanceof ArrayProperty)
            {
                json.add(propertiesValues((ArrayProperty) property, fieldMap.get(property), showMode));
                continue;
            }

            addValueToJsonArray(json, properties.getPropertyAt(i).getValue());
        }
        return json;
    }

    /**
     * Convert model to Json
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    private static void fillCompositePropertyMeta(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                                  JsonObjectBuilder json, JsonPath path) throws Exception
    {

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);

            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) )continue;

            JsonPath newPath = path.append(property.getName());

            fillSimplePropertyMeta(property, json, newPath);

            if(property instanceof CompositeProperty)
            {
                if( property.getValue() instanceof DynamicPropertySet)
                {
                    dpsMeta((DynamicPropertySet)property.getValue(), json, newPath);
                    continue;
                }

                fillCompositePropertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty)
            {
                fillArrayPropertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
            }
        }
    }

    private static void fillArrayPropertyMeta(ArrayProperty properties, FieldMap fieldMap, int showMode,
                                              JsonObjectBuilder json, JsonPath path) throws Exception
    {
        for( int j = 0; j < properties.getPropertyCount(); j++ )
        {
            Property property = properties.getPropertyAt(j);

            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) continue;

            JsonPath newPath = path.append(property.getName());

            fillSimplePropertyMeta(property, json, newPath);

            if(property instanceof CompositeProperty)
            {
                if( property.getValue() instanceof DynamicPropertySet)
                {
                    dpsMeta((DynamicPropertySet)property.getValue(), json, newPath);
                    continue;
                }

                fillCompositePropertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty)
            {
                fillArrayPropertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
            }
        }
    }

    private static boolean fillSimplePropertyMeta(Property property, JsonObjectBuilder json, JsonPath path) throws Exception
    {
        JsonObjectBuilder p = Json.createObjectBuilder();
        p.add(displayName.name(), property.getDisplayName());
        p.add(description.name(), property.getShortDescription().split("\n")[0]);
        if(property.isReadOnly())p.add(readOnly.name(), property.isReadOnly());

        Class<?> clazz;
        if(property instanceof SimpleProperty){
            clazz = ((SimpleProperty) property).getValueClass();
        }else if(property instanceof ArrayProperty){
            clazz = ((ArrayProperty) property).getItemClass();
        }else{
            clazz = property.getClass();
        }

        if(clazz != String.class)
        {
            p.add(type.name(), getTypeName(clazz) );
        }

        if(property instanceof ArrayProperty)
        {
            p.add(COLLECTION, true);
        }

        Class<?> editorClass = property.getPropertyEditorClass();

        if( editorClass != null )
        {
            if( JsonSerializable.class.isAssignableFrom(editorClass) )
            {
                JsonSerializable editor = (JsonSerializable)editorClass.newInstance();
                if( editor instanceof PropertyEditorEx )
                {
                    initEditor( property, (PropertyEditorEx)editor );
//todo                    JSONObject p1 = editor.toJson();
//                    if( p1 != null )
//                    {
//                        Iterator<?> iterator = p1.keys();
//                        while( iterator.hasNext() )
//                        {
//                            String key = iterator.next().toString();
//                            if( key.equals("dictionary") )
//                            {
//                                JsonArray array = p1.optJsonArray("dictionary");
//                                if( array != null )
//                                {
//                                    String[] elements = new String[array.length()];
//                                    for( int index = 0; index < array.length(); index++ )
//                                        elements[index] = array.optString(index);
//                                    p.put(DICTIONARY_ATTR, createDictionary(elements, false));
//                                }
//                            }
//                            else
//                            {
//                                p.put(key, p1.get(key));
//                            }
//                        }
//                        return p;
//                    }
                }
            }
            else if( TagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                TagEditorSupport editor = (TagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.add(dictionary.name(), createDictionary(tags, true));
                }
            }
            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
            {
                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.add(dictionary.name(), createDictionary(tags, false));
                }
            }
            else if( CustomEditorSupport.class.isAssignableFrom(editorClass) )
            {
                //TODO: support or correctly process some editors
                //Some editors like biouml.model.util.ReactionEditor, biouml.model.util.FormulaEditor
                //use Application.getApplicationFrame(), so we got a NullPointerException here
                CustomEditorSupport editor = (CustomEditorSupport)editorClass.newInstance();
                initEditor( property, editor );
                String[] tags = editor.getTags();
                if( tags != null )
                {
                    p.add(dictionary.name(), createDictionary(tags, false));
                }
                if(property instanceof ArrayProperty)
                {
                    p.add(multipleSelectionList.name(), true);
                }
            }

            json.add(path.get(), p);
            return false;
        }

        json.add(path.get(), p);
        return true;
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

    /**
     * Counterpart for parseColor
     * @param color color to encode
     * @return array of color components
     */
    private static JsonArray encodeColor(Color color)
    {
        JsonArrayBuilder json = Json.createArrayBuilder();
        if(color == null || color.getAlpha() == 0) return json.build();

        return json.add(color.getRed()).add(color.getGreen()).add(color.getBlue()).build();
    }

    private static JsonArrayBuilder createDictionary(Object[] strings, boolean byPosition)
    {
        if( strings == null )
            strings = new Object[] {};
        int position = 0;
        JsonArrayBuilder values = Json.createArrayBuilder();
        JsonArrayBuilder pair = Json.createArrayBuilder();
        for( Object tagObj : strings )
        {
            String tag = tagObj.toString();
            if( byPosition )
                values.add(pair.add(position).add(tag).build());
            else
                values.add(pair.add(tag).add(tag).build());
            position++;
        }
        return values;
    }

    private static void setBeanValues(CompositeProperty properties, JsonStructure jsonst, JsonPath path, FieldMap fieldMap, int showMode) throws Exception
    {
        //JsonObject object = getJsonObject(json);

        //Set<Map.Entry<String, JsonValue>> entries = object.entrySet();
        //return jsonb.fromJson(json, clazz);

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            JsonPath newPath = path.append(property.getName());

            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }

//            if(property instanceof CompositeProperty) {
//                setBeanValues((CompositeProperty)property, jsonst, newPath, fieldMap.get(property), showMode);
//                continue;
//            }
//
//
            if(property instanceof ArrayProperty) {
                setBeanValues((ArrayProperty) property, jsonst, path, fieldMap.get(property), showMode);
                continue;
            }

            Object value = getSimpleValueFromJson(property.getValueClass(), jsonst.getValue(newPath.get()));
            try {
                property.setValue(value);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            //addValueToJsonObject(json, property.getName(), property.getValue());
        }
    }

    private static void setBeanValues(ArrayProperty property, JsonStructure jsonst, JsonPath path, FieldMap fieldMap, int showMode) throws Exception
    {
//        Class<?> c = property.getPropertyEditorClass();
//        if( c != null )
//        {
//            if( GenericMultiSelectEditor.class.isAssignableFrom(c) )
//            {
//                GenericMultiSelectEditor editor = (GenericMultiSelectEditor)c.newInstance();
//                initEditor( property, editor );
//
//                JsonArray jsonArray = (JsonArray)jsonst.getValue( path.get() );
//                String[] val = jsonArray.stream()
//                        .map(x -> ((JsonString) x).getString())
//                        .collect(Collectors.toList()).toArray(new String[jsonArray.size()]);
//                editor.setStringValue(val);
//                property.setValue(editor.getValue());//setValue(property, editor.getValue());
//                return;
//            }
////            else if( JSONCompatibleEditor.class.isAssignableFrom(c) )
////            {
////                JSONCompatibleEditor editor = (JSONCompatibleEditor)c.newInstance();
////                editor.fillWithJSON(property, jsonObject);
////                return;
////            }
//        }
//
//        JsonArray jsonArray = (JsonArray)jsonst.getValue( path.get() );
//        //process array actions
//        if( !property.isReadOnly() )
//        {
//            while( jsonArray.size() > property.getPropertyCount() )
//            {
//                property.insertItem(property.getPropertyCount(), null);
//            }
//            while( jsonArray.size() < property.getPropertyCount() )
//            {
//                property.removeItem(property.getPropertyCount() - 1);
//            }
//        }
//        Object oldValue = property.getValue();
//        if( oldValue != null && oldValue.getClass().isArray() )
//        {
//            Property element = property.getPropertyAt(0);
//            if( element instanceof SimpleProperty ) //Simple Properties processing
//            {
//                for( int k = 0; k < property.getPropertyCount(); k++ )
//                {
//                    Property oldElement = property.getPropertyAt(k);
//                    String elemName = oldElement.getName();
//                    for( int m = 0; m < jsonArray.size(); m++ )
//                    {
//                        JsonArray jsonBean = jsonArray.getJsonArray(m);
//                        for( int ind = 0; ind < jsonBean.size(); ind++ )
//                        {
//                            JsonObject jsonProperty = jsonBean.getJsonObject(ind);
//                            if( jsonProperty.getString("name").equals(elemName) )
//                            {
//                                Object value = getSimpleValueFromJson( oldElement.getValueClass(), jsonProperty);
//                                oldElement.setValue(value);//setValue( oldElement, value );
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            else
//            {
//                Object[] oldArray = (Object[])oldValue;
//
//                int index = 0;
//                for( Object oldObject : oldArray )
//                {
//                    CompositeProperty elementModel = resolveModel( oldObject );
//                    if( jsonArray.size() > index )
//                    {
//                        JsonValue jsonValue = jsonArray.get(index);
//                        //setBeanValues(elementModel, jsonst, path.append( ?? ), fieldMap, showMode);//correctBeanOptions(elementModel, jsonBean);
//                        throw new RuntimeException("todo");
//                    }
//                    index++;
//                }
//            }
//        }
//        if( jsonObject.has("action") && !property.isReadOnly() )
//        {
//            String actionName = jsonObject.getString("action");
//            if( actionName.equals("item-add") )
//            {
//                property.insertItem( property.getPropertyCount(), null);
//            }
//            else if( actionName.equals("item-remove") )
//            {
//                int size = property.getPropertyCount();
//                if( size > 0 )
//                {
//                    property.removeItem(size - 1);
//                }
//            }
//        }
    }

    private static void setDpsValues(DynamicPropertySet dps, JsonStructure jsonst, JsonPath path)
    {
        for (DynamicProperty property: dps)
        {
            JsonPath newPath = path.append(property.getName());

            property.setValue(getSimpleValueFromJson(property.getType(), jsonst.getValue(newPath.get())));
        }
    }

    private static Object getSimpleValueFromJson(Class<?> klass, JsonValue value)
    {
        if(value.getValueType() == JsonValue.ValueType.NULL){
            return null;
        }

        if(value.getValueType() == JsonValue.ValueType.STRING)
        {
            if (klass == String.class) {
                return ((JsonString) value).getString();
            }
        }

        if(value.getValueType() == JsonValue.ValueType.NUMBER)
        {
            if (klass == Double.class) {
                return ((JsonNumber) value).doubleValue();
            }
            if (klass == Float.class) {
                return ((JsonNumber) value).doubleValue();
            }
            if (klass == Long.class) {
                return ((JsonNumber) value).longValue();
            }
            if (klass == Integer.class) {
                return ((JsonNumber) value).intValue();
            }
            if (klass == BigInteger.class) {
                return ((JsonNumber) value).bigIntegerValue();
            }
            if (klass == BigDecimal.class) {
                return ((JsonNumber) value).bigDecimalValue();
            }
        }

        if(value.getValueType() == JsonValue.ValueType.FALSE || value.getValueType() == JsonValue.ValueType.TRUE)
        {
            return value.getValueType() == JsonValue.ValueType.TRUE;
        }

        //if( klass == Boolean.class ){return ((Json)value); }

//        if( klass == Date.class ){json.add(name, value.toString() ); return true;}
//        if( klass == java.util.Date.class ){json.add(name, new java.sql.Date(((java.util.Date)value).getTime()).toString() ); return true;}
//
//        if( value instanceof JsonValue )
//        {
//            json.add(name, (JsonValue)value); return true;
//        }
//
//        if( value instanceof DynamicPropertySet)
//        {
//            json.add(name, dpsValuesBuilder((DynamicPropertySet)value));return true;
//        }
//
//        if( Color.class.isAssignableFrom( value.getClass() ) )
//        {
//            json.add(name, encodeColor((Color)value));return true;
//        }
//
//        if( value instanceof Object[])
//        {
//            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//            for (Object aValueArr : (Object[]) value) addValueToJsonArray(arrayBuilder, aValueArr);
//            json.add(name, arrayBuilder.build());
//            return true;
//        }
//
//        json.add(name, value.toString());
//        return true;
        throw new RuntimeException("todo");
    }

    private static final String COLLECTION = "collection";
}
