package com.developmentontheedge.beans.web;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.HyperlinkEvent;

import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

/**
 * @pending showExpertAction
 */
public class HtmlPropertyInspector extends HtmlPane
{
    private static final long serialVersionUID = 1L;

    ////////////////////////////////////////////////////////////////////////////
    // Static methods to add HtmlPropertyInspector specific metadata to
    // PropertyDescriptor
    //


    /** 
     * Indicates that composite property should be substituted by its child 
     * with the specified name. 
     */
    public static final String SUBSTITUTE_BY_CHILD = "html-substitute-by-child";

    /** 
     * Indicates that value of composite property should be substituted by 
     * value of its child with the specified name.
     * So we will get a single property with name of composite property
     * and value of its child property  
     */
    public static final String SUBSTITUTE_BY_CHILD_VALUE = "html-substitute-by-child-value";


    /** Property display name specific for HtmlPropertyInspector. */
    public static final String DISPLAY_NAME = "html-display-name";

    /** @pending should we concern about other ways of display name specifications? */
    public static String getDisplayName(FeatureDescriptor pd)
    {
        return (String)pd.getValue(DISPLAY_NAME);
    }

    public static void setDisplayName(FeatureDescriptor pd, String displayName)
    {
        pd.setValue(DISPLAY_NAME, displayName);
    }


    /** Bena method that generates HTML for the given bean. */
    public static final String HTML_GENERATOR_METHOD = "html-genertor-method";
    public static Method getHtmlGeneratorMethod(FeatureDescriptor pd)
    {
        return (Method)pd.getValue(HTML_GENERATOR_METHOD);
    }
    public static void setHtmlGeneratorMethod(FeatureDescriptor pd, Method method)
    {
        pd.setValue(HTML_GENERATOR_METHOD, method);
    }

    /** Property editor class specific for HtmlPropertyInspector. */
    public static final String PROPERTY_EDITOR_CLASS = "html-property-editor";

    /** @pending ?should we concern about other ways of display name specifications? */
    public static Class<?> getPropertyEditorClass(FeatureDescriptor pd)
    {
        return (Class<?>)pd.getValue(PROPERTY_EDITOR_CLASS);
    }

    public static void setPropertyEditorClass(FeatureDescriptor pd, Class propertyEditorClass)
    {
        pd.setValue(PROPERTY_EDITOR_CLASS, propertyEditorClass);
    }

    ////////////////////////////////////////////////////////////////////////////

    protected JToolBar toolbar;
    protected HtmlBeanGenerator htmlGenerator;
    protected Object bean;

    public HtmlPropertyInspector()
    {
        toolbar = new JToolBar();
        toolbar.setVisible(false);
        toolbar.setFloatable(false);
        fillToolbar();
        add(BorderLayout.NORTH, toolbar);

        htmlGenerator = new HtmlBeanGenerator();
    }

    private ComponentModel model;
    public void explore(Object bean)
    {
        this.bean = bean;
        String result = "";

        if (bean != null)
        {
            try
            {
                model = ComponentFactory.getModel(bean, ComponentFactory.Policy.DEFAULT, true);
                Method method = getHtmlGeneratorMethod(model.getDescriptor());
                if( method != null )    
                    result = (String)method.invoke(bean, ( Object[] )null); 
                else
                    result = htmlGenerator.generate(model);
            }
            catch (Throwable t)
            {
                Logger.getLogger().error("Bean explore error, bean=" + bean + ". ", t);
            }
        }
        setInitialText(result);
    }

    public void setInitialText(String text)
    {
        super.setInitialText(text);
        toolbar.setVisible(text != null && !("".equals(text.trim())));
    }

    ////////////////////////////////////////////////////////////////////////////
    // HyperlinkListener interface implementations
    //

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        String link = e.getDescription();

        if (link.startsWith("property_description"))
        {
            String text = getPropertyDesription(link);

            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
            {
                editor.setToolTipText("<html><body>" + text + "</body></html>");
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                followHyperlink(text);
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.EXITED)
            {
                editor.setToolTipText(null);
            }

            return;
        }

        super.hyperlinkUpdate(e);
    }

    protected String getPropertyDesription(String link)
    {
        int pos = link.indexOf("/");
        String name = link.substring(pos + 1);
        Property property = model.findProperty(name);

        if (property != null)
            return property.getDescriptor().getShortDescription();

        return "Error: property description for property " + name + " not found.";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility methods for ExplorerPaneCard
    //

    public JComponent getView()
    {
        return scrollPane;
    }

    @Override
    public Action[] getActions()
    {
        return actions;
    }

    public Object getExploredBean()
    {
        return bean;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties
    //

    /**
     * Returns current show mode.
     * @see Property#SHOW_USUAL
     * @see Property#SHOW_EXPERT
     * @see Property#SHOW_HIDDEN
     * @see Property#SHOW_PREFERRED
     */
    public int getPropertyShowMode()
    {
        return htmlGenerator.getPropertyShowMode();
    }

    /**
     * Sets show mode.
     * @param propertyShowMode the new show mode
     * @see Property#SHOW_USUAL
     * @see Property#SHOW_EXPERT
     * @see Property#SHOW_HIDDEN
     * @see Property#SHOW_PREFERRED
     */
    public void setPropertyShowMode(int propertyShowMode)
    {
        htmlGenerator.setPropertyShowMode(propertyShowMode);
        explore(bean);
    }

    public boolean isShowEmptyValues()
    {
        return htmlGenerator.isShowEmptyValues();
    }

    public void setShowEmptyValues(boolean showEmptyValues)
    {
        htmlGenerator.setShowEmptyValues(showEmptyValues);
    }

    public HtmlFormatter getHtmlFormatter()
    {
        return htmlGenerator.getHtmlFormatter();
    }

    public void setHtmlFormatter(HtmlFormatter formatter)
    {
        htmlGenerator.setHtmlFormatter(formatter);
        explore(bean);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Action issues
    //

    private void fillToolbar()
    {
        for (int i = 0; i < actions.length; i++)
        {
            Action action = actions[i];
            AbstractButton button;
            if (action == showExpertAction)
                button = new JToggleButton(action);
            else
                button = new JButton(action);

            button.setAlignmentY(0.5f);
            Dimension btnSize = new Dimension(25, 25);
            button.setSize(btnSize);
            button.setPreferredSize(btnSize);
            button.setMinimumSize(btnSize);
            button.setMaximumSize(btnSize);

            if (button.getIcon() != null)
                button.setText(null);

            toolbar.add(button);
        }
    }

    public static final String SHOW_EXPERT_ACTION = "SHOW_EXPERT_ACTION";
    protected Action showExpertAction;

    @Override
    protected void initActions()
    {
        super.initActions();

        showExpertAction = new ShowExpertAction(SHOW_EXPERT_ACTION);
        showExpertAction.setEnabled(false);
        ActionInitializer.initAction(showExpertAction, SHOW_EXPERT_ACTION);

        actions = new Action[] { backwardAction, forwardAction, /*showExpertAction*/ };
    }

    class ShowExpertAction extends AbstractAction
    {
        public ShowExpertAction(String name)
        {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int mode = getPropertyShowMode();
            if (mode == Property.SHOW_USUAL)
            {
                setPropertyShowMode(Property.SHOW_USUAL);
            }
            else
            {
                setPropertyShowMode(Property.SHOW_EXPERT);
            }

        }
    }
}
