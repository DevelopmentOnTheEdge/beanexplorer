package com.developmentontheedge.beans.editors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;
import java.util.ResourceBundle;

import com.developmentontheedge.beans.log.Logger;

/**
 * Helper class to present property value defined by some enumeration.
 * For this purpose you only need set up array of strings, that represents possible int values and start value.
 * It is strongly suggested that possible values are begining from startValue and there are not gaps.
 */
public class TagEditorSupport extends PropertyEditorSupport
{
    /** List of possible values. */
    protected String[] values;

    /** The first possible value. */
    protected int startValue;

    /** The subclasses should initialise startValue and values atributes. */
    protected TagEditorSupport()
    {
    }

    /**
     * Creates TagEditorSupport with the specified possible values and start value.
     * @param values Array of strings.
     * @param startValue Number of first string in array.
     */
    public TagEditorSupport( String[] values, int startValue )
    {
        this.values     = values;
        this.startValue = startValue;
    }

    /**
     * Creates TagEditorSupport and initialise its values from the MessageBundle. The MessageBundle should
     * returns for the specified key array of strings that represents possible values.
     * @pending Note. For correct loading of message bundle for components it is necessary explicitly define ClassLoader,
     * because the components jar files may be not included in classpath.
     * This is why this method is declared as protected.
     */
    protected TagEditorSupport( String messageBundle, String key, int startValue )
    {
        this( messageBundle, TagEditorSupport.class, key, startValue );
    }

    /**
     * Creates TagEditorSupport and initialise its values from the MessageBundle (Class c is used to define correct
     * class loader for a MessageBundle). The MessageBundle should returns for the specified key array of strings
     * that represents possible values.
     * @param messageBundle Name of message bundle.
     * @param c Class which used for extract class loader for MessageBundle.
     * @param key Key to array of strings in message bundle.
     * @param startValue Number of first string in array.
     * @pending Note. For correct loading of message bundle for
     * components it is necessary explicitly define ClassLoader,
     * because the components jar files are not included in classpath.
     * This is why this method is declared as protected.
     */
    public TagEditorSupport( String messageBundle, Class c, String key, int startValue )
    {
        try
        {
            this.startValue = startValue;
            ResourceBundle resources = ResourceBundle.getBundle( messageBundle, Locale.getDefault(),
                                                                 c.getClassLoader() );
            values = resources.getStringArray( key );
        }
        catch ( Exception e )
        {
            Logger.getLogger().error( getClass().getName(), e );
        }
    }

    /**
     * Gets the Integer (enum) property value as text. Override this method for String format value representation
     *
     * @return  The property value in human reading string.
     */
    @Override
    public String getAsText()
    {
        Integer integer = ( Integer )getValue();
        if ( integer == null )
            return "0";
        int index = integer.intValue() - startValue;
        return values[ index ];
    }


    /**
     * Sets the Integer (enum) property value by parsing a given String.
     * Override this method for converting from String value representation to value object.
     *
     * @param text the property value as a human editable string.
     * @throws java.lang.IllegalArgumentException if
     * either the String is badly formatted or
     * if this kind of property can't be expressed as text.
     */
    @Override
    public void setAsText( String text )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            if ( values[ i ].equals( text ) )
            {
                setValue( i + startValue );
                return;
            }
        }
    }

    /**
     * Returns an array of String representation of Integer values (enum values).
     * This method is used for filling values of combo box editor.
     * If the property value must be one of a set of known tagged values,
     * then this method should return an array of the tags.
     *
     * @return The tag values for this property.  May be null if this
     *   property cannot be represented as a tagged value.
     *
     */
    @Override
    public String[] getTags()
    {
        return values;
    }
}
//PropertyDescriptor
