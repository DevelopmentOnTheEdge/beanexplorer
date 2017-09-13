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
import com.developmentontheedge.beans.model.SimpleProperty;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

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
//        if(addValueToArray(test, bean)){
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

    private static boolean addValueToObject(JsonObjectBuilder json, String name, Object value)
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

        if( value instanceof Object[])
        {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Object aValueArr : (Object[]) value)addValueToArray(arrayBuilder, aValueArr);
            json.add(name, arrayBuilder.build());
            return true;
        }

        return false;
    }

    static boolean addValueToArray(JsonArrayBuilder json, Object value)
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

        return false;
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
        addAttr(json, property, reloadOnFocusOut);
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
                    if (tagsObject instanceof String[][])
                    {
                        for (String[] tag : ((String[][]) tagsObject))
                        {
                            arrayBuilder.add(arrayBuilder2.add(tag[0]).add(tag[1]).build());
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
    private static void fillCompositePropertyMeta(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                                  JsonObjectBuilder json, JsonPath path) throws Exception
    {

        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
                continue;
            }
            JsonPath newPath = path.append(property.getName());

            if(!property.getName().equals("class")){
                convertSinglePropertyMeta(property, fieldMap.get(property), showMode, json, newPath);
            }

            if(property instanceof CompositeProperty) {
                fillCompositePropertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
                continue;
            }

            if(property instanceof ArrayProperty) {
                fillArrayPropertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
            }
        }
    }

    private static void fillArrayPropertyMeta(ArrayProperty property, FieldMap fieldMap, int showMode,
                                              JsonObjectBuilder json, JsonPath path) throws Exception
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
                    json.add(dictionary.name(), createDictionary(tags, false));
                    json.add(type.name(), "multi-select");
                    Object[] vals = (Object[])property.getValue();
                    JsonArrayBuilder value = Json.createArrayBuilder();
                    if( vals != null )
                    {
                        for( Object val : vals )
                        {
                            value.add(val.toString());
                        }
                    }
                    /*json.add(VALUE_ATTR, value);*/
                    return;
                }
//                if( editor instanceof JSONCompatibleEditor )
//                {
//                    ( (JSONCompatibleEditor)editor ).addAsJSON(property, p, fieldMap, showMode);
//                    return p;
//                }
            }
        }
        /*JsonArrayBuilder value = Json.createArrayBuilder();*/
        for( int j = 0; j < property.getPropertyCount(); j++ )
        {
            Property element = property.getPropertyAt(j);
            if( element instanceof CompositeProperty )
            {
                JsonPath newPath = path.append(property.getName());
                getModelAsJSON((CompositeProperty)element, fieldMap.get(property), showMode, json, newPath);
            }
            else
            {
                JsonObjectBuilder pCh = Json.createObjectBuilder();
                Object val = element.getValue();
                if( val != null )
                {
                    pCh.add(type.name(), (val instanceof Boolean) ? "bool" : "code-string");
                    pCh.add(NAME_ATTR, element.getName());
                    pCh.add(displayName.name(), element.getName());
                    pCh.add(VALUE_ATTR, val.toString());
                    pCh.add(readOnly.name(), element.isReadOnly());
                    /*value.add(Json.createArrayBuilder().add(pCh).build());*/
                }
            }
        }
        json.add(type.name(), "collection");
        /*json.add(VALUE_ATTR, value);*/
//        for( int i = 0; i < properties.getPropertyCount(); i++ )
//        {
//            Property property = properties.getPropertyAt(i);
//            if( !property.isVisible(showMode) || !fieldMap.contains(property.getName()) ) {
//                continue;
//            }
//            JsonPath newPath = path.append(property.getName());
//            //if(!property.getName().equals("class"))json.add(newPath.get(), convertSinglePropertyMeta(property));
//
//            if(property instanceof CompositeProperty) {
//                fillCompositePropertyMeta((CompositeProperty) property, fieldMap.get(property), showMode, json, newPath);
//                continue;
//            }
//
//            if(property instanceof ArrayProperty) {
//                fillArrayPropertyMeta((ArrayProperty) property, fieldMap.get(property), showMode, json, newPath);
//            }
//        }
    }

    private static void fillSimplePropertyMeta(SimpleProperty property, FieldMap fieldMap, int showMode,
                                               JsonObjectBuilder json, JsonPath path) throws Exception
    {
        JsonObjectBuilder p = Json.createObjectBuilder();

        Class<?> editorClass = property.getPropertyEditorClass();
        if( editorClass != null )
        {
            if( JsonSerializable.class.isAssignableFrom(editorClass) )
            {
                JsonSerializable editor = (JsonSerializable)editorClass.newInstance();
                if( editor instanceof PropertyEditorEx )
                {
                    initEditor( property, (PropertyEditorEx)editor );
//todo                    JSONObject p1 = editor.toJSON();
//                    if( p1 != null )
//                    {
//                        Iterator<?> iterator = p1.keys();
//                        while( iterator.hasNext() )
//                        {
//                            String key = iterator.next().toString();
//                            if( key.equals("dictionary") )
//                            {
//                                JSONArray array = p1.optJSONArray("dictionary");
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
                        p.add(DICTIONARY_ATTR, createDictionary(tags, false));
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
            p.add(TYPE_ATTR, (value instanceof Boolean) ? "bool" : "code-string");
            //todo p.add(VALUE_ATTR, value.toString());
        }
        json.add(property.getName(), p);
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

    private static void convertSinglePropertyMeta(Property property, FieldMap fieldMap, int showMode,
                                                  JsonObjectBuilder json, JsonPath path) throws Exception
    {
        String name = property.getName();
        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
            return;
        JsonObjectBuilder p = Json.createObjectBuilder();
        //p.add(NAME_ATTR, name);
        p.add(DISPLAYNAME_ATTR, property.getDisplayName());
        p.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        p.add(READONLY_ATTR, property.isReadOnly());
        json.add(name, p);

        JsonPath newPath = path.append(property.getName());
        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )//|| property.getPropertyEditorClass() == PenEditor.class
        {
            fillCompositePropertyMeta((CompositeProperty)property, fieldMap, showMode, json, newPath);
            return;
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            fillArrayPropertyMeta((ArrayProperty)property, fieldMap, showMode, json, newPath);
            return;
        }

        if( property instanceof SimpleProperty) {
            fillSimplePropertyMeta((SimpleProperty) property, fieldMap, showMode, json, newPath);
        }

//        JsonObjectBuilder json = Json.createObjectBuilder();

//        json.add(type.name(), getTypeName(property.getValueClass()));
//
//        if(!property.getName().equals(property.getDisplayName()))
//        {
//            json.add(displayName.name(), property.getDisplayName());
//        }
//
//        String shortDescription = property.getShortDescription().split("\n")[0];
//        if(!property.getName().equals(shortDescription))
//        {
//            json.add(description.name(), property.getShortDescription().split("\n")[0]);
//        }
//
//        if(property.isReadOnly())
//        {
//            json.add(readOnly.name(), true);
//        }
//
//        return json.build();
    }

        /**
     * Convert model to JSON
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static void getModelAsJSON(CompositeProperty properties, FieldMap fieldMap, int showMode,
                                           JsonObjectBuilder json, JsonPath path)
            throws Exception
    {
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            try
            {
                JsonPath newPath = path.append(property.getName());
                //if(!property.getName().equals("class"))

                convertSinglePropertyMeta( property, fieldMap.get(property), showMode, json, newPath );
//                if(object != null)
//                    json.add(newPath.get(), object);
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Unable to convert property: #" + i + ": "
                        + ( property == null ? null : property.getName() ), e );
            }
        }
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

//    /**
//     * Convert model to JSON
//     * @param properties model to convert
//     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
//     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
//     */
//    public static JSONArray getModelAsJSON(CompositeProperty properties, FieldMap fieldMap, int showMode)
//            throws Exception
//    {
//        JSONArray result = new JSONArray();
//        for( int i = 0; i < properties.getPropertyCount(); i++ )
//        {
//            Property property = properties.getPropertyAt(i);
//            try
//            {
//                JSONObject object = convertSingleProperty( fieldMap, showMode, property );
//                if(object != null)
//                    result.put(object);
//            }
//            catch( Exception e )
//            {
//                throw new BiosoftInternalException( e, "Unable to convert property: #" + i + ": "
//                        + ( property == null ? null : property.getName() ) );
//            }
//        }
//        return result;
//    }
//
//    private static JSONObject convertSingleProperty(FieldMap fieldMap, int showMode, Property property) throws Exception
//    {
//        String name = property.getName();
//        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
//            return null;
//        JSONObject p = new JSONObject();
//        p.put(NAME_ATTR, name);
//        p.put(DISPLAYNAME_ATTR, property.getDisplayName());
//        p.put(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
//        p.put(READONLY_ATTR, property.isReadOnly());
//        if( property instanceof CompositeProperty && (!property.isHideChildren() || property.getPropertyEditorClass() == PenEditor.class) )
//        {
//            return fillCompositeProperty( fieldMap, showMode, property, p );
//        }
//        if( property instanceof ArrayProperty && !property.isHideChildren() )
//        {
//            return fillArrayPropertyMeta( fieldMap, showMode, property, p );
//        }
//        return fillSimpleProperty( property, p );
//    }
//
//    private static JSONObject fillSimpleProperty(Property property, JSONObject p) throws InstantiationException, IllegalAccessException,
//            JSONException
//    {
//        Class<?> editorClass = property.getPropertyEditorClass();
//        if( editorClass != null )
//        {
//            if( JSONSerializable.class.isAssignableFrom(editorClass) )
//            {
//                JSONSerializable editor = (JSONSerializable)editorClass.newInstance();
//                if( editor instanceof PropertyEditorEx )
//                {
//                    initEditor( property, (PropertyEditorEx)editor );
//                    JSONObject p1 = editor.toJSON();
//                    if( p1 != null )
//                    {
//                        Iterator<?> iterator = p1.keys();
//                        while( iterator.hasNext() )
//                        {
//                            String key = iterator.next().toString();
//                            if( key.equals("dictionary") )
//                            {
//                                JSONArray array = p1.optJSONArray("dictionary");
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
//                }
//            }
//            else if( TagEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                TagEditorSupport editor = (TagEditorSupport)editorClass.newInstance();
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    p.put(DICTIONARY_ATTR, createDictionary(tags, true));
//                }
//            }
//            else if( StringTagEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                StringTagEditorSupport editor = (StringTagEditorSupport)editorClass.newInstance();
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    p.put(DICTIONARY_ATTR, createDictionary(tags, false));
//                }
//            }
//            else if( CustomEditorSupport.class.isAssignableFrom(editorClass) )
//            {
//                CustomEditorSupport editor = null;
//                //TODO: support or correctly process some editors
//                //Some editors like biouml.model.util.ReactionEditor, biouml.model.util.FormulaEditor
//                //use Application.getApplicationFrame(), so we got a NullPointerException here
//                try
//                {
//                    editor = (CustomEditorSupport)editorClass.newInstance();
//                    initEditor( property, editor );
//                    String[] tags = editor.getTags();
//                    if( tags != null )
//                    {
//                        p.put(DICTIONARY_ATTR, createDictionary(tags, false));
//                    }
//                }
//                catch( Exception e )
//                {
//                }
//            }
//        }
//
//        Object value = property.getValue();
//        if( value != null )
//        {
//            p.put(TYPE_ATTR, (value instanceof Boolean) ? "bool" : "code-string");
//            p.put(VALUE_ATTR, value.toString());
//        }
//        return p;
//    }
//
//    private static JSONObject fillArrayPropertyMeta(FieldMap fieldMap, int showMode, Property property, JSONObject p)
//            throws Exception
//    {
//        Class<?> c = property.getPropertyEditorClass();
//        if( c != null )
//        {
//            if( CustomEditorSupport.class.isAssignableFrom(c) )
//            {
//                CustomEditorSupport editor = (CustomEditorSupport)c.newInstance();
//                initEditor( property, editor );
//                String[] tags = editor.getTags();
//                if( tags != null )
//                {
//                    p.put(DICTIONARY_ATTR, createDictionary(tags, false));
//                    p.put(TYPE_ATTR, "multi-select");
//                    Object[] vals = (Object[])property.getValue();
//                    JSONArray value = new JSONArray();
//                    if( vals != null )
//                    {
//                        for( Object val : vals )
//                        {
//                            value.put(val.toString());
//                        }
//                    }
//                    p.put(VALUE_ATTR, value);
//                    return p;
//                }
//                if( editor instanceof JSONCompatibleEditor )
//                {
//                    ( (JSONCompatibleEditor)editor ).addAsJSON(property, p, fieldMap, showMode);
//                    return p;
//                }
//            }
//        }
//        JSONArray value = new JSONArray();
//        ArrayProperty array = (ArrayProperty)property;
//        for( int j = 0; j < array.getPropertyCount(); j++ )
//        {
//            Property element = array.getPropertyAt(j);
//            if( element instanceof CompositeProperty )
//            {
//                value.put(fillCompositePropertyMeta((CompositeProperty)element, fieldMap.get(property), showMode));
//            }
//            else
//            {
//                JSONObject pCh = new JSONObject();
//                Object val = element.getValue();
//                if( val != null )
//                {
//                    pCh.put(TYPE_ATTR, (val instanceof Boolean) ? "bool" : "code-string");
//                    pCh.put(NAME_ATTR, element.getName());
//                    pCh.put(DISPLAYNAME_ATTR, element.getName());
//                    pCh.put(VALUE_ATTR, val.toString());
//                    pCh.put(READONLY_ATTR, element.isReadOnly());
//                    value.put(new JSONArray().put(pCh));
//                }
//            }
//        }
//        p.put(TYPE_ATTR, "collection");
//        p.put(VALUE_ATTR, value);
//        return p;
//    }
//
//    private static JSONObject fillCompositeProperty(FieldMap fieldMap, int showMode, Property property, JSONObject p)
//            throws Exception
//    {
//        Object value = property.getValue();
//        Class<?> valueClass = property.getValueClass();
//        if( GenericComboBoxItem.class.isAssignableFrom( valueClass ) )
//        {
//            p.put(TYPE_ATTR, "code-string");
//            p.put(VALUE_ATTR, ( (GenericComboBoxItem)value ).getValue());
//            ( (GenericComboBoxItem)value ).updateAvailableValues();
//            p.put(DICTIONARY_ATTR, createDictionary( ( (GenericComboBoxItem)value ).getAvailableValues(), false));
//        }
//        else if( GenericMultiSelectItem.class.isAssignableFrom( valueClass ) )
//        {
//            p.put(TYPE_ATTR, "multi-select");
//            p.put(VALUE_ATTR, ( (GenericMultiSelectItem)value ).getValues());
//            ( (GenericMultiSelectItem)value ).updateAvailableValues();
//            p.put(DICTIONARY_ATTR, createDictionary( ( (GenericMultiSelectItem)value ).getAvailableValues(), false));
//        }
//        else if( value != null && customBeans.containsKey( valueClass ) )
//        {
//            Object wrapper = customBeans.get( valueClass ).getConstructor( valueClass ).newInstance( value );
//            p.put(TYPE_ATTR, "composite");
//            p.put(VALUE_ATTR, fillCompositePropertyMeta(ComponentFactory.getModel( wrapper ), fieldMap.get(property), showMode));
//        }
//        else if( Color.class.isAssignableFrom( valueClass ) )
//        {
//            p.put(TYPE_ATTR, "color-selector");
//            JSONArray valueEl = new JSONArray();
//            valueEl.put(encodeColor((Color)value));
//            p.put(VALUE_ATTR, valueEl);
//        }
//        else
//        {
//            p.put(TYPE_ATTR, "composite");
//            p.put(VALUE_ATTR, fillCompositePropertyMeta((CompositeProperty)property, fieldMap.get(property), showMode));
//        }
//        return p;
//    }

    protected static JsonArrayBuilder createDictionary(Object[] strings, boolean byPosition)
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

    public static final String NAME_ATTR = "name";
    public static final String DISPLAYNAME_ATTR = "displayName";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String TYPE_ATTR = "type";
    public static final String READONLY_ATTR = "readOnly";
    public static final String VALUE_ATTR = "value";
    public static final String CHILDREN_ATTR = "children";
    public static final String DICTIONARY_ATTR = "dictionary";
}
