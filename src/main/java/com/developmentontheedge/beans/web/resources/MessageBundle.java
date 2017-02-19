/* $Id: MessageBundle.java,v 1.4 2002/11/10 06:12:12 zha Exp $ */
package com.developmentontheedge.beans.web.resources;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.util.ListResourceBundle;
import java.util.MissingResourceException;

import javax.swing.Action;
import javax.swing.KeyStroke;

import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.web.HtmlPropertyInspector;


public class MessageBundle extends ListResourceBundle
{

    @Override
    public Object[][] getContents()
    {
        return contents;
    }

    private static Object[][] contents =
    {
        // Backward Action
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.SMALL_ICON           , "prev.gif"},
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.NAME                 , "Back"},
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.SHORT_DESCRIPTION    , "Back"},
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.LONG_DESCRIPTION     , "Back"},
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.MNEMONIC_KEY         , "B"},
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.ACCELERATOR_KEY      , KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK | Event.ALT_MASK) },
        { HtmlPropertyInspector.BACKWARD_ACTION      + Action.ACTION_COMMAND_KEY   , "cmd-back"},

        // Forward Action
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.SMALL_ICON           , "next.gif"},
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.NAME                 , "Forward"},
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.SHORT_DESCRIPTION    , "Forward"},
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.LONG_DESCRIPTION     , "Forward"},
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.MNEMONIC_KEY         , "F"},
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.ACCELERATOR_KEY      , KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK | Event.ALT_MASK) },
        { HtmlPropertyInspector.FORWARD_ACTION      + Action.ACTION_COMMAND_KEY   , "cmd-forward"},

        // Show Expert Action
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.SMALL_ICON           , "about.gif"},
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.NAME                 , "Show expert"},
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.SHORT_DESCRIPTION    , "Show expert"},
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.LONG_DESCRIPTION     , "Show expert"},
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.MNEMONIC_KEY         , "S"},
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.ACCELERATOR_KEY      , KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.ALT_MASK) },
        { HtmlPropertyInspector.SHOW_EXPERT_ACTION      + Action.ACTION_COMMAND_KEY   , "cmd-show-expert"},
/*
        // Show Expert Action
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.SMALL_ICON           , "about.gif"},
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.NAME                 , "Open in browser"},
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.SHORT_DESCRIPTION    , "Open in browser"},
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.LONG_DESCRIPTION     , "Open in browser"},
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.MNEMONIC_KEY         , "O"},
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.ACCELERATOR_KEY      , KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK | Event.ALT_MASK) },
        { HtmlPropertyInspector.OPEN_IN_BROWSER_ACTION      + Action.ACTION_COMMAND_KEY   , "cmd-open-in-browser"},
*/
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
        }
        catch (MissingResourceException t)
        {
            Logger.getLogger().warn("Missing resource <" + key + "> in " + this.getClass() );
        }
        return key;
    }
}
