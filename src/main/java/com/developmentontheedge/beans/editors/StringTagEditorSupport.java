package com.developmentontheedge.beans.editors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;
import java.util.ResourceBundle;

import com.developmentontheedge.beans.log.Logger;

/**
 * Helper class to present property value defined by some enumeration.
 * For this purpose you only need set up array of strings, that represents possible int
 * values and start value.
 * It is strongly suggested that possible values are begining from startValue
 * and there are not gaps.
 * @author Fedor A. Kolpakov
 */
public class StringTagEditorSupport extends PropertyEditorSupport
{
    /** List of possible values. */
    protected String[] values;

    /** The subclasses should initialise values atributes. */
    protected StringTagEditorSupport() {}

    /** Creates TagEditorSupport with the specified possible values. */
    public StringTagEditorSupport(String[] values)
    {
        this.values = values;
    }

    /**
     * Creates StringTagEditorSupport and initialise its values from the MessageBundle. 
     * The MessageBundle should returns for the specified key array of strings that represents 
     * possible values.
     * @pending Note. For correct loading of message bundle for components it is 
     * necessary explicitly define ClassLoader,
     * because the components jar files may be not included in classpath.
     * This is why this method is declared as protected.
     */
    protected StringTagEditorSupport( String messageBundle, String key)
    {
        this( messageBundle, StringTagEditorSupport.class, key);
    }

    /**
     * Creates StringTagEditorSupport and initialise its values from the MessageBundle 
     * (Class c is used to define correct class loader for a MessageBundle). 
     * The MessageBundle should returns for the specified key array of strings
     * that represents possible values.
     * @param messageBundle Name of message bundle.
     * @param c Class which used for extract class loader for MessageBundle.
     * @param key Key to array of strings in message bundle.
     */
    public StringTagEditorSupport( String messageBundle, Class c, String key)
    {
        try
        {
            ResourceBundle resources = ResourceBundle.getBundle( messageBundle, Locale.getDefault(),
                                                                 c.getClassLoader() );
            values = resources.getStringArray( key );
        }
        catch ( Exception e )
        {
            Logger.getLogger().error( getClass().getSimpleName(), e );
        }
    }

    @Override
    public String getAsText()
    {
        return (String)getValue();
    }

    @Override
    public void setAsText(String text)
    {
        setValue(text);
    }

    @Override
    public String[] getTags()
    {
        return values;
    }
}

