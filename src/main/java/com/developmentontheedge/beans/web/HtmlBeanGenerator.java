package com.developmentontheedge.beans.web;

import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;
import java.io.StringWriter;
import java.io.Writer;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

public class HtmlBeanGenerator
{
    ////////////////////////////////////////////////////////////////////////////
    // properties
    //

    HtmlFormatter formatter = new SimpleHtmlFormatter();
    public HtmlFormatter getHtmlFormatter()
    {
        return formatter;
    }
    public void setHtmlFormatter(HtmlFormatter formatter)
    {
        this.formatter = formatter;
    }

    /** Indicates what properties (usual, expert, hidden,preferred) should be displayed. */
    private int propertyShowMode = Property.SHOW_USUAL | Property.SHOW_PREFERRED;

    /**
     * Returns current show mode.
     * @see Property#SHOW_USUAL
     * @see Property#SHOW_EXPERT
     * @see Property#SHOW_HIDDEN
     * @see Property#SHOW_PREFERRED
     */
    public int getPropertyShowMode()
    {
        return propertyShowMode;
    }

    /**
     * Sets show mode.
     * @param propertyShowMode the new show mode
     * @see Property#SHOW_USUAL
     * @see Property#SHOW_EXPERT
     * @see Property#SHOW_HIDDEN
     * @see Property#SHOW_PREFERRED
     */
    public void setPropertyShowMode( int propertyShowMode )
    {
        this.propertyShowMode = propertyShowMode;
    }

    /** Indicates whether properties with emty (null) values should be shown. */
    boolean showEmptyValues = false;
    public boolean isShowEmptyValues()
    {
        return showEmptyValues;
    }

    public void setShowEmptyValues(boolean showEmptyValues)
    {
        this.showEmptyValues = showEmptyValues;
    }


    ////////////////////////////////////////////////////////////////////////////
    // generate functions
    //

    public String generate(Object bean) throws Exception
    {
        StringWriter writer = new StringWriter();
        generate(bean, writer);
        return writer.toString();
    }

    /**
     * @pending why get ComponentModel ignore cash
     */
    public void generate(Object bean,  Writer out) throws Exception
    {
        if( bean == null )
            return;

        ComponentModel model = ComponentFactory.getModel(bean, true);
        generate(model, out);
    }

    String generate(ComponentModel model) throws Exception
    {
        StringWriter writer = new StringWriter();
        generate(model, writer);
        return writer.toString();
    }

    void generate(ComponentModel model,  Writer out) throws Exception
    {
        formatter.generateHeader(out, model.getDisplayName());

        int limit = model.getVisibleCount( propertyShowMode );
        for( int i = 0; i < limit; i++ )
        {
            Property property = model.getVisiblePropertyAt(i, propertyShowMode);
            if( isJavaClassProperty( property ) )
                continue;

            generateProperty(out, property, 0);
        }

        formatter.generateFooter(out);
    }


    /**
     * @pending should we invoke PropertyEditor for null values?
     */
    void generateProperty(Writer out, Property prop, int level) throws Exception
    {
        FeatureDescriptor pd = prop.getDescriptor();
        String stringValue = "&nbsp;";

        String  displayName = HtmlPropertyInspector.getDisplayName(pd);
        if(displayName == null)
            displayName = prop.getDisplayName();

        boolean readOnly    = prop.isReadOnly();
        boolean canBeNull   = prop.getBooleanAttribute( BeanInfoConstants.CAN_BE_NULL );

        String child = (String)pd.getValue(HtmlPropertyInspector.SUBSTITUTE_BY_CHILD);
        if( child != null )
        {
            Property childProp = prop.findProperty(child);
            if( childProp != null )
            {
                generateProperty(out, childProp, level);
                return;
            } 
            else
                Logger.getLogger().error("Can not find property '" + child + 
                                  "' to substitute parent '" + prop.getName() );
        }

        child = (String)pd.getValue(HtmlPropertyInspector.SUBSTITUTE_BY_CHILD_VALUE);
        if( child != null )
        {
            Property childProp = prop.findProperty(child);
            if( childProp != null )
            { 
                prop = childProp;
            }
            else
            {
                Logger.getLogger().error("Can not find property '" + child + 
                                  "' to substitute parent '" + prop.getName() );
            } 
        }
        
        Object propertyValue = prop.getValue();
        if(propertyValue == null && !showEmptyValues)
            return;


        // here we invoke propertyEditor to get String Value
        if( propertyValue != null)
        {
            PropertyEditor editor = getPropertyEditor(prop);
            if(editor != null)
                stringValue = editor.getAsText();
            else if( prop.isLeaf() )
                stringValue = propertyValue.toString();
        }


        formatter.generateProperty(out, level, displayName, prop.getCompleteName(), stringValue, readOnly, canBeNull);

        // generate child properties:
        int count = prop.getVisibleCount(propertyShowMode);
        if(count > 0)
        {
            level++;
            formatter.generateLevelStart(out, level);

            for( int i = 0; i < count; i++ )
            {
                Property property = prop.getVisiblePropertyAt( i, propertyShowMode );
                if( property.getName().equals( "class" ) )
                        continue;
                generateProperty(out, property, level);
            }

            formatter.generateLevelEnd(out, level);
            level--;
        }
    }

    PropertyEditor getPropertyEditor(Property property)
    {
        FeatureDescriptor fd = property.getDescriptor();

        // here we invoke propertyEditor to get String Value
        Class<?> editorClass = HtmlPropertyInspector.getPropertyEditorClass(fd);
        if( editorClass == null )
            editorClass = property.getPropertyEditorClass();

        if(editorClass != null)
        {
            try
            {
                PropertyEditor editor = (PropertyEditor)editorClass.newInstance();
                Object value = property.getValue();
                editor.setValue( value );

                if( editor instanceof PropertyEditorEx )
                {
                    Object owner = property.getOwner();
                    if( owner instanceof Property.PropWrapper )
                        owner = ( ( Property.PropWrapper )owner ).getOwner();
                    (( PropertyEditorEx )editor).setBean( owner );
                    (( PropertyEditorEx )editor).setDescriptor( property.getDescriptor() );
                }

                return editor;
            }
            catch(Exception ex)
            {
                Logger.getLogger().error("Error when getting editor", ex);
            }
        }

        return null;
    }

    public static boolean isJavaClassProperty( Property property )
    {
        if( !property.getName().equals( "class" ) )
            return false;

        if( property.getOwner() instanceof Property.PropWrapper &&
            ( ( Property.PropWrapper )property.getOwner() ).getOwner() != null )
            return false;

        return true;
    }
}
