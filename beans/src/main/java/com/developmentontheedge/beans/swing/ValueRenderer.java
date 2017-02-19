package com.developmentontheedge.beans.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.editors.BooleanEditor;
import com.developmentontheedge.beans.editors.PropertyEditorEx;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.Property;

/** @todo comments */
public class ValueRenderer implements TableCellRenderer
{
    protected AbstractPropertyInspector propertyInspector;
    protected RendererPool pool = new RendererPool();
    protected JComponent compositeView;
    protected static final Border noFocusBorder = new EmptyBorder( 1, 1, 1, 1 );
    protected boolean variableRowHeight = false;

    private final LocalMouseListener mouseListener = new LocalMouseListener();

    public ValueRenderer()
    {
        compositeView = new LightJPanel();
    }

    public ValueRenderer( AbstractPropertyInspector inspector )
    {
        this();
        propertyInspector = inspector;
    }

    public void registerListeners()
    {
        propertyInspector.addMouseListener(mouseListener);
        propertyInspector.addMouseMotionListener(mouseListener);
    }

    public void unregisterListeners()
    {
        propertyInspector.removeMouseListener(mouseListener);
        propertyInspector.removeMouseMotionListener(mouseListener);
    }

    /** Format the view as read only property. */
    protected void formatReadOnly( Property property, Component view )
    {
        view.setForeground( property.isReadOnly() ? Color.gray : Color.black );
    }

    /** @returns view if value is null */
    public Component getViewForNullValue( JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column )
    {
        // setBackground(grayColor);
        return pool.nextDefaultRenderer().getTableCellRendererComponent( table, "", isSelected,
        hasFocus, row, column );
    }

    /**
     * Creates property value view based on its PropertyEditor.
     * @todo high paintable PropertyEditor is not supported now.
     * @todo medium format view depending on hasFocus, isSelected for custom renderer
     */
    protected Component getViewFromPropertyEditor( JTable table, Property property,
    boolean isSelected, boolean hasFocus,
    int row, int column )
    {
        Component view = null;
        PropertyEditor editor = getEditor(property);
        if(editor != null)
        {
            if ( editor.isPaintable() && editor instanceof PropertyEditorEx )
                view = ((PropertyEditorEx)editor).getCustomRenderer(table, isSelected, hasFocus);
            else
            {
                Object value = property.getValue();
                String text = value instanceof String ? (String)value :
                editor.getAsText();
                view = pool.nextDefaultRenderer().getTableCellRendererComponent(table, text,
                                                            isSelected, hasFocus, row, column );
                formatReadOnly(property, view);
            }
        }

        return view;
    }

    /**
     * @todo compositeView - should be light like DefaultCellRenderer
     * @todo try to use BorderLayout
     * @pending - currently is based on JTable settings. So it will have problems in
     */
    public Component getCompositeView( JTable table, Property property, boolean isSelected,
    boolean hasFocus, int row, int column )
    {
        String propertyNameList = ( String )property.getDescriptor().getValue(
        BeanInfoConstants.COMPOSITE_EDITOR_PROPERTY_LIST );
        if ( propertyNameList == null )
        {
            return null;
        }

        StringTokenizer tokens = new StringTokenizer( propertyNameList, ";, " );

        LayoutManager layoutManager = ( LayoutManager )property.getDescriptor().
        getValue( BeanInfoConstants.COMPOSITE_EDITOR_LAYOUT_MANAGER );
        if ( layoutManager == null )
        {
            layoutManager = new GridLayout( 1, tokens.countTokens() );
        }

        compositeView.removeAll();
        compositeView.setLayout( layoutManager );

        while ( tokens.hasMoreTokens() )
        {
            String name = tokens.nextToken();
            Property p = property.findProperty( name );

            if ( p == null )
            {
                Logger.getLogger().error(
                        "ValueRenderer#composite editor: property " + name + " not found in " + property.getCompleteName() );
            }
            else
            {
                Component view = getPropertyView( table, p, false, false, row, column );
                compositeView.add( view );
            }
        }

        // format composite view depending from selection and focus
        compositeView.setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
        compositeView.setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );

        if ( !hasFocus )
        {
            compositeView.setBorder( noFocusBorder );
        }
        else
        {
            compositeView.setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder" ) );
            if ( table.isCellEditable( row, column ) )
            {
                compositeView.setForeground( UIManager.getColor( "Table.focusCellForeground" ) );
                compositeView.setBackground( UIManager.getColor( "Table.focusCellBackground" ) );
            }
        }

        // ---- optimization to avoid painting background ----
        Color back = compositeView.getBackground();
        boolean colorMatch = ( back != null ) && ( back.equals( table.getBackground() ) ) && table.isOpaque();
        compositeView.setOpaque( !colorMatch );
        // ---- end optimization to aviod painting background ----

        return compositeView;
    }

    /** Creates view for the specified property. */
    public Component getPropertyView( JTable table, Property property, boolean isSelected,
    boolean hasFocus, int row, int column )
    {
        Component view;
        view = getCompositeView( table, property, isSelected, hasFocus, row, column );
        if ( view != null )
        {
            return view;
        }

        view = getViewFromPropertyEditor( table, property, isSelected, hasFocus, row, column );
        if ( view != null )
        {
            return view;
        }

        Object value = property.getValue();
        if(Boolean.parseBoolean(String.valueOf(property.getDescriptor().getValue(BeanInfoConstants.PASSWORD_FIELD))))
            value = "";
        if ( value != null && value instanceof Boolean )
        {
            PropertyEditorEx editor = new BooleanEditor();
            editor.setValue( value );
            view = editor.getCustomRenderer( table, isSelected, hasFocus );
        }
        else if ( value != null && value instanceof Image )    
        {
             view = new JLabel( new ImageIcon( ( Image )value ) );
        }
        else if ( value != null && value instanceof ImageIcon )    
        {
             view = new JLabel( ( ImageIcon )value );
        }
        else
        {
            if ( value == null || !( value instanceof String || value instanceof Number ) )
            {
                value = "";
            }
            else if ( value instanceof Number )
            {
                value = formatNumber( (Number)value, property );
            }

            view = pool.nextDefaultRenderer().getTableCellRendererComponent( table, value, isSelected,
            hasFocus, row, column );
        }

        formatReadOnly( property, view );
        return view;
    }

    /**
     * Creates the component which will represent value of property for current cell.
     * @todo comments
     */
    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column )
    {
        pool.flush();

        Component c;
        if ( value != null && value instanceof Property )
            c = getPropertyView( table, ( Property )value, isSelected, hasFocus, row, column );
        else
            return getViewForNullValue( table, value, isSelected, hasFocus, row, column );

        if( variableRowHeight && column <  table.getColumnModel().getColumnCount() )
        {
            if( row != currentRow )
            {
                updateRowHeight(table, currentRow);
                currentRow = row;
            }

            c.setSize( table.getColumnModel().getColumn(column).getWidth(),
                       c.getPreferredSize().height);

           //for spreading of collumn
            if (table.getRowHeight(row) < c.getPreferredSize().height ) 
                table.setRowHeight(row, c.getPreferredSize().height);

               if (column >= 0 && column < 100)
                   cellHeights[column] = c.getPreferredSize().height;
        }

        return c;
    }

    int currentRow;
    int[] cellHeights = new int[100];

    protected void updateRowHeight(JTable table, int row)
    {
        int max = 16;
        for( int i=0; i<cellHeights.length; i++)
        {
            max = Math.max(max, cellHeights[i]);
            cellHeights[i] = 0;
        }

        //fitting collumn
        if( table.getRowHeight(row) - max > 5 ) 
            table.setRowHeight(row, max);
    }

    ////////////////////////////////////////
    // Number formatting issues
    //

    protected static final DecimalFormat customNumberFormat  = new DecimalFormat();

    protected NumberFormat defaultNumberFormat = NumberFormat.getNumberInstance( Locale.US );

    protected String formatNumber( Number number, Property property )
    {
        Object format = property.getDescriptor().getValue(BeanInfoConstants.NUMBER_FORMAT);
        
        if( format instanceof NumberFormat )
            return ((NumberFormat)format).format( number );
                    
        if( format instanceof String )
        {
            if( BeanInfoConstants.NUMBER_FORMAT_NONE.equals(format) )
                return number.toString();

            customNumberFormat.applyPattern( (String)format );  
            return customNumberFormat.format( number );
        }

        if( defaultNumberFormat != null )
            return defaultNumberFormat.format( number );

        return number.toString();
    }

    ////////////////////////////////////////
    // MouseEvent processing issues
    //

    private PropertyEditor curEditor;
    private Property curProperty;
    private Rectangle lastRect;
    private Point lastPoint;

    private PropertyEditor getEditor( Property property )
    {
        PropertyEditor editor = null;
        try
        {
            Class<?> c = property.getPropertyEditorClass();
            if ( c != null )
            {
                editor = ( PropertyEditor )c.newInstance();

                if ( editor instanceof PropertyEditorEx )
                {
                    Object owner = property.getOwner();
                    if( owner instanceof Property.PropWrapper )
                        owner = ( ( Property.PropWrapper )owner ).getOwner();
                    ( ( PropertyEditorEx )editor ).setBean( owner );
                    ( ( PropertyEditorEx )editor ).setDescriptor( property.getDescriptor() );
                }

                Object value = property.getValue();
                editor.setValue( value );
            }
        }
        catch ( Exception ex )
        {
            Logger.getLogger().error( "Error when getting editor", ex );
        }

        return editor;
    }

    private void dispatchEvent( MouseEvent e )
    {
        int type = e.getID();
        Point pt = e.getPoint();
        MouseEvent newEvent = null;

        Property property = propertyInspector.getProperty( pt );
        if ( property == null )
        {
            return;
        }

        Rectangle rect = propertyInspector.getCellRect( pt );

        if ( property != null && rect != null )
        {
            int x = pt.x - rect.x;
            int y = pt.y - rect.y;

            newEvent = new MouseEvent( ( Component )e.getSource(), e.getID(), e.getWhen(),
            e.getModifiers(), x, y, e.getClickCount(), e.isPopupTrigger() );
        }

        if ( property != curProperty || type == MouseEvent.MOUSE_EXITED )
        {
            curProperty = property;
            if ( curEditor != null && curEditor instanceof MouseListener )
            {
                int xx = 0, yy = 0;

                if ( lastPoint != null && lastRect != null )
                {
                    xx = lastPoint.x - lastRect.x;
                    yy = lastPoint.y - lastRect.y;
                }

                e = new MouseEvent( ( Component )e.getSource(), e.getID(), e.getWhen(), e.getModifiers(),
                xx, yy, e.getClickCount(), e.isPopupTrigger() );
                ( ( MouseListener )curEditor ).mouseExited( e );

                if ( type == MouseEvent.MOUSE_EXITED )
                {
                    return;
                }
            }

            curEditor = getEditor( property );
            if ( curEditor != null && curEditor instanceof MouseListener )
            {
                ( ( MouseListener )curEditor ).mouseEntered( newEvent );
                if ( type == MouseEvent.MOUSE_ENTERED )
                {
                    return;
                }
            }
        }

        lastRect = rect;
        lastPoint = pt;

        if ( curEditor == null )
        {
            return;
        }

        if ( curEditor instanceof MouseListener )
        {
            switch ( type )
            {
                case MouseEvent.MOUSE_CLICKED:
                    ( ( MouseListener )curEditor ).mouseClicked( newEvent );
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    ( ( MouseListener )curEditor ).mousePressed( newEvent );
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    ( ( MouseListener )curEditor ).mouseReleased( newEvent );
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    ( ( MouseListener )curEditor ).mouseEntered( newEvent );
                    break;

            }
        }

        if ( curEditor instanceof MouseMotionListener )
        {
            switch ( type )
            {
                case MouseEvent.MOUSE_DRAGGED:
                    ( ( MouseMotionListener )curEditor ).mouseDragged( newEvent );
                    break;
                case MouseEvent.MOUSE_MOVED:
                    ( ( MouseMotionListener )curEditor ).mouseMoved( newEvent );
                    break;
            }
        }
    }

    class LocalMouseListener implements MouseMotionListener, MouseListener
    {
        @Override
        public void mouseEntered( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mouseExited( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mouseClicked( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mouseReleased( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mousePressed( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mouseDragged( MouseEvent e )
        {
            dispatchEvent( e );
        }

        @Override
        public void mouseMoved( MouseEvent e )
        {
            dispatchEvent( e );
        }
    }


    /** @pending draft version */
    static class RendererPool
    {
        int current = 0;
        TableCellRenderer[] pool = { new DefaultTableCellRenderer(),
                                     new DefaultTableCellRenderer(),
                                     new DefaultTableCellRenderer(),
                                     new DefaultTableCellRenderer(),
                                     new DefaultTableCellRenderer()
                                   };

        public RendererPool()
        {
        }

        /** @todo high pool extention */
        public TableCellRenderer nextDefaultRenderer()
        {
            return pool[ current++ ];
        }

        public void flush()
        {
            current = 0;
        }

    }

    ////////////////////////////////////////
    // LightJPanel
    //


    /**
     * This class inherits from <code>JPanel</code>.
     * However <code>JTable</code> employs a unique mechanism for rendering
     * its cells and therefore requires some slightly modified behavior
     * from its cell renderer. So this class
     * overrides the <code>validate</code>, <code>revalidate</code>,
     * <code>repaint</code>, and <code>firePropertyChange</code> methods to be
     * no-ops.
     * @pending validate - when and how it should be called
     */
    static class LightJPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;

        //public void validate() {}
        @Override
        public void revalidate()
        {
        }

        @Override
        public void repaint( long tm, int x, int y, int width, int height )
        {
        }

        @Override
        public void repaint( Rectangle r )
        {
        }

        @Override
        public void firePropertyChange( String propertyName, boolean oldValue, boolean newValue )
        {
        }
    }
}
