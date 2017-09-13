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
import one.util.streamex.EntryStream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import java.awt.*;
import java.util.Map;


/**
 * copy ru.biosoft.server.JSONUtils serialization
 *
 * Provides API to serialize beans and dynamic property sets to Json.
 */
public class JsonFactoryCopy
{
    public static final String NAME_ATTR = "name";
    public static final String DISPLAYNAME_ATTR = "displayName";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String TYPE_ATTR = "type";
    public static final String READONLY_ATTR = "readOnly";
    public static final String VALUE_ATTR = "value";
    public static final String CHILDREN_ATTR = "children";
    public static final String DICTIONARY_ATTR = "dictionary";

//    private static final Map<Class<?>, Class<?>> customBeans = EntryStream.<Class<?>, Class<?>> of(
//            Point.class, PointWrapper.class,
//            ColorFont.class, ColorFontWrapper.class,
//            Dimension.class, DimensionWrapper.class ).toMap();

    private static final Map<Class<?>, Class<?>> customBeans = EntryStream.<Class<?>, Class<?>> of(
            Point.class,
            Dimension.class).toMap();

    private static void initEditor(Property property, PropertyEditorEx editor)
    {
        Object owner = property.getOwner();
        if( owner instanceof Property.PropWrapper )
            owner = ( (Property.PropWrapper)owner ).getOwner();
        editor.setValue(property.getValue());
        editor.setBean(owner);
        editor.setDescriptor(property.getDescriptor());
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

    /**
     * Counterpart for parseColor
     * @param color color to encode
     * @return
     */
    private static String encodeColor(Color color)
    {
        if(color == null || color.getAlpha() == 0) return "";
        return "["+color.getRed()+","+color.getGreen()+","+color.getBlue()+"]";
    }

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

    /**
     * Returns true if model contains at least one expert option on any level
     * @param model - model to check
     */
    public static boolean isExpertAvailable(CompositeProperty model)
    {
        for( int i = 0; i < model.getPropertyCount(); i++ )
        {
            Property property = model.getPropertyAt(i);
            if( property.isVisible(Property.SHOW_EXPERT) && !property.isVisible(Property.SHOW_USUAL) )
                return true;
            if( property instanceof CompositeProperty && property.isVisible(Property.SHOW_USUAL)
                    && isExpertAvailable((CompositeProperty)property) )
                return true;
        }
        return false;
    }

    /**
     * Returns additional bean attributes
     * @param model - model of bean to get attributes
     * @return JsonObjectBuilder containing attributes
     */
    public static JsonObjectBuilder getBeanAttributes(CompositeProperty model)
    {
        JsonObjectBuilder result = Json.createObjectBuilder();
        
        result.add("expertOptions", isExpertAvailable(model));
        
        return result;
    }

    /**
     * Convert model to JSON
     * @param properties model to convert
     */
    public static JsonArrayBuilder getModelAsJSON(CompositeProperty properties) throws Exception
    {
        return getModelAsJSON(properties, FieldMap.ALL, Property.SHOW_USUAL);
    }

    /**
     * Convert model to JSON
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static JsonArrayBuilder getModelAsJSON(CompositeProperty properties, FieldMap fieldMap, int showMode)
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
                        + ( property == null ? null : property.getName() ) ,e);
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
        p.add(DISPLAYNAME_ATTR, property.getDisplayName());
        p.add(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        p.add(READONLY_ATTR, property.isReadOnly());
        if( property instanceof CompositeProperty && (!property.isHideChildren() ) )//|| property.getPropertyEditorClass() == PenEditor.class
        {
            return fillCompositeProperty( fieldMap, showMode, property, p );
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            return fillArrayProperty( fieldMap, showMode, property, p );
        }
        return fillSimpleProperty( property, p );
    }

    private static JsonObjectBuilder fillSimpleProperty(Property property, JsonObjectBuilder p) throws IllegalAccessException, InstantiationException {
        Class<?> editorClass = property.getPropertyEditorClass();
        if( editorClass != null )
        {
            if( JsonSerializable.class.isAssignableFrom(editorClass) )
            {
                JsonSerializable editor = (JsonSerializable)editorClass.newInstance();
                if( editor instanceof PropertyEditorEx )
                {
                    initEditor( property, (PropertyEditorEx)editor );
//                    JsonObjectBuilder p1 = editor.toJSON();
//                    if( p1 != null )
//                    {
//                        Iterator<?> iterator = p1.keys();
//                        while( iterator.hasNext() )
//                        {
//                            String key = iterator.next().toString();
//                            if( key.equals("dictionary") )
//                            {
//                                JsonArrayBuilder array = p1.optJsonArrayBuilder("dictionary");
//                                if( array != null )
//                                {
//                                    String[] elements = new String[array.length()];
//                                    for( int index = 0; index < array.length(); index++ )
//                                        elements[index] = array.optString(index);
//                                    p.add(DICTIONARY_ATTR, createDictionary(elements, false));
//                                }
//                            }
//                            else
//                            {
//                                p.add(key, p1.get(key));
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
//                if( editor instanceof JSONCompatibleEditor )
//                {
//                    ( (JSONCompatibleEditor)editor ).addAsJSON(property, p, fieldMap, showMode);
//                    return p;
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
                value.add(getModelAsJSON((CompositeProperty)element, fieldMap.get(property), showMode));
            }
            else
            {
                JsonObjectBuilder pCh = Json.createObjectBuilder();
                Object val = element.getValue();
                if( val != null )
                {
                    pCh.add(TYPE_ATTR, (val instanceof Boolean) ? "bool" : "code-string");
                    pCh.add(NAME_ATTR, element.getName());
                    pCh.add(DISPLAYNAME_ATTR, element.getName());
                    pCh.add(VALUE_ATTR, val.toString());
                    pCh.add(READONLY_ATTR, element.isReadOnly());
                    value.add(Json.createArrayBuilder().add(pCh));
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
//        else
        if( value != null && customBeans.containsKey( valueClass ) )
        {
            Object wrapper = customBeans.get( valueClass ).getConstructor( valueClass ).newInstance( value );
            p.add(TYPE_ATTR, "composite");
            p.add(VALUE_ATTR, getModelAsJSON(ComponentFactory.getModel( wrapper ), fieldMap.get(property), showMode));
        }
        else if( Color.class.isAssignableFrom( valueClass ) )
        {
            p.add(TYPE_ATTR, "color-selector");
            JsonArrayBuilder valueEl = Json.createArrayBuilder();
            valueEl.add(encodeColor((Color)value));
            p.add(VALUE_ATTR, valueEl);
        }
        else
        {
            p.add(TYPE_ATTR, "composite");
            p.add(VALUE_ATTR, getModelAsJSON((CompositeProperty)property, fieldMap.get(property), showMode));
        }
        return p;
    }
}
