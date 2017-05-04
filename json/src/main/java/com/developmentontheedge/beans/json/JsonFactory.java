package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.CompositeProperty;

import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Provides API to serialize beans and dynamic property sets to JSON. 
 */
public class JsonFactory
{
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
    
    protected static addBeanValues(CompositeProperty properties, int showMode)
    {}
    
    /**
     * Convert model to JSON
     * @param properties model to convert
     * @param fieldMap fieldMap of properties to include. Cannot be null. Use {@link FieldMap#ALL} to include all fields
     * @param showMode mode like {@link Property#SHOW_USUAL} which may filter some fields additionally
     */
    public static JSONArray getModelAsJSON(CompositeProperty properties, FieldMap fieldMap, int showMode)
            throws Exception
    {
        JSONArray result = new JSONArray();
        for( int i = 0; i < properties.getPropertyCount(); i++ )
        {
            Property property = properties.getPropertyAt(i);
            try
            {
                JSONObject object = convertSingleProperty( fieldMap, showMode, property );
                if(object != null)
                    result.put(object);
            }
            catch( Exception e )
            {
                throw new BiosoftInternalException( e, "Unable to convert property: #" + i + ": "
                        + ( property == null ? null : property.getName() ) );
            }
        }
        return result;
    }

    private static JSONObject convertSingleProperty(FieldMap fieldMap, int showMode, Property property) throws Exception
    {
        String name = property.getName();
        if( !property.isVisible(showMode) || !fieldMap.contains(name) )
            return null;
        JSONObject p = new JSONObject();
        p.put(NAME_ATTR, name);
        p.put(DISPLAYNAME_ATTR, property.getDisplayName());
        p.put(DESCRIPTION_ATTR, property.getShortDescription().split("\n")[0]);
        p.put(READONLY_ATTR, property.isReadOnly());
        if( property instanceof CompositeProperty && (!property.isHideChildren() || property.getPropertyEditorClass() == PenEditor.class) )
        {
            return fillCompositeProperty( fieldMap, showMode, property, p );
        }
        if( property instanceof ArrayProperty && !property.isHideChildren() )
        {
            return fillArrayProperty( fieldMap, showMode, property, p );
        }
        return fillSimpleProperty( property, p );
    }

    private static JSONObject fillSimpleProperty(Property property, JSONObject p) throws InstantiationException, IllegalAccessException,
            JSONException
    {
        Class<?> editorClass = property.getPropertyEditorClass();
        if( editorClass != null )
        {
            if( JSONSerializable.class.isAssignableFrom(editorClass) )
            {
                JSONSerializable editor = (JSONSerializable)editorClass.newInstance();
                if( editor instanceof PropertyEditorEx )
                {
                    initEditor( property, (PropertyEditorEx)editor );
                    JSONObject p1 = editor.toJSON();
                    if( p1 != null )
                    {
                        Iterator<?> iterator = p1.keys();
                        while( iterator.hasNext() )
                        {
                            String key = iterator.next().toString();
                            if( key.equals("dictionary") )
                            {
                                JSONArray array = p1.optJSONArray("dictionary");
                                if( array != null )
                                {
                                    String[] elements = new String[array.length()];
                                    for( int index = 0; index < array.length(); index++ )
                                        elements[index] = array.optString(index);
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

    private static JSONObject fillArrayProperty(FieldMap fieldMap, int showMode, Property property, JSONObject p)
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
                    JSONArray value = new JSONArray();
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
        JSONArray value = new JSONArray();
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
                JSONObject pCh = new JSONObject();
                Object val = element.getValue();
                if( val != null )
                {
                    pCh.put(TYPE_ATTR, (val instanceof Boolean) ? "bool" : "code-string");
                    pCh.put(NAME_ATTR, element.getName());
                    pCh.put(DISPLAYNAME_ATTR, element.getName());
                    pCh.put(VALUE_ATTR, val.toString());
                    pCh.put(READONLY_ATTR, element.isReadOnly());
                    value.put(new JSONArray().put(pCh));
                }
            }
        }
        p.put(TYPE_ATTR, "collection");
        p.put(VALUE_ATTR, value);
        return p;
    }

    private static JSONObject fillCompositeProperty(FieldMap fieldMap, int showMode, Property property, JSONObject p)
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
            JSONArray valueEl = new JSONArray();
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


}
