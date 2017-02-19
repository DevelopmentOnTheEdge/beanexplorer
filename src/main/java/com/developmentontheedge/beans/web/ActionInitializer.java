package com.developmentontheedge.beans.web;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.KeyStroke;

import com.developmentontheedge.beans.log.Logger;

/**
 * This class contains common rutines to init actions
 * by the data from the corresponding MessageBundle.
 *
 */
public class ActionInitializer
{
    protected static ResourceBundle resources;
    protected static Class<?> loader;


    /**
     * Inits ActionInitializer with the specified
     * ResourceBundle name and class, used for resource loading.
     */
    public static void init(String resourceBundleName, Class<?> l)
    {
        initResourceBundle(resourceBundleName, l);
    }


    /**
     * Inits ActionInitializer with the specified
     * ResourceBundle and class, used for resource loading.
     */
    public static void init(ResourceBundle r, Class<?> l)
    {
        resources = r;
        loader    = l;
    }

    /**
     * Init action by the data from the MessageBundle.
     */
    public static void initAction(Action action, String key)
    {
        initActionValue(action, Action.NAME,               key + Action.NAME);
        initActionValue(action, Action.SHORT_DESCRIPTION,  key + Action.SHORT_DESCRIPTION);
        initActionValue(action, Action.LONG_DESCRIPTION,   key + Action.LONG_DESCRIPTION);
        initActionValue(action, Action.ACTION_COMMAND_KEY, key + Action.ACTION_COMMAND_KEY);

        String value = getResourceString(key + Action.MNEMONIC_KEY);
        if (value != null)
            action.putValue(Action.MNEMONIC_KEY, new Integer(value.charAt(0)));

        URL url = getResource(key + Action.SMALL_ICON);
        if (url != null)
        {
            action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(url));
        }

        Object obj = getResourceObject(key + Action.ACCELERATOR_KEY);
        if (obj instanceof KeyStroke)
        {
            action.putValue(Action.ACCELERATOR_KEY, obj);
        }

    }

    /**
     * Init action value with the specified actionKey
     * by the string data from the message bundle with the corresponding key.
     */
    public static void initActionValue(Action action, String actionKey, String resourceKey)
    {
        String value = getResourceString(resourceKey);

        if (value != null)
            action.putValue(actionKey, value);
    }

    /**
     * Creates resourceBundle for the specified name and default locale.
     */
    public static void initResourceBundle(String resourceBundlename, Class<?> l)
    {
        try
        {
            loader = l;
            resources = ResourceBundle.getBundle(resourceBundlename,
                                                 Locale.getDefault());
        } catch (MissingResourceException mre)
        {
            Logger.getLogger().error( ActionInitializer.class/*getClass()*/ + " properties not found. " + mre);
        }
    }

    /**
     * Returns object with the specified key from the resource bundle.
     * If the MissingResourceException occurs, catch it and returns null.
     */
    protected static Object getResourceObject(String nm)
    {
        Object obj = null;

        if (resources == null)
            Logger.getLogger().error( ActionInitializer.class/*getClass()*/ + " resource bundle not init.");
        else
        {
            try
            {
                obj = resources.getObject( nm );
            } catch (MissingResourceException mre)
            {
                Logger.getLogger().warn("Missing resource <" + nm + "> in " + ActionInitializer.class );
            }
        }

        return obj;
    }


    /**
     * Returns string with the specified key from the resource bundle.
     * If the MissingResourceException occurs, catch it and returns null.
     */
    protected static String getResourceString(String nm)
    {
        String str = null;

        if (resources == null)
            Logger.getLogger().error( ActionInitializer.class + " resource bundle not init.");
        else
        {
            try
            {
                str = resources.getString( nm );
            } catch (MissingResourceException mre)
            {
                Logger.getLogger().warn("Missing resource <" + nm + "> in " + ActionInitializer.class );
            }
        }

        return str;
    }
/*
    public static String getResourcePath(String name)
    {
        return "resources/" + getResourceString(name);
    }
*/
    /**
     * Get URL for the specified key using loader class.
     */
    public static URL getResource(String key)
    {
        //String name = getResourcePath(key);
        String name = getResourceString(key);
        if (name != null)
        {
            URL url = loader.getResource(name);
            return url;
        }

        return null;
    }

}
