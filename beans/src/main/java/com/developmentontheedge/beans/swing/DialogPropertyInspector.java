package com.developmentontheedge.beans.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.developmentontheedge.beans.BeanEventListener;
import com.developmentontheedge.beans.EventConstants;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.model.SimpleProperty;
import com.developmentontheedge.beans.util.SmartText;

/**
 * This property inspector shows bean like dialog.
 * You can add this inspector into JDialog and use them.
 */
@SuppressWarnings("serial")
public class DialogPropertyInspector extends JPanel implements PropertyChangeListener,
AbstractPropertyInspector
{
    /** Indicates what properties (usual, expert, hidden,preferred) should be displayed. */
    private int propertyShowMode = Property.SHOW_USUAL | Property.SHOW_PREFERRED;

    private static final Dimension zeroDimension = new Dimension( 0, 0 );
    private static final JTable dummyTable = new JTable();

    private static final int MAX_RECURSION = 3;

    private JComponent contents = null; // test variable

    /** Creates instance of PropertyInspector. */
    public DialogPropertyInspector()
    {
        super(
            new BorderLayout() );
        setSize( 200, 200 );
        //inspectorActionManager = new InspectorActionManager( mobile );
        setupPopupMenu( popup = new JPopupMenu() );
        addMouseListener(
            new MouseAdapter()
            {
                @Override
                public void mousePressed( MouseEvent e )
                {
                    if ( e.isPopupTrigger() && popup.getComponentCount() > 0 )
                    {
                        popup.show( e.getComponent(), e.getX(), e.getY() );
                    }
                }

                @Override
                public void mouseReleased( MouseEvent e )
                {
                    if ( e.isPopupTrigger() && popup.getComponentCount() > 0 )
                    {
                        popup.show( e.getComponent(), e.getX(), e.getY() );
                    }
                }
            } );
    }

    /**
     * This method gets called when a bound property is changed.
     * @param evt a PropertyChangeEvent object describing the event source
     *      and the property that has changed.
     */
    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if(bViewCreating)
        {
            return;
        }
        String propertyName = evt.getPropertyName();
        boolean recreate = false;

        int ind = propertyName.lastIndexOf( '.' );
        String modifier = null;
        if ( ind > 0 )
        {
            modifier = propertyName.substring( ind );
            propertyName = propertyName.substring( 0, ind );
        }

        if ( modifier != null && ( modifier.equals( EventConstants.EVT_SET_VALUE ) ||
        modifier.equals( EventConstants.EVT_PROPERTY_ADDED ) ||
        modifier.equals( EventConstants.EVT_PROPERTY_REMOVED ) ) )
        {
            recreate = true;
        }

        Property p = componentModel.findProperty( propertyName );
        if ( p == null )
        {
            return;
        }
        if ( !( p instanceof SimpleProperty ) && recreate )
        {
            ComponentFactory.recreateChildProperties( p );
            SwingUtilities.invokeLater(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setComponentModel( componentModel );
                    }
                } );
            return;
        }
        // @pending - proper implementation
        // for the lack of better idea
        JPanel view = map.get(p.getCompleteName());
        if(view != null)
        {
            JLabel label = new JLabel(p.getDisplayName());
            JComponent editor = getEditor(p);

            view.removeAll();
            view.add(label, labelGridBagConstraints);
            view.add(editor, editorGridBagConstraints);

            validate();
            repaint();
        }
        /*
        SwingUtilities.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    setComponentModel( componentModel );
                }
            } );
        */
    }

    /** ComponentModel that is currently assotiated with PropertyInspector. */
    private ComponentModel componentModel = null;

    /**
     *  Gets current component model.
     *
     * @return the current component model
     * @pending to be declared protected
     *
     */
    protected ComponentModel getComponentModel()
    {
        return componentModel;
    }

    /**
     * Set component model.
     * @deprecated
     */
    public void setComponentModel( ComponentModel componentModel )
    {
        map.clear();
        removeAll();
        if ( componentModel == null )
        {
            return;
        }
        if ( this.componentModel != null )
        {
            this.componentModel.removePropertyChangeListener( this );
        }

        this.componentModel = componentModel;
        this.componentModel.addPropertyChangeListener( this );

        //////////
        //JComponent contents = createTabbedPropertyView(componentModel, 2);
        //add( contents, BorderLayout.CENTER );

        contents = createGroupPropertyView( componentModel, columnCount, maxRecursion );
        add(new JScrollPane( contents ), BorderLayout.CENTER );
        validate();
    }

    /** Default width of the property editor. */
    public static final int DEFAULT_EDITOR_WIDTH = 60;

    /** Default value editor to get default property editors. */
    private final ValueEditor defEditor = new ValueEditor();

    /** Default value renderers to get default property renderers. */
    private final ValueRenderer defRenderer = new ValueRenderer();

    private final HashMap<String, JPanel> map = new HashMap<>();

    private JComponent getEditor(Property childProperty)
    {
        JComponent editor = ( JComponent )defEditor.getEditorComponent( this, childProperty, false, -1, -1 );
        boolean isReadOnly = childProperty.isReadOnly();
        if ( editor == null && isReadOnly )
        {
            // try to use renderer
            try
            {
                editor = ( JComponent )defRenderer.getTableCellRendererComponent( dummyTable,
                childProperty, false, false, -1, -1 );
            }
            catch ( Exception e )
            {
                Logger.getLogger().warn( "Cannot create editor for " + childProperty.getCompleteName(), e );
            }
        }

        if ( editor != null )
        {
            // should work in most cases
            editor.setEnabled( !isReadOnly );
            Component[] comps = editor.getComponents();
            for ( int j = 0; j < comps.length; j++ )
            {
                comps[ j ].setEnabled( !isReadOnly );
            }
            Dimension editorDimension = childProperty.getEditorPreferredSize();
            if ( editorDimension == null || editorDimension.equals( zeroDimension ) )
            {
                editorDimension = new
                Dimension( DEFAULT_EDITOR_WIDTH,
                editor.getPreferredSize().height );
                childProperty.setEditorPreferredSize( editorDimension );
            }

            editor.setPreferredSize( editorDimension );
            if ( showToolTip )
            {
                SmartText descr = new SmartText();
                descr.setText( childProperty.getToolTip() );
                descr.setText( "<html>" + descr.insertBreaks( "<br>", toolTipWidth, true ) + "</html>" );
                editor.setToolTipText( descr.toString() );
            }
        }
        return editor;
    }

    private boolean insertEditor( JPanel contents, Property childProperty, int row, int column )
    {
        JComponent editor = getEditor(childProperty);

        if ( editor != null )
        {
            JPanel panel = new PropertyPanel( childProperty.getDisplayName(), editor );
            contents.add( panel,
                new GridBagConstraints( column, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets( 0, 5, 0, 5 ), 0, 0 ) );

            map.put(childProperty.getCompleteName(), panel);

            return true;
        }
        return false;
    }

    /**
     * Creates JComponent for editing property. Simple (non-composite) properties
     * placed into specified number of columns. For CompositeProperty
     * a separate tab creates and all simple sub-properties placed in it.
     */
    private JComponent createTabbedPropertyView( Property property, int columns,
    int recursionLevel )
    {
        if ( recursionLevel <= 0 )
        {
            return null;
        }
        JTabbedPane tabbedPane = null;
        JPanel contents = new JPanel();
        contents.setLayout( new GridBagLayout() );

        int row = 0;
        int column = 0;
        int propCount = property.getVisibleCount( propertyShowMode );
        // always do substitue by child for dialog
        while ( propCount == 1 /* && property.isSubstituteByChild() */ )
        {
            boolean bFound = false;
            for ( int k = 0; k < property.getPropertyCount(); k++ )
            {
                Property newProp = property.getPropertyAt( k );
                if ( newProp.isVisible( propertyShowMode ) && !( newProp instanceof SimpleProperty ) )
                {
                    bFound = true;
                    property = newProp;
                    propCount = property.getVisibleCount( propertyShowMode );
                    break;
                }
            }
            if( !bFound ) break;
        }

        for ( int i = 0; i < propCount; i++ )
        {
            Property childProperty = property.getVisiblePropertyAt( i, propertyShowMode );

            if ( !( childProperty instanceof SimpleProperty ) )
            {
                if ( tabbedPane == null )
                {
                    tabbedPane = new JTabbedPane();
                    Color temp = tabbedPane.getForeground();
                    tabbedPane.setForeground( Color.blue );
                    tabbedPane.addTab( property.getDisplayName(),
                        new JScrollPane( contents ) );
                    tabbedPane.setForeground( temp );
                }
                JComponent childPropertyView =
                createTabbedPropertyView( childProperty, columns, recursionLevel - 1 );
                if ( childPropertyView == null )
                {
                    continue;
                }
                tabbedPane.addTab( childProperty.getDisplayName(), childPropertyView );
                continue;
            }
            if ( insertEditor( contents, childProperty, row, column ) )
            {
                if ( column == columns - 1 )
                {
                    column = 0;
                    row++;
                }
                else
                {
                    column++;
                }
            }
        }
        return tabbedPane != null ? tabbedPane : ( JComponent )contents;
    }

    private boolean bViewCreating = false;

    /**
     * Creates JComponent for editing property. Simple (non-composite) properties
     * placed into specified number of columns. For CompositeProperty
     * a separate group creates and all simple sub-properties placed in it.
     */
    private JComponent createGroupPropertyView( Property property, int columns,
    int recursionLevel )
    {
        if ( recursionLevel <= 0 )
        {
            return null;
        }

        bViewCreating = true;

        JPanel contents = new JPanel();
        contents.setLayout(
            new GridBagLayout() );
        //contents.setLayout(new GridLayout(0, 1));
        //contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));
        int row = 0;
        int column = 0;
        int propCount = property.getVisibleCount( propertyShowMode );
        // always do substitue by child for dialog
        while ( propCount == 1 /*&& property.isSubstituteByChild()*/ )
        {
            boolean bFound = false;
            for ( int k = 0; k < property.getPropertyCount(); k++ )
            {
                Property newProp = property.getPropertyAt( k );
                if ( newProp.isVisible( propertyShowMode ) && !( newProp instanceof SimpleProperty ) )
                {
                    bFound = true;
                    property = newProp;
                    propCount = property.getVisibleCount( propertyShowMode );
                    break;
                }
            }
            if( !bFound ) break;
        }

        for ( int i = 0; i < propCount; i++ )
        {
            Property childProperty = property.getVisiblePropertyAt( i, propertyShowMode );
            if ( !( childProperty instanceof SimpleProperty ) )
            {
                JPanel childPropertyView =
                ( JPanel )createGroupPropertyView( childProperty, columns, recursionLevel - 1 );
                if ( childPropertyView == null )
                {
                    continue;
                }
                if ( childPropertyView.getComponentCount() == 0 )
                {
                    continue;
                }
                TitledBorder border = new
                TitledBorder( BorderFactory.createEtchedBorder(),
                childProperty.getDisplayName() );
                childPropertyView.setBorder( border );

                column = 0;
                row++;
                contents.add( childPropertyView,
                    new GridBagConstraints( 0, row, columns, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets( 0, 5, 5, 5 ), 0, 0 ) );
                row++;
                continue;
            }
            if ( insertEditor( contents, childProperty, row, column ) )
            {
                if ( column == columns - 1 )
                {
                    column = 0;
                    row++;
                }
                else
                {
                    column++;
                }
            }
        }
        //return new JScrollPane(contents);
        bViewCreating = false;
        return contents;
    }

    private final GridBagConstraints labelGridBagConstraints =
        new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets( 0, 5, 0, 5 ), 0, 0 );
    private final GridBagConstraints editorGridBagConstraints =
        new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                new Insets( 0, 0, 0, 5 ), 0, 0 );

    class PropertyPanel extends JPanel
    {
        PropertyPanel( String name, Component editor )
        {
            super(new GridBagLayout());
            JLabel label = new JLabel(name);

            add(label, labelGridBagConstraints);
            add(editor, editorGridBagConstraints);
            // debug
            //label.setBorder( BorderFactory.createLineBorder( Color.red ) );
            //setBorder( BorderFactory.createLineBorder( Color.blue ) );
            // debug
        }
    }

    /** Indicates whether is needed to show tool tip (property description). */
    private boolean showToolTip = true;

    /**
     * Returns show tooltip flag. Show tooltip flag indicates whether is needed to show tool tip (property description).
     *
     * @return  the show tooltip flag.
     */
    public boolean isShowToolTip()
    {
        return showToolTip;
    }

    /**
     * Sets show tooltip flag.
     *
     * @param showToolTip the show tooltip flag
     * @see #isShowToolTip
     */
    public void setShowToolTip( boolean showToolTip )
    {
        this.showToolTip = showToolTip;
    }

    /** Indicates the width of tooltip in characters for line wrapping */
    private int toolTipWidth = 50;

    /**
     * Gets tooltip width
     *
     * @return the tooltip width in characters.
     */
    public int getToolTipWidth()
    {
        return toolTipWidth;
    }

    /**
     * Sets tooltip width
     *
     * @param toolTipWidth the specified tooltip width in characters
     */
    public void setToolTipWidth( int toolTipWidth )
    {
        this.toolTipWidth = toolTipWidth;
    }

    private JPopupMenu popup;

    /**
     * Gets current popup menu of Property Inspector.
     *
     * @return the current popup menu of Property Inspector.
     */
    public JPopupMenu getPopup()
    {
        return popup;
    }

    protected void setupPopupMenu( JPopupMenu popup )
    {
    }

    void logNonCriticalErrorMessage( String msg, Exception e )
    {
        if(msg == null)
            msg = "";
        if(e == null)
            Logger.getLogger().warn( msg );
        else
            Logger.getLogger().warn( msg, e );
    }

    /**
     * This method is a main entry point for setting a component
     * to be explored in PropertyInspector.
     */
    public void explore( Object bean )
    {
        ComponentModel mdl = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
        setComponentModel( mdl );
    }

    /**
     * This method is a main entry point for setting a component
     * to be explored in PropertyInspector.
     */
    public void explore( Class<?> beanClass )
    {
        setComponentModel( beanClass == null ? null : ComponentFactory.getModel(beanClass, ComponentFactory.Policy.UI) );
    }

    /**
     * This method clears a PropertyInspector.
     */
    @Override
    public void clear()
    {
        explore( null );
    }

    /**
     * This method cleanups a PropertyInspector.
     */
    public void cleanup()
    {
    }

    /**
     * This method is a wrapper method
     * to listen for <code>PropertyChangeEvent</code>s
     * on properties that do not fire this event themselves.
     * Then if such property is changed using PropertyInspector
     * the event is fired to any interested party
     * which registered itself using this method.
     * Importantly that property name must be specified
     * using its completr name
     * <br><br>
     * Example:<br>
     * <code>
     * inspector.addPropertyChangeListener( "arrayProperty/[0]/stringProperty", listener );
     * </code>
     */
    @Override
    public void addPropertyChangeListener( String propertyName,
    PropertyChangeListener listener )
    {
        Property prop = componentModel.findProperty( propertyName );
        if ( prop != null )
        {
            prop.addPropertyChangeListener( listener );
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property
     * referenced via complete name
     */
    @Override
    public void removePropertyChangeListener( String propertyName,
    PropertyChangeListener listener )
    {
        Property prop = componentModel.findProperty( propertyName );
        if ( prop != null )
        {
            prop.removePropertyChangeListener( listener );
        }
    }

    /**
     * Adds the specified bean event listener to receive bean events from Dialog Property Inspector.
     *
     * @param propertyName
     *                  the name of the property to listen on
     * @param eventName the name of the event to listen for
     * @param listener  the BeanEventListener to be added
     *
     * @pending Write Tests!
     */
    public void addBeanEventListener( String propertyName, String eventName,
                                      BeanEventListener listener )
    {
        Property prop;
        if ( propertyName == null || propertyName.length() == 0 )
        {
            prop = componentModel;
        }
        else
        {
            prop = componentModel.findProperty( propertyName );
        }
        if ( prop instanceof CompositeProperty )
        {
            ( ( CompositeProperty )prop ).addBeanEventListener( eventName, listener );
        }
    }

    /**
     * Removes the specified bean event listener from bean events listeners list.
     *
     * @param propertyName
     *                  the name of the property that was listened on
     * @param eventName the name of the event that was listened for
     * @param listener  the BeanEventListener to be removed
     */
    public void removeBeanEventListener( String propertyName, String eventName,
                                         BeanEventListener listener )
    {
        Property prop;
        if ( propertyName == null || propertyName.length() == 0 )
        {
            prop = componentModel;
        }
        else
        {
            prop = componentModel.findProperty( propertyName );
        }
        if ( prop instanceof CompositeProperty )
        {
            ( ( CompositeProperty )prop ).removeBeanEventListener( eventName, listener );
        }
    }

    private int columnCount;
    public void setColumnCount( int value )
    {
        columnCount = value;
    }


    private int maxRecursion = MAX_RECURSION;

    /**
     * Gets max level of bean properties parsing.
     * @return Max level of bean properties parsing.
     */
    public int getMaxRecursion()
    {
        return maxRecursion;
    }

    /**
     * Set max level of bean properties parsing.
     * @param value  level for which bean should be explored
     */
    public void setMaxRecursion( int value )
    {
        maxRecursion = value;
    }


    /**
     * Returns current show mode.
     * @return Current show mode.
     * @pending Move constants from PropertyInspector to base class or declare it in this class.
     */
    public int getPropertyShowMode()
    {
        return propertyShowMode;
    }

    /**
     * Sets show mode
     *
     * @param propertyShowMode the new show mode
     * @pending Move constants from PropertyInspector to base class or declare it in this class.
     */
    public void setPropertyShowMode( int propertyShowMode )
    {
        this.propertyShowMode = propertyShowMode;
    }

    @Override
    public Property getProperty( Point pt )
    {
        throw new RuntimeException( "Not implemented!" );
    }

    @Override
    public Rectangle getCellRect( Point pt )
    {
        throw new RuntimeException( "Not implemented!" );
    }

}
