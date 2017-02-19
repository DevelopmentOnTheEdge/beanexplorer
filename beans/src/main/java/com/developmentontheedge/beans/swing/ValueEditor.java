package com.developmentontheedge.beans.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.editors.BooleanEditor;
import com.developmentontheedge.beans.editors.ColorEditor;
import com.developmentontheedge.beans.editors.DoubleEditor;
import com.developmentontheedge.beans.editors.FloatEditor;
import com.developmentontheedge.beans.editors.IntegerEditor;
import com.developmentontheedge.beans.editors.LongEditor;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.editors.StringEditor;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.model.PropertyChangeUndo;
import com.developmentontheedge.beans.undo.TransactionEvent;

public class ValueEditor extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = 1L;

    /** It is needed to manage transaction issues when property value is changed. */
    protected AbstractPropertyInspector propertyInspector;

    protected int clickCountToStart = 1;

    /** Editing property. */
    protected Property editingProperty;

    /** Indicates whether composite editor is used. */
    protected boolean isEditorComposite;

    protected Component editor;

    public ValueEditor()
    {
    }

    public ValueEditor(AbstractPropertyInspector propertyInspector)
    {
        this.propertyInspector = propertyInspector;
    }

    ////////////////////////////////////////
    //  Implementing the CellEditor Interface
    //

    @Override
    public Object getCellEditorValue()
    {
        return editingProperty;
    }


    @Override
    public boolean isCellEditable(EventObject anEvent)
    {
        if( anEvent instanceof MouseEvent )
            return ( (MouseEvent)anEvent ).getClickCount() >= clickCountToStart;

        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        // here should be only properties
        if( ! ( value instanceof Property ) )
            editor = null;
        else
        {
            editingProperty = (Property)value;
            editor = getCompositeEditor( table, editingProperty, isSelected, row, column );
            if( editor == null )
                editor = getEditorComponent( table, editingProperty, isSelected, row, column );
        }

        return editor;
    }

    ////////////////////////////////////////
    // Utility methods
    //

    protected Component getCompositeEditor(JComponent parent, Property property, boolean isSelected, int row, int column)
    {
        String propertyNameList = (String)property.getDescriptor().getValue( BeanInfoConstants.COMPOSITE_EDITOR_PROPERTY_LIST );
        isEditorComposite = propertyNameList != null;
        if( !isEditorComposite )
            return null;

        StringTokenizer tokens = new StringTokenizer( propertyNameList, ";, " );

        LayoutManager layoutManager = (LayoutManager)property.getDescriptor().getValue( BeanInfoConstants.COMPOSITE_EDITOR_LAYOUT_MANAGER );
        if( layoutManager == null )
            layoutManager = new GridLayout( 1, tokens.countTokens() );

        JComponent compositeView = new JPanel();
        compositeView.setOpaque( false );
        compositeView.removeAll();
        compositeView.setLayout( layoutManager );

        while( tokens.hasMoreTokens() )
        {
            String name = tokens.nextToken();
            Property p = property.findProperty( name );

            if( p == null )
                Logger.getLogger().error( "ValueEditor#composite editor: property " + name + " not found in " + property.getCompleteName() );
            else
            {
                Component view = getEditorComponent( parent, p, isSelected, row, column );
                compositeView.add( view );
            }
        }

        return compositeView;
    }


    /**
     * creates a component for custom editor if it is possible, owervise return null
     * sets initial value for property editor in any case
     */
    protected Component getEditorComponent(JComponent parentComp, Property property, boolean isSelected, int row, int column)
    {
        // get the property value and create new instance of value, if needed
        Object value = property.getValue();
        if( value == null )
            value = newValueInstance( property );

        Class<?> propertyEditorClass = getEditorClass( property, value );
        if( propertyEditorClass == null )
        {
            Logger.getLogger().warn( "Null editor class of property " + property.getCompleteName() );
            return null;
        }

        Component editComp = null;
        PropertyEditor propertyEditor = null;
        EditorDelegate delegate = null;
        try
        {
            propertyEditor = (PropertyEditor)propertyEditorClass.newInstance();
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "can not instantiate property editor of " + propertyEditorClass, e );
            return null;
        }

        // set information about invoking class to editor which implements the PropertyEditorEx interface
        if( propertyEditor instanceof PropertyEditorEx )
        {
            Object owner = property.getOwner();
            if( owner instanceof Property.PropWrapper )
                owner = ( (Property.PropWrapper)owner ).getOwner();
            ( (PropertyEditorEx)propertyEditor ).setBean( owner );
            ( (PropertyEditorEx)propertyEditor ).setDescriptor( property.getDescriptor() );
        }

        // set the initial value
        propertyEditor.setValue( value );


        // get custom editor if it is possible, of course...
        if( propertyEditor.supportsCustomEditor() )
        {
            if( propertyEditor instanceof PropertyEditorEx )
                editComp = ( (PropertyEditorEx)propertyEditor ).getCustomEditor( parentComp, isSelected );
            else
                editComp = propertyEditor.getCustomEditor();
            final PropertyEditor parent = propertyEditor;
            delegate = new EditorDelegate( property )
            {
                @Override
                public Object getValue()
                {
                    return parent.getValue();
                }
            };
            propertyEditor.addPropertyChangeListener( delegate );
            editComp.addFocusListener( delegate );
        }
        else
        {
            String[] tags = propertyEditor.getTags();
            if( tags == null )
            {
                final JTextField tf = new JTextField( propertyEditor.getAsText() );
                editComp = tf;
                final PropertyEditor parent = propertyEditor;
                delegate = new EditorDelegate( property )
                {
                    @Override
                    public Object getValue()
                    {
                        try
                        {
                            parent.setAsText( tf.getText() );
                        }
                        catch( IllegalArgumentException ex )
                        {
                            //JOptionPane.showMessageDialog(null,"Illegal argument","Error",JOptionPane.ERROR_MESSAGE);
                        }

                        return parent.getValue();
                    }
                };
                tf.addActionListener( delegate );
                editComp.addFocusListener( delegate );
            }

            // tag editor's support
            else
            {

                final JComboBox<String> cb = new JComboBox<>( tags );
                cb.setSelectedItem( propertyEditor.getAsText() );
                editComp = cb;
                final PropertyEditor parent = propertyEditor;
                delegate = new EditorDelegate( property )
                {
                    @Override
                    public Object getValue()
                    {
                        String str = (String)cb.getSelectedItem();
                        try
                        {
                            parent.setAsText( str );
                        }
                        catch( IllegalArgumentException e )
                        {
                        }
                        return parent.getValue();
                    }
                };
                cb.addActionListener( delegate );
            }
        }

        // return the component for custom editor
        return editComp;
    }

    /** Create type dependent cell editor */
    protected Class<?> getEditorClass(Property property, Object value)
    {
        Class<?> c = property.getPropertyEditorClass();
        if( c != null )
        {
            return c;
        }
        if( value instanceof Boolean )
        {
            return BooleanEditor.class; // for obfuscation
        }
        else if( value instanceof Integer )
        {
            return IntegerEditor.class; // for obfuscation
        }
        else if( value instanceof Long )
        {
            return LongEditor.class; // for obfuscation
        }
        else if( value instanceof Float )
        {
            return FloatEditor.class; // for obfuscation
        }
        else if( value instanceof Double )
        {
            return DoubleEditor.class; // for obfuscation
        }
        else if( value instanceof Color )
        {
            return ColorEditor.class; // for obfuscation
        }

        else if( value instanceof String )
        {
            return StringEditor.class; // for obfuscation
        }
        // PENDING
        // PropertyInspector seems to process more or less correctly
        // when null editor is returned
        // BUT whether we really want to return null?
        return null;
    }

    /** Create a new value instance */
    protected Object newValueInstance(Property property)
    {
        Object newValue = null;
        try
        {
            Class<?> c = property.getValueClass();
            if( c.getName().equals( "java.lang.Class" ) || c.getName().equals( "java.lang.reflect.Method" ) )
            {
                // only JVM can do this ;-)
                return null;
            }
            String typeName = c.getName();
            try
            {
                if( c.isArray() )
                {
                    newValue = Array.newInstance( c.getComponentType(), 0 );
                }
                else if( c.isPrimitive() || typeName.indexOf( "java.lang" ) >= 0 )
                {
                    newValue = ComponentFactory.instanceForClass( c );
                }
                else
                {
                    newValue = c.newInstance();
                }
            }
            catch( java.lang.InstantiationException exc )
            {
                newValue = ComponentFactory.findStaticInstance( c );
            }
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "can not initialize empty value of property = " + property.getCompleteName(), e );
        }
        return newValue;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void fireEditingStopped()
    {
        super.fireEditingStopped();
    }

    ////////////////////////////////////////
    //
    //  Protected EditorDelegate class
    //

    protected abstract class EditorDelegate implements ActionListener, ItemListener, PropertyChangeListener, FocusListener
    {
        private final Property property;

        public EditorDelegate(Property property)
        {
            this.property = property;
        }

        public abstract Object getValue();

        @Override
        public void actionPerformed(ActionEvent e)
        {
            doAction( "action performed", e.getSource() );
        }

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            doAction( "item state changed", e.getSource() );
        }

        @Override
        public void propertyChange(PropertyChangeEvent e)
        {
            doAction( "property change", e.getSource() );
        }

        @Override
        public void focusLost(FocusEvent e)
        {
            doAction( "focus lost", e.getSource() );
        }

        @Override
        public void focusGained(FocusEvent e)
        {
        }

        ////////////////////////////////////

        /**
         * @pending fireEditingStopped - if it is not commented boolean editor does not work
         * @pending logging
         * @pending logging for catch exception
         */
        private void doAction(String eventType, Object source)
        {
            Object value = getValue();
            Object oldValue = property.getValue();
            boolean equal = ( oldValue == null ) && ( value == null ) || ( oldValue != null ) && oldValue.equals( value );
            if( !equal )
            {
                try
                {
                    PropertyInspector pi = null;
                    TabularPropertyInspector tpi = null;
                    if( propertyInspector != null && propertyInspector instanceof PropertyInspector )
                    {
                        pi = (PropertyInspector)propertyInspector;
                    }
                    else if( propertyInspector != null && propertyInspector instanceof TabularPropertyInspector )
                    {
                        tpi = (TabularPropertyInspector)propertyInspector;
                    }

                    if( pi != null )
                    {
                        pi.fireStartTransaction( new TransactionEvent( property.getOwner(), "property change: "
                                + property.getCompleteName() ) );
                    }
                    else if( tpi != null )
                    {
                        tpi.fireStartTransaction( new TransactionEvent( property.getOwner(), "property change: "
                                + property.getCompleteName() ) );
                    }
                    property.setValue( value );

                    if( pi != null )
                    {
                        pi.fireAddEdit( new PropertyChangeUndo( property, oldValue, value ) );
                        pi.fireCompleteTransaction();
                    }
                    else if( tpi != null )
                    {
                        tpi.fireAddEdit( new PropertyChangeUndo( property, oldValue, value ) );
                        tpi.fireCompleteTransaction();
                    }

                }
                catch( Exception e )
                {
                    Logger.getLogger().error( "EditorDelegate set value error." + e );
                }
            }

            if( !isEditorComposite && ( !eventType.equals( "focus lost" ) || source == editor ) )
                fireEditingStopped();
        }
    }
}
