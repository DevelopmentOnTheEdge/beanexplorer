package com.developmentontheedge.beans.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.UndoableEdit;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.PropertyDescriptorEx;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.swing.table.BeanTableModelAdapter;
import com.developmentontheedge.beans.swing.table.Column;
import com.developmentontheedge.beans.swing.table.ColumnModel;
import com.developmentontheedge.beans.swing.table.ColumnWithSort;
import com.developmentontheedge.beans.swing.table.DefaultRowModel;
import com.developmentontheedge.beans.swing.table.RowModel;
import com.developmentontheedge.beans.swing.table.SortButtonRenderer;
import com.developmentontheedge.beans.swing.table.SortedBeanTableModelAdapter;
import com.developmentontheedge.beans.swing.table.SortedTableModel;
import com.developmentontheedge.beans.undo.Transactable;
import com.developmentontheedge.beans.undo.TransactionEvent;
import com.developmentontheedge.beans.undo.TransactionListener;

/** This property inspector display several beans of same kind in tabular view (like JTable). */
public class TabularPropertyInspector extends JPanel implements AbstractPropertyInspector, Transactable
{
    private static final long serialVersionUID = 1L;

    private JScrollPane scrollPane;

    /** Creates instance of new TabularPropertyInspector. */
    public TabularPropertyInspector()
    {
        setupPopupMenu( popup = new JPopupMenu() );
        initTable();
    }

    /** Current popup menu of TabularPropertyInspector. */
    private JPopupMenu popup;

    /**
     * Gets current popup menu of Property Inspector.
     * @return the current popup menu of Property Inspector.
     */
    public JPopupMenu getPopup()
    {
        return popup;
    }

    /**
     * Initializes Tabular Property Inspector's popup menu
     * @param popup  the menu
     */
    protected void setupPopupMenu( JPopupMenu popup )
    {
    }

    /** Indicates what properties (usual, expert, hidden,preferred) should be displayed. */
    private int propertyShowMode = PropertyInspector.SHOW_USUAL | PropertyInspector.SHOW_PREFERRED;

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
     * Sets show mode for the properties of the beans
     * @param propertyShowMode the new show mode
     * @pending Move constants from PropertyInspector to base class or declare it in this class.
     */
    public void setPropertyShowMode( int propertyShowMode )
    {
        this.propertyShowMode = propertyShowMode;
        if ( tableModel != null )
        {
            tableModel.getColumnModel().setShowMode( propertyShowMode );
            tableModel.fireTableChanged(
                new TableModelEvent( tableModel ) );
        }
    }

    /** Determines is header for each row will be visible. */
    private boolean rowHeader = true;

    /** Retruns flag whether rows are numbered */
    public boolean getRowHeader()
    {
        return rowHeader;
    }

    /**
     * Specifies whether the rows should be numbered
     */
    public void setRowHeader( boolean rowHeaderVisibility )
    {
        int prefSize = 75;
        if ( table != null && table.getColumnCount() > 1 )
        {
            prefSize = table.getColumnModel().getColumn( 1 ).getPreferredWidth();
        }

        boolean oldValue = rowHeader;
        rowHeader = rowHeaderVisibility;
        if ( tableModel != null )
        {
            tableModel.setRowHeader( rowHeader );
        }
        firePropertyChange( "rowHeader", oldValue, rowHeader );

        if ( table != null )
        {
            TableColumnModel columnModel = table.getColumnModel();
            if ( columnModel != null && table.getColumnCount() > 0 )
            {
                TableColumn col = columnModel.getColumn( 0 );
                if ( col != null )
                {
                    col.setResizable( !rowHeaderVisibility );
                    if ( rowHeaderVisibility )
                    {
                        col.setPreferredWidth( 30 );
                        col.setMaxWidth( 30 );
                        col.setMinWidth( 30 );
                    }
                    else
                    {
                        col.setMaxWidth( 300 );
                        col.setMinWidth( 0 );
                        col.setPreferredWidth( prefSize );
                    }
                }
            }
        }
    }

    /** Flag that indicates whether column sorting is enabled. */
    protected boolean sortEnabled = true;

    /**
     * Returns flag specifying whether column sorting is enabled
     */
    public boolean getSortEnabled()
    {
        return sortEnabled;
    }

    protected BeanTableModelAdapter createSortedTableModel( RowModel rowModel, ColumnModel options )
    {
      return new SortedBeanTableModelAdapter( rowModel, options );            
    }

    /**
     * Specifies whether column sorting is enabled
     */
    public void setSortEnabled( boolean enabled )
    {
        boolean oldValue = sortEnabled;
        sortEnabled = enabled;
        if(tableModel == null)
        {
            firePropertyChange( "sortEnabled", oldValue, sortEnabled );
            return;
        }
        boolean prevRowHeader = tableModel.getRowHeader();
        RowModel rowModel = tableModel.getRowModel();
        ColumnModel options = tableModel.getColumnModel();
        tableModel.unregisterListeners();
        tableModel = null;
        if ( enabled )
        {
            tableModel = createSortedTableModel( rowModel, options );
            tableModel.getColumnModel().setSortEnabled( true );
        }
        else
        {
            tableModel = new BeanTableModelAdapter(
            rowModel,
            options );
            tableModel.getColumnModel().setSortEnabled( false );
        }
        tableModel.setRowHeader( prevRowHeader );
        table.setModel( tableModel );

        if ( getRowHeader() )
        {
            TableColumn col = table.getColumnModel().getColumn( 0 );
            col.setResizable( false );
            col.setMinWidth( 30 );
            col.setPreferredWidth( 30 );
            col.setMaxWidth( 30 );
        }

        firePropertyChange( "sortEnabled", oldValue, sortEnabled );
    }

    /**
     * Returns default NumberFormat used for displaying number value of property.
     */
    public NumberFormat getDefaultNumberFormat()
    {
        return valueRenderer.defaultNumberFormat;
    }
   
    /**
     * Sets up default NumberFormat used for displaying number value of property.
     *
     * @see java.text.NumberFormat 
     * @see BeanInfoConstants#NUMBER_FORMAT
     * @see PropertyDescriptorEx#setNumberFormat
     */
    public void setDefaultNumberFormat(NumberFormat format)
    {
        valueRenderer.defaultNumberFormat = format;
    }

    public boolean isVariableRowHeight()
    {
        return valueRenderer.variableRowHeight;
    }

    public void setVariableRowHeight(boolean variableRowHeight)
    {
        valueRenderer.variableRowHeight = variableRowHeight;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * <code>TableModel</code> that provides the data displayed by this
     * <code>TabularPropertyInspector</code>.
     */
    protected BeanTableModelAdapter tableModel;

    /** Instance of <code>JTable</code> used by this <code>TabularPropertyInspector</code>. */
    protected JTable table;

    /**
     *  Returns instance of <code>JTable</code> used by this <code>TabularPropertyInspector</code>.
     *  @return Instance of <code>JTable</code> used by this <code>TabularPropertyInspector</code>.
     */
    public JTable getTable()
    {
        return table;
    }

    /**
     * Main "entry point" for TabularPropertyInspector.
     * It is used to supply list of beans to be shown in the tabular form
     * along with model describeing what columns must be shown.
     * @param rowModel the model to retrieve the list of beans to be dispayed
     * @param columnModel the model describing columns to be shown
     */
    public void explore( RowModel rowModel, ColumnModel columnModel )
    {
        unregisterListeners();
        if ( rowModel != null )
        {
            if ( getSortEnabled() )
            {
                tableModel = createSortedTableModel( rowModel, columnModel );
            }
            else
            {
                tableModel = new BeanTableModelAdapter( rowModel, columnModel );
            }
            //addListSelectionListener( tableModel );
            tableModel.setRowHeader( getRowHeader() );
            table.setModel( tableModel );

            if ( getRowHeader() )
            {
                TableColumn col = table.getColumnModel().getColumn( 0 );
                col.setResizable( false );
                col.setMinWidth( 30 );
                col.setPreferredWidth( 30 );
                col.setMaxWidth( 30 );
            }

            //initHeaders();
            if ( table.getColumnCount() > 8 )
            {
                table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
            }
            else
            {
                table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
            }

            if (tableModel instanceof SortedTableModel)
            {
                ((SortedTableModel)tableModel).sort();
            }
        }
        else
        {
            table = null;
            tableModel = null;
            removeAll();
            repaint();
            initTable();
        }
        registerListeners();
    }

    protected void registerListeners()
    {
        unregisterListeners();

        if ( table != null )
        {
            initHeaders();
            table.addMouseListener(getTablePopupMouseListener());
            table.getTableHeader().addMouseListener(headerListener);
            if (valueRenderer != null)
            {
                valueRenderer.registerListeners();
            }
            if (scrollPane != null)
            {
                scrollPane.setViewportView(table);
            }
        }
        if ( tableModel != null )
        {
            if ( tableModel instanceof ListSelectionListener )
            {
                addListSelectionListener( ( ListSelectionListener )tableModel );
            }
            tableModel.addTableModelListener(table);
            tableModel.registerListeners();
        }
        if (scrollPane != null)
        {
            scrollPane.setViewportView(table);
        }
    }

    protected void unregisterListeners()
    {
        if ( table != null )
        {
            resetHeaderRenderers();
            table.removeMouseListener(getTablePopupMouseListener());
            table.getTableHeader().removeMouseListener(headerListener);
            if (valueRenderer != null)
            {
                valueRenderer.unregisterListeners();
            }
        }
        if ( tableModel != null )
        {
            if ( tableModel instanceof ListSelectionListener )
            {
                removeListSelectionListener( ( ListSelectionListener )tableModel );
            }
            tableModel.removeTableModelListener(table);
            tableModel.unregisterListeners();
        }
        if (scrollPane != null)
        {
            scrollPane.setViewportView(table);
        }
    }

    /**
     * Main "entry point" for TabularPropertyInspector.
     * It is used to supply list of beans to be shown in the tabular form
     * along with template bean used to retrieve list of columns to be shown.
     * @param rowModel the model to retrieve the list of beans to be dispayed
     * @param templateBean the properties of the <i>templateBean</i> will be used to calculate the list of columns to show
     * @param showMode Should be combination of SHOW_USUAL, SHOW_EXPERT, SHOW_HIDDEN, SHOW_PREFERRED
     */
    public void explore( RowModel rowModel, Object templateBean, int showMode )
    {
        propertyShowMode = showMode;
        explore( rowModel,
            new ColumnModel( templateBean, propertyShowMode ) );
    }

    /**
     * Main "entry point" for TabularPropertyInspector.
     * It supplies the list of beans to be shown in the form of array.
     * Columns to be shown are calculated using properties of the first bean in the array
     */
    public void explore( Object[] beans )
    {
        if ( beans == null || beans.length == 0 )
        {
            explore( null, null );
        }
        else
        {
            DefaultRowModel defaultRowModel = new DefaultRowModel();
            for ( int i = 0; i < beans.length; i++ )
            {
                defaultRowModel.add( beans[ i ] );
            }
            explore( defaultRowModel,
                new ColumnModel( beans[ 0 ] ) );
        }
    }

    /**
     * Main "entry point" for TabularPropertyInspector.
     * It supplies the list of beans to be shown in the form of iterator.
     * This method is intended for huge lists of beans like results of SQL queries
     * Columns to be shown are calculated using properties of the first bean
     * return by iterator
     */
    public void explore( Iterator beans )
    {
        if ( beans == null || !beans.hasNext() )
        {
            explore( null, null );
        }
        else
        {
            DefaultRowModel defaultRowModel = new DefaultRowModel();
            while ( beans.hasNext() )
            {
                defaultRowModel.add( beans.next() );
            }
            explore( defaultRowModel,
                new ColumnModel( defaultRowModel.getBean( 0 ) ) );
        }
    }

    /**
     * This method clears a PropertyInspector.
     */
    @Override
    public void clear()
    {
        explore( null , null );
    }

    /**
     * Notifies this component that it no longer has a parent component.
     * This method cleanups a TabularPropertyInspector.
     */
    @Override
    public void removeNotify()
    {
        unregisterListeners();
        super.removeNotify();
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        registerListeners();
    }

    /**
     * Returns a column model.
     */
    public ColumnModel getColumnModel()
    {
        if ( tableModel != null )
        {
            return tableModel.getColumnModel();
        }
        return null;
    }

    /** Used internally */
    @Override
    public Property getProperty( Point pt )
    {
        int row = table.rowAtPoint( pt );
        if ( row < 0 )
        {
            return null;
        }
        int column = table.columnAtPoint( pt );
        if ( column < 0 )
        {
            return null;
        }
        Object value = table.getValueAt( row, column );
        if ( value instanceof Property )
        {
            return ( Property )value;
        }
        return null;
    }

    /** Used internally */
    @Override
    public Rectangle getCellRect( Point pt )
    {
        int row = table.rowAtPoint( pt );
        if ( row < 0 )
        {
            return null;
        }
        int column = table.columnAtPoint( pt );
        if ( column < 0 )
        {
            return null;
        }
        return table.getCellRect( row, column, true );
    }

    public Object getModelOfSelectedRow()
    {
        return getModelForRow( table.getSelectedRow() );
    }

    public Object getModelForRow( int row )
    {
        return row < 0 ? null : tableModel.getModelForRow( row );
    }
    ////////////////////////////////////////
    // Listener issues
    //

    /*
     * List of listeners.
     */

    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Add a ListSelectionListener to the listener list.
     * @param l      the ListSelectionListener to be added
     */
    public void addListSelectionListener( ListSelectionListener l )
    {
        listenerList.add( ListSelectionListener.class, l );
    }

    /**
     * Remove a ListSelectionListener from the listener list.
     * @param l      the ListSelectionListener to be removed
     */
    public void removeListSelectionListener( ListSelectionListener l )
    {
        listenerList.remove( ListSelectionListener.class, l );
    }

    @Override
    public void addMouseListener( MouseListener listener )
    {
        if ( table != null )
        {
            table.addMouseListener( listener );
        }
    }

    @Override
    public void removeMouseListener( MouseListener listener )
    {
        if ( table != null )
        {
            table.removeMouseListener( listener );
        }
    }

    @Override
    public void addMouseMotionListener( MouseMotionListener listener )
    {
        if ( table != null )
        {
            table.addMouseMotionListener( listener );
        }
    }

    @Override
    public void removeMouseMotionListener( MouseMotionListener listener )
    {
        if ( table != null )
        {
            table.removeMouseMotionListener( listener );
        }
    }

    /** @param e    the ListSelectionEvent object. */
    protected void fireValueChanged( ListSelectionEvent e )
    {
        Object[] listeners = listenerList.getListenerList();

        for ( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if ( listeners[ i ] == ListSelectionListener.class )
            {
                ( ( ListSelectionListener )listeners[ i + 1 ] ).valueChanged( e );
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Private

    private final ValueRenderer valueRenderer = new ValueRenderer(this);

    protected void initTable()
    {
        table = new JTable()
        {
            /** Relay the the event to PropertyInsepctor's listeners */
            @Override
            public void valueChanged( ListSelectionEvent e )
            {
                super.valueChanged( e );
                if ( TabularPropertyInspector.this != null )
                {
                    TabularPropertyInspector.this.fireValueChanged( e );
                }
            }

            @Override
            public void tableChanged( TableModelEvent e )
            {
                super.tableChanged( e );
                if ( TabularPropertyInspector.this != null && table != null )
                {
                    initHeaders();
                    if ( tableModel != null )
                    {
                        if ( tableModel.getRowHeader() != getRowHeader() )
                        {
                            setRowHeader( tableModel.getRowHeader() );
                        }
                        if ( tableModel.getColumnModel().isSortEnabled() != getSortEnabled() )
                        {
                            setSortEnabled( tableModel.getColumnModel().isSortEnabled() );
                        }
                    }
                    if ( getRowHeader() && table.getColumnModel() != null && table.getColumnModel().getColumnCount() > 0)
                    {
                        TableColumn col = table.getColumnModel().getColumn( 0 );
                        col.setResizable( false );
                        col.setMinWidth( 30 );
                        col.setPreferredWidth( 30 );
                        col.setMaxWidth( 30 );
                    }
                }
            }

            @Override
            public void updateUI()
            {
                try{ setRowHeight( getPreferredRowHeight() ); }
                catch(Exception e){}

                super.updateUI();
            }

        };
        //valueRenderer = new ValueRenderer(this);
        table.setDefaultRenderer( Object.class, valueRenderer );
        table.setDefaultEditor( Object.class, new ValueEditor(this) );

        table.setRowHeight( getPreferredRowHeight() );
        table.getColumnModel().setColumnMargin(2);

        table.addMouseListener(getTablePopupMouseListener());

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed( false );
        //header.addMouseListener( headerListener = new HeaderListener( header ) );
        headerListener = new HeaderListener( header );


        scrollPane = new JScrollPane( table );
        setLayout( new BorderLayout() );
        add( scrollPane, BorderLayout.CENTER );
    }

    protected int getPreferredRowHeight()
    {
        javax.swing.JTextField tf = new javax.swing.JTextField("Yy");
        return tf.getMinimumSize().height;
    }



    private HeaderListener headerListener;

    protected void initHeaders()
    {
        resetHeaderRenderers();
        TableColumnModel model = table.getColumnModel();
        if (getColumnModel() != null)
        {
            Column[] columns = getColumnModel().getColumns();
            int cnt = ( getRowHeader() ? 1 : 0 );
            for ( int i = 0; i < columns.length; i++ )
            {
                if ( columns[ i ].getEnabled() )
                {
                    if (cnt < model.getColumnCount())
                    {
                        TableCellRenderer renderer = new LocalButtonRenderer( columns[ i ] );
                        model.getColumn( cnt++ ).setHeaderRenderer( renderer );
                    }
                }
            }
        }
    }

    private void resetHeaderRenderers()
    {
        TableColumnModel model = table.getColumnModel();
        if (getColumnModel() != null)
        {
            Column[] columns = getColumnModel().getColumns();
            int cnt = ( getRowHeader() ? 1 : 0 );
            for ( int i = 0; i < columns.length; i++ )
            {
                if ( columns[ i ].getEnabled() )
                {
                    if (cnt < model.getColumnCount())
                    {
                        model.getColumn( cnt++ ).setHeaderRenderer( null );
                    }
                }
            }
        }
    }

    private MouseListener tablePopupMouseListener;

    private MouseListener getTablePopupMouseListener()
    {
        if (tablePopupMouseListener == null)
        {
            tablePopupMouseListener = new MouseAdapter()
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
            };
        }
        return tablePopupMouseListener;
    }

    //////////////////////////////////////////////
    //
    //
    class LocalButtonRenderer extends SortButtonRenderer
    {
        private static final long serialVersionUID = 1L;

        public LocalButtonRenderer( Column fieldOptions )
        {
            super( fieldOptions );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column )
            {
                if ( getRowHeader() )
                {
                    if ( column == 0 )
                    {
                        return this;
                    }
                    else
                    {
                        column--;
                    }
                }
                return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
        }
    }

    class HeaderListener extends MouseAdapter
    {
        JTableHeader header;

        HeaderListener( JTableHeader header )
        {
            this.header = header;
        }

        @Override
        public void mouseClicked( MouseEvent e )
        {
            int col = header.columnAtPoint( e.getPoint() ) + ( getRowHeader() ? -1 : 0 );

            if ( col < 0 || e.getClickCount() != 1 || !getSortEnabled() )
            {
                return;
            }

            Column[] columns = getColumnModel().getColumns();
            // find internal column index (among all field options, including invisible)
            for ( int i = 0, cnt = 0; i < columns.length; i++ )
            {
                if ( columns[ i ].getEnabled() )
                {
                    if ( cnt == col )
                    {
                        col = i;
                        break;
                    }
                    cnt++;
                }
            }

            if(columns[col] instanceof ColumnWithSort)
            {
                // reverse sorting direction
                ColumnWithSort fieldOption = (ColumnWithSort)columns[col];
                boolean isAscent = !( fieldOption.getSorting() == ColumnWithSort.SORTING_ASCENT );

                Column[] cols = tableModel.getColumnModel().getColumns();
                for( int i=0; i<cols.length; i++ )
                {
                    ( ( ColumnWithSort )cols[ i ] ).setSorting( ColumnWithSort.SORTING_NONE );
                }
                fieldOption.setSorting(isAscent ? ColumnWithSort.SORTING_ASCENT : ColumnWithSort.SORTING_DESCENT);

                //( ( SortedBeanTableModelAdapter )model ).sort();
            }
            if ( header.getTable().isEditing() )
            {
                header.getTable().getCellEditor().stopCellEditing();
            }
            tableModel.fireTableChanged( new TableModelEvent( tableModel, TableModelEvent.HEADER_ROW ) );

        }
    }

    ////////////////////////////////////////
    // Transactable interface implementation
    //

    @Override
    public void addTransactionListener(TransactionListener listener)
    {
        listenerList.add(TransactionListener.class, listener);
    }

    @Override
    public void removeTransactionListener(TransactionListener listener)
    {
        listenerList.remove(TransactionListener.class, listener);
    }

    protected void fireStartTransaction(TransactionEvent evt)
    {
        Object[] listeners = listenerList.getListenerList();
        for ( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if ( listeners[ i ] == TransactionListener.class )
                ( (TransactionListener) listeners[ i + 1 ] ).startTransaction(evt);
        }
    }

    protected void fireAddEdit(UndoableEdit ue)
    {
        Object[] listeners = listenerList.getListenerList();
        for ( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if ( listeners[ i ] == TransactionListener.class )
                ( (TransactionListener) listeners[ i + 1 ] ).addEdit(ue);
        }
    }

    protected void fireCompleteTransaction()
    {
        Object[] listeners = listenerList.getListenerList();
        for ( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if ( listeners[ i ] == TransactionListener.class )
                ( (TransactionListener) listeners[ i + 1 ] ).completeTransaction();
        }
        table.repaint();
    }
}
