package com.developmentontheedge.beans.undo;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static final Object[][] contents =
    {
        {"PN_TRANSACTION_NAME", "Transaction name"},
        {"PD_TRANSACTION_NAME", "Transaction name"},
        {"PN_TRANSACTION_COMMENT", "Comment"},
        {"PD_TRANSACTION_COMMENT", "Comment"},
    };

    /**
     * Returns string from the resource bundle for the specified key.
     * If the sting is absent the key string is returned instead and
     * the message is printed in <code>log4j.Category</code> for the component.
     */
    public String getResourceString(String key)
    {
        try
        {
            return getString(key);
        } catch (Throwable t)
        {
        }
        return key;
    }
}
