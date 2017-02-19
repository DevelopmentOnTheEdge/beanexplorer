package com.developmentontheedge.beans.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;

import com.developmentontheedge.beans.BeanEventListener;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.EventConstants;
import com.developmentontheedge.beans.PropertyDescriptorEx;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.CompositeProperty;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.model.SimpleProperty;
import com.developmentontheedge.beans.swing.treetable.DefaultTreeTableModel;
import com.developmentontheedge.beans.swing.treetable.JTreeTable;
import com.developmentontheedge.beans.undo.Transactable;
import com.developmentontheedge.beans.undo.TransactionEvent;
import com.developmentontheedge.beans.undo.TransactionListener;
import com.developmentontheedge.beans.util.SmartText;

/**
 * This is simplest PropertyInspector. Though it can be uses as is,
 * it is just interface element without application useful features.
 * Those features must be added by derivative classes, since
 * primary intension behind PropertyInspector is to keep it small.
 */
public class PropertyInspector extends JPanel implements PropertyChangeListener,
AbstractPropertyInspector, Transactable
{
    private static final long serialVersionUID = 1L;

    /**
     * Flag, that forces to show only usual properties.
     *
     * @see   #setPropertyShowMode
     */
    public static final int SHOW_USUAL     = Property.SHOW_USUAL;
    /**
     * Flag, that forces to show only "expert" and usual properties.
     *
     * @see   #setPropertyShowMode
     */
    public static final int SHOW_EXPERT    = Property.SHOW_EXPERT;

    /**
     * Flag, that forces to show only "hidden" and usual properties.
     *
     * @see   #setPropertyShowMode
     */
    public static final int SHOW_HIDDEN    = Property.SHOW_HIDDEN;

    /**
     * Flag, that forces to show only "preferred" .
     *
     * @see   #setPropertyShowMode
     */
    public static final int SHOW_PREFERRED = Property.SHOW_PREFERRED;

    /**
     * Split pane, top pane contains <code>treePane</code>,
     * bottom pane contains <code>toolTipPane</code>.
     */
    protected JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /**
     * JTreeTable used to display properties tree.
     */
    protected JTreeTable treeTable = null;

    /**
     * Scroll pane containing <CODE>treeTable</CODE>.
     */
    protected JScrollPane treePane = null;

    /** Indicates what properties (usual, expert, hidden,preferred) should be displayed. */
    private int propertyShowMode = Property.SHOW_USUAL | Property.SHOW_PREFERRED;

    /** Creates instance of PropertyInspector. */
    public PropertyInspector()
    {
        super( new BorderLayout() );

        add(splitPane, BorderLayout.CENTER);        
        splitPane.setDividerSize(5);

        setupPopupMenu( popup = new JPopupMenu() );

        //inspectorActionManager = new InspectorActionManager( mobile );

        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent evt)
            {
                updateSplitPaneDivider();
            }
        });

    }

    ////////////////////////////////////////
    // Transactable interface implementation
    //

    protected EventListenerList listenerList = new EventListenerList();

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
    }

    ////////////////////////////////////////
    // AbstractPropertyInspector interface implementation
    //

    @Override
    public Property getProperty( Point pt )
    {
        int row = treeTable.rowAtPoint( pt );
        if ( row < 0 )
        {
            return null;
        }

        int column = treeTable.columnAtPoint( pt );
        if ( column < 0 )
        {
            return null;
        }

        Object value = treeTable.getValueAt( row, column );
        if ( value instanceof Property )
        {
            return ( Property )value;
        }

        return null;
    }

    @Override
    public Rectangle getCellRect( Point pt )
    {
        int row = treeTable.rowAtPoint( pt );
        if ( row < 0 )
        {
            return null;
        }

        int column = treeTable.columnAtPoint( pt );
        if ( column < 0 )
        {
            return null;
        }

        return treeTable.getCellRect( row, column, true );
    }

    @Override
    public void addMouseListener( MouseListener listener )
    {
        if ( treeTable != null )
        {
            treeTable.addMouseListener( listener );
        }
    }

    @Override
    public void removeMouseListener( MouseListener listener )
    {
        if ( treeTable != null )
        {
            treeTable.removeMouseListener( listener );
        }
    }

    @Override
    public void addMouseMotionListener( MouseMotionListener listener )
    {
        if ( treeTable != null )
        {
            treeTable.addMouseMotionListener( listener );
        }
    }

    @Override
    public void removeMouseMotionListener( MouseMotionListener listener )
    {
        if ( treeTable != null )
        {
            treeTable.removeMouseMotionListener( listener );
        }
    }

    ////////////////////////////////////////
    // bean explore related methods
    //

    /** The component that is currently explored with PropertyInspector. */
    private Object bean;

    /** ComponentModel that is currently assotiated with PropertyInspector. */
    private ComponentModel componentModel = null;

    /**
     * This method is a main entry point for setting a component
     * to be explored in PropertyInspector.
     */
    public void explore( Object bean )
    {
        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_integrity_dialog$
        //$check_regtrial_dialog$

        if ( bean == null )
        {
            if ( treeTable != null )
            {
                remove( treePane );
                repaint();
            }
            return;
        }

        if ( bean.getClass().isArray() )
        {
            bean = new ArrayBeanWrapper( bean );
        }

        this.bean = bean;
        ComponentModel mdl = ComponentFactory.getModel( bean );
        setComponentModel( mdl );
    }

    /**
     * This method is a main entry point for setting a component
     * to be explored in PropertyInspector (only properties which names
     * passed as array of strings).
     */
    public void explore( Object bean, String[] filter )
    {
        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_integrity_dialog$
        //$check_regtrial_dialog$
        if ( bean == null )
        {
            if ( treeTable != null )
            {
                remove( treePane );
                repaint();
            }
            return;
        }

        this.bean = bean;
        ComponentModel mdl = ComponentFactory.getModel( bean );
        ComponentModel fmdl = ComponentFactory.filterComponentModel( mdl, filter );
        setComponentModel( fmdl );

        this.treeTable.getTree().setRootVisible( false );
    }

    /**
     * This method clears a PropertyInspector.
     */
    @Override
    public void clear()
    {
        explore(null);
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        registerListeners();
    }

    /**
     * Notifies this component that it no longer has a parent component.
     * This method cleanups a PropertyInspector.
     */
    @Override
    public void removeNotify()
    {
        unregisterListeners();
        super.removeNotify();
    }


    /**
     *  Gets current bean.
     *
     * @return the current bean.
     */
    public Object getBean()
    {
        return bean;
    }

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
     *  Sets current component model.
     *
     * @param componentModel   the current component model
     * @deprecated
     * @pending to be declared protected
     */
    public void setComponentModel( ComponentModel componentModel )
    {
        unregisterListeners();

        if ( componentModel == null )
        {
            return;
        }

        this.componentModel = componentModel;

        treeTable = new JTreeTable(
            new PropertyInspectorTreeTableModel( componentModel, propertyShowMode ) )
            {
                @Override
                public String getToolTipText( MouseEvent event )
                {
                    String tip = showToolTip ? getToolTip( event ) : null;
                    return tip;
                }
        };
        treeTable.getColumnModel().getColumn(1).setResizable( false );

        treeTable.getTableHeader().setReorderingAllowed( false );
        treeTable.getTree().setCellRenderer( new NameRenderer() );


        treeTable.setDefaultRenderer( Object.class, valueRenderer);
        treeTable.setDefaultEditor( Object.class,  new ValueEditor(this) );
        treeTable.setIntercellSpacing( new Dimension( 2, 2 ) );
        treeTable.setShowGrid( showGrid );
        treeTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        treeTable.setRowSelectionAllowed( false );
        treeTable.setColumnSelectionAllowed( false );
        treeTable.setCellSelectionEnabled( true );

treeTable.setFocusTraversalPolicy(new java.awt.DefaultFocusTraversalPolicy());

        treeListener = new PropertyTreeListenerAdapter();

        treePane = new JScrollPane(treeTable); 

        splitPane.setTopComponent(treePane);
        validate();

        selectCell( 0, 0 );

        if ( componentModel.isExpanded() )
        {
            restoreCollapsingState();
        }
        else
        {
            treeTable.getTree().collapseRow( 0 );
        }

        registerListeners();
        updateToolTipPane(componentModel);
    }

    private final ValueRenderer valueRenderer = new ValueRenderer( this );

    private void registerListeners()
    {
        unregisterListeners();

        if ( componentModel != null )
        {
            componentModel.addPropertyChangeListener( this );
        }

        if ( treeTable != null )
        {
            treeTable.addMouseListener(getPopupMouseListener());
            if (treeListener != null)
            {
                treeTable.getTree().addTreeSelectionListener( treeListener );
                treeTable.getTree().addTreeExpansionListener( treeListener );
            }
            if (valueRenderer != null)
            {
                valueRenderer.registerListeners();
            }
            //treePane.setViewportView(treeTable);
//            JViewport viewport = treePane.getViewport();
//            if (viewport != null)
//            {
//                viewport.remove(treeTable);
//            }

            //validate();
        }
    }


    private void unregisterListeners()
    {
        if ( componentModel != null )
        {
            componentModel.removePropertyChangeListener( this );
        }

        if ( treeTable != null )
        {
            treeTable.removeMouseListener(getPopupMouseListener());
            if (treeListener != null)
            {
                treeTable.getTree().removeTreeSelectionListener( treeListener );
                treeTable.getTree().removeTreeExpansionListener( treeListener );
            }
            if (valueRenderer != null)
            {
                valueRenderer.unregisterListeners();
            }
//            JViewport viewport = treePane.getViewport();
//            if (viewport != null)
//            {
//                viewport.remove(treeTable);
//            }
            //treePane.setViewportView(null);
        }
    }

    private PropertyTreeListenerAdapter treeListener;

    private MouseListener popupMouseListener;

    private MouseListener getPopupMouseListener()
    {
        if (popupMouseListener == null)
        {
            popupMouseListener = new MouseAdapter()
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
        return popupMouseListener;
    }


    ////////////////////////////////////////
    // Tree related issues
    //

    /**
     * Select pointed cell in table.
     * <p>We have to wait then table updated own and only then select our cell.
     */
    protected void selectCell( int row, int column )
    {
        validateCell( row, column );
        if ( row < 0 || column < 0 )
        {
            return;
        }

        final int r = row;
        final int c = column;
        SwingUtilities.invokeLater(
            new Runnable()
            {
                @Override
                public void run()
                {
                    treeTable.changeSelection( r, c, false, false );
                }
            } );
    }

    //----- public collapse/expand related methods -------------------/

    /**
     * Returns row number for the specified property name.
     * <p><b>Note</b> property name should be relative the component model.
     * @param name complete property name.
     */
    public int getRow( String name )
    {
        Property p = null;
        if ( componentModel != null )
        {
            p = componentModel.findProperty( name );
        }

        if ( p != null )
        {
            return ( getRow( p ) );
        }

        return -1;
    }

    /**
     * Collapse node for the specified property.
     *
     * @param name   name of the property which node should be collapsed
     */
    public void collapseProperty( String name )
    {
        collapseRow( getRow( name ) );
    }

    /**
     * Expand node for the specified property.
     * If <CODE>recursively</CODE> is true all node hierarchy will be expanded.
     * Else only node for specified property will be expanded.
     *
     * @param name   name of the property which node should be expanded
     * @param recursively should all node hierarchy be expanded
     */
    public void expandProperty( String name, boolean recursively )
    {
        expandRow( getRow( name ), recursively );
    }

    /**
     * Expand nodes in the tree that displays bean properties for specified level.
     * If <CODE>recursive</CODE> is true all tree will be expanded. Else only root nodes will be expanded.
     *
     * @param level  level for which tree of properties should be expanded
     */
    public void expand(int level)
    {
        if(isRootVisible())
            expandRow(0, level);
        else
        {
            for(int i=0; i < componentModel.getPropertyCount(); i++)
            {
                Property child = componentModel.getPropertyAt(i);
                expandRow(getRow(child), level);
            }
        }
    }

    /**
     * Expand nodes in the tree that displays bean properties.
     * If <CODE>recursive</CODE> is true all tree will be expanded. Else only root nodes will be expanded.
     *
     * @param recursive should all tree hierarchy be expanded
     */
    public void expandAll( boolean recursive )
    {
        if(treeTable == null)
        {
            return;
        }
        JTree tree = treeTable.getTree();
        for ( int i = 0; i < tree.getRowCount(); i++ )
            expandRow(i, recursive);
    }

    /**
     * Collapse all nodes in the tree that displays bean properties. After that only
     * root nodes will be visible.
     */
    public void collapseAll()
    {
        if(treeTable == null)
        {
            return;
        }
        JTree tree = treeTable.getTree();
        for ( int i = 0; i < tree.getRowCount(); i++ )
            collapseRow(i);
    }

    private void restoreCollapsingState()
    {
        JTree tree = treeTable.getTree();

        for(int i=0; i<tree.getRowCount(); i++)
            restoreCollapsingState(tree, i);
    }

    private void restoreCollapsingState( JTree tree, int row )
    {
        if ( row >= 0 )
        {
            TreePath path = tree.getPathForRow( row );
            tree.expandRow( row );
            Object o = path.getLastPathComponent();
            if ( row == 0 ) // this is root model
            {
                for ( int i = 0; i < componentModel.getPropertyCount(); i++ )
                {
                    Property pChild = componentModel.getPropertyAt( i );
                    if ( pChild.isExpanded() )
                    {
                        restoreCollapsingState( tree, getRow( pChild ) );
                    }
                }
            }
            else if ( o instanceof Property )
            {
                Property p = ( Property )o;
                for ( int i = 0; i < p.getPropertyCount(); i++ )
                {
                    Property pChild = p.getPropertyAt( i );
                    if ( pChild.isExpanded() )
                    {
                        restoreCollapsingState( tree, getRow( pChild ) );
                    }
                }
            }
        }
    }

    //----- collapse/expand utility functions ------------------------/

    /**
     * Collapses specified row.
     *
     * @param row collapsed row
     */
    protected void collapseRow( int row )
    {
        if(treeTable == null)
        {
            return;
        }
        if ( row >= 0 )
        {
            treeTable.getTree().collapseRow( row );
        }
    }

    /**
     * Expands specified row in table of Property Inspector
     *
     * @param row  the specified row
     * @param recursive true, if expanding will be processed recursively
     */
    protected void expandRow( int row, boolean recursively )
    {
        if(treeTable == null)
        {
            return;
        }
        if ( row >= 0 )
        {
            JTree tree = treeTable.getTree();
            tree.expandRow( row );
            if ( recursively )
            {
                TreePath path = tree.getPathForRow( row );
                Object o = path.getLastPathComponent();
                if ( o instanceof Property )
                {
                    Property p = ( Property )o;
                    int n = p.getPropertyCount();
                    for ( int i = 0; i < n; i++ )
                    {
                        Property pChild = p.getPropertyAt( i );
                        expandRow( getRow( pChild ), true );
                    }
                }
            }
        }
    }

    /**
     * Expands specified row on specified depth
     *
     * @param row   the expanded row
     * @param level the depth of expanding
     */
    protected void expandRow(int row, int level)
    {
        if(treeTable == null)
        {
            return;
        }
        if(row >= 0 && level > 0)
        {
            level--;
            JTree tree = treeTable.getTree();
            tree.expandRow(row);

            TreePath path = tree.getPathForRow(row);
            Property p = (Property)path.getLastPathComponent();
            int n = p.getPropertyCount();
            for(int i = 0; i < n; i++)
            {
                Property pChild = p.getPropertyAt(i);
                expandRow(getRow(pChild), level);
            }
        }
    }

    /**
     * Returns row number for the specified property.
     *
     * @pending may be the algorithm should be optimised
     */
    protected int getRow( Property property )
    {
        if(treeTable == null)
        {
            return -1;
        }
        JTree tree = treeTable.getTree();
        int n = tree.getRowCount();
        String name = property.getCompleteName();

        TreePath path;
        Property p;
        for(int i=1; i<n; i++)
        {
            path = tree.getPathForRow(i);
            p = (Property)path.getLastPathComponent();
            if( p.getCompleteName().startsWith(name) )
                return i;
        }

        return -1;
    }

    /** Returns start/end positions of rows for the specified (may be composite) property. */
    protected Point getRowRange( Property property )
    {
        if(treeTable == null)
        {
            return null;
        }
        Point result = null;
        JTree tree = treeTable.getTree();
        String name = property.getCompleteName();

        TreePath path;
        Property p;
        boolean found = false;
        for ( int i = 1; i < tree.getRowCount(); i++ )
        {
            path = tree.getPathForRow( i );
            p = ( Property )path.getLastPathComponent();
            if ( p.getCompleteName().startsWith( name ) )
            {
                if ( found )
                {
                    result.y = i;
                }
                else
                {
                    found = true;
                    result = new Point( i, i );
                }
            }
            else if ( found )
            {
                return result;
            }
        }

        return result;
    }

    ////////////////////////////////////////
    // Tree selection issues
    //


    private void handleTreeSelectionEvent( TreeSelectionEvent e )
    {
        int row = treeTable.getSelectedRow();
        int column = treeTable.getSelectedColumn();
        validateCell( row, column );


            TreePath path = e.getPath();
            Object node = path.getLastPathComponent();
            if( node instanceof Property )
                updateToolTipPane( (Property)node );
    }

    /**
     * Validate PropertyInspector settings for the specified selected cell.
     * <p>This method is overrided by <code>PropertyEditorEx</code>.
     */
    protected void validateCell( int row, int column )
    {
    }


    private class PropertyTreeListenerAdapter implements TreeSelectionListener,
    TreeExpansionListener
    {
        @Override
        public void valueChanged( TreeSelectionEvent e )
        {
            handleTreeSelectionEvent( e );
        }

        @Override
        public void treeExpanded( TreeExpansionEvent e )
        {
            TreePath path = e.getPath();
            Object node = path.getLastPathComponent();
            if ( !( node instanceof Property ) )
            {
                return;
            }
            Property property = ( Property )node;
            property.setExpanded( true );
            restoreCollapsingState( ( JTree )e.getSource(),
            node instanceof ComponentModel ? 0 : getRow( property ) );
        }

        @Override
        public void treeCollapsed( TreeExpansionEvent e )
        {
            TreePath path = e.getPath();
            Object node = path.getLastPathComponent();
            if ( node instanceof Property )
            {
                ( ( Property )node ).setExpanded( false );
            }
            else
            {
                logNonCriticalErrorMessage( "Unknown node type: " + node.getClass(), null );
            }
        }
    }


    ////////////////////////////////////////
    // Property change issues
    //


    /**
     * This method gets called when a bound property is changed.
     * @param evt a PropertyChangeEvent object describing the event source
     *      and the property that has changed.
     */
    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        String propertyName = evt.getPropertyName();
        boolean recreate = propertyName.equals("*");

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
        if( p instanceof CompositeProperty && !(p.isHideChildren()) )
            recreate = true;

        if ( p == null && !recreate)
        {
            return;
        }

        if (  (p instanceof SimpleProperty ) || ! recreate )
        {
            Point rows = getRowRange( p );
            if ( rows != null )
            {
                AbstractTableModel tm = ( AbstractTableModel )treeTable.getModel();
                tm.fireTableChanged( new TableModelEvent( tm, rows.x, rows.y ) );
            }
        }
        else
        {
            ComponentFactory.recreateChildProperties( p );
            if(treeTable != null)
                treeTable.updateUI();
        }

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
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener )
    {
        if( componentModel == null )
            return;

        if( propertyName==null )
        {
            componentModel.addPropertyChangeListener( listener );
        }
        else
        {
            Property prop = componentModel.findProperty( propertyName );
            if ( prop != null )
            {
                prop.addPropertyChangeListener( listener );
            }
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property
     * referenced via complete name
     */
    @Override
    public void removePropertyChangeListener( String propertyName,PropertyChangeListener listener )
    {
        if( componentModel == null )
            return;

        if( propertyName==null )
        {
            componentModel.removePropertyChangeListener( listener );
        }
        else
        {
            Property prop = componentModel.findProperty( propertyName );
            if ( prop != null )
            {
                prop.removePropertyChangeListener( listener );
            }
        }
    }

    /**
     * Adds the specified bean event listener to receive bean events from Property Inspector.
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

    ////////////////////////////////////////
    // Popup menus
    //

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


    /**
     * Initializes Property Inspector popup menu
     *
     * @param popup  the menu
     */
    protected void setupPopupMenu( JPopupMenu popup )
    {
        JMenuItem showGridMenuItem = new JMenuItem( "Toggle Grid" );
        showGridMenuItem.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    setShowGrid( !isShowGrid() );
                }
            } );
        popup.add( showGridMenuItem );
    }

    ////////////////////////////////////////
    // Properties
    //

    /**
     * Returns current show mode.
     *
     * @see #SHOW_USUAL
     * @see #SHOW_EXPERT
     * @see #SHOW_HIDDEN
     * @see #SHOW_PREFERRED
     *
     */
    public int getPropertyShowMode()
    {
        return propertyShowMode;
    }

    /**
     * Sets show mode.
     *
     * @param propertyShowMode the new show mode
     * @see #SHOW_USUAL
     * @see #SHOW_EXPERT
     * @see #SHOW_HIDDEN
     * @see #SHOW_PREFERRED
     */
    public void setPropertyShowMode( int propertyShowMode )
    {
        this.propertyShowMode = propertyShowMode;
    }

    /** Indicates whether is needed to show grid. */
    private boolean showGrid = true;


    /**
     * Returns show grid flag. Show grid flag indicates whether is needed to show grid.
     *
     * @return the value of show grid flag
     */
    public boolean isShowGrid()
    {
        return showGrid;
    }

    /**
     * Sets new value of show grid flag.
     *
     * @param showGrid the new value of show grid flag
     * @see #isShowGrid
     */
    public void setShowGrid( boolean showGrid )
    {
        this.showGrid = showGrid;
        if ( treeTable != null )
        {
            treeTable.setShowGrid( showGrid );
        }
    }

    /** Indicates whether treetable root node should be shown. */
    public boolean isRootVisible()
    {
        return treeTable.getTree().isRootVisible();
    }

    /**
     * Determines whether or not the root node of Property Inspector
     *
     * @param visible  true if the root node of the Property Inspector is to be displayed
     */
    public void setRootVisible( boolean visible )
    {
        treeTable.getTree().setRootVisible( visible );
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


    ////////////////////////////////////////
    // Tooltip issues
    //

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
     * Gets tooltip width.
     *
     * @return the tooltip width in characters
     */
    public int getToolTipWidth()
    {
        return toolTipWidth;
    }


    /**
     * Sets tooltip width.
     *
     * @param toolTipWidth the specified tooltip width in characters
     */
    public void setToolTipWidth( int toolTipWidth )
    {
        this.toolTipWidth = toolTipWidth;
    }

    /**
     * Creates the tool tip string (property or component description)
     * that will be shown as tool tip or status bar message.
     * <p> The tool tip is generated only for first column.
     * @param event mouse event that stores mouse coordinates to localize
     * treetable cell where event was occur. modifications by adolg: added formatting and
     * removed <b>Component</b> etc.
     */
    protected String getToolTip( MouseEvent event )
    {
        SmartText descr = new SmartText();
        int column = treeTable.columnAtPoint( event.getPoint() );
        if ( column == 0 )
        {
            int row = treeTable.rowAtPoint( event.getPoint() );
            if ( row == 0 )
            {
                descr.setText( componentModel.getTypeDescription() );
            }
            else
            {
                Object value = treeTable.getValueAt( row, 1 );
                if ( value instanceof Property )
                {
                    Property p = ( Property )value;
                    descr.setText( p.getToolTip() );
                }
            }

            // perform line wrapping now
            descr.setText( "<html>" + descr.insertBreaks( "<br>", toolTipWidth, true ) + "</html>" );
            return descr.toString();
        }
        return null;
    }

    protected JEditorPane toolTipPane;

    private boolean showTooTipPane = false;
    public boolean isShowToolTipPane()
    {
        return showTooTipPane;
    }

    public void setShowToolTipPane(boolean showTooTipPane)
    {
        if( this.showTooTipPane == showTooTipPane )
            return;

        this.showTooTipPane = showTooTipPane;

        if( showTooTipPane )
        {
            toolTipPane = new JEditorPane("text/html", "");
            toolTipPane.setEditable(false);
            toolTipPane.setPreferredSize(new Dimension(100, toolTipPanePreferredHeight));

            splitPane.setBottomComponent(new JScrollPane(toolTipPane));
            splitPane.setDividerSize(5);
            updateSplitPaneDivider();
        }
        else
        {
            toolTipPane = null;
            splitPane.setBottomComponent(null);
            splitPane.setDividerSize(0);
        }
    }

    private int toolTipPanePreferredHeight = 75;
    public int getToolTipPanePreferredHeight()
    {
        return toolTipPanePreferredHeight;
    }
    public void setToolTipPanePreferredHeight(int value)
    {
        toolTipPanePreferredHeight = value;

        if( toolTipPane != null )
        {
            toolTipPane.setPreferredSize(new Dimension(100, toolTipPanePreferredHeight));
            updateSplitPaneDivider();
        }
    }

    protected void updateToolTipPane(Property property)
    {
        if( toolTipPane != null )
        {
            String text = "<html><b>" + property.getDisplayName() + "</b><br>" +
                                 property.getToolTip() + "</html>";
            if( !text.equals(toolTipPane.getText()) )
            {
                toolTipPane.setText(text); 
                toolTipPane.setCaretPosition(0);
            }
        }
    }

    protected void updateSplitPaneDivider()
    {
        if( toolTipPane != null )
        {
            int height = Math.max( (int)(getSize().height*0.6), 
                                   getSize().height - toolTipPanePreferredHeight);
            splitPane.setDividerLocation(height);
        }
    }

    ////////////////////////////////////////
    // utilites
    //

    void logNonCriticalErrorMessage( String msg, Exception e )
    {
        Logger.getLogger().error( msg, e );
    }

    ////////////////////////////////////////
    // PropertyInspectorTreeTableModel
    //

    static private class PropertyInspectorTreeTableModel extends DefaultTreeTableModel
    {
        public PropertyInspectorTreeTableModel( ComponentModel model, int showMode )
        {
            super(
                new PropertySetTreeModelAdapter( model, showMode ),
                new DefaultTableModel(
                new String[]
                {
                    "Property", "Value"
                }, 1 ) );

        }

        /**
         * Generally we can analyze 'i' value in this function
         * and return appropriate objects for multi-column PropertyIsnpector
         * For now function is called only for values of 'i' equal 1
         * with node being instance of Property
         */
        @Override
        public Object getValueAt( Object node, int i )
        {
            return node;
        }

        /** Do nothing because property.setValue */
        @Override
        public void setValueAt( Object aValue, Object node, int column )
        {
        }

        @Override
        public boolean isCellEditable( Object node, int column )
        {
            // without this we are unable to expand composite properties
            if ( column == 0 )
            {
                return true;
            }

            if ( node instanceof Property )
            {
                Property p = ( Property )node;
                if ( p.isReadOnly() )
                {
                    return false;
                }

                if ( p instanceof SimpleProperty )
                {
                    return true;
                }

                // @pending At the moment if one property from the list of composite editor is read only all property is read only. Is there better idea?
                Object cepl = p.getDescriptor().getValue( BeanInfoConstants.COMPOSITE_EDITOR_PROPERTY_LIST );
                if ( cepl != null )
                {
                    boolean bReadOnly = false;
                    StringTokenizer st = new StringTokenizer( (String)cepl, ";, " );
                    while ( st.hasMoreTokens() )
                    {
                        Property prop = p.findProperty( st.nextToken() );
                        if ( prop == null )
                        {
                            continue;
                        }
                        if ( ( bReadOnly = prop.isReadOnly() ) )
                        {
                            break;
                        }
                    }

                    return !bReadOnly;
                }

                return ( ( PropertyDescriptor )p.getDescriptor() ).getPropertyEditorClass() != null;
            }

            return false;
        }
    }

    public static class ArrayBeanWrapper
    {
        private final Object array;

        public ArrayBeanWrapper( Object array )
        {
            this.array = array;
        }

        public Object[] getArray()
        {
            return ( Object[] ) array;
        }
    }


}

