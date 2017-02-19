package com.developmentontheedge.beans.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.Property;

public class PropertyInspectorEx extends PropertyInspector
{
    private static final long serialVersionUID = 1L;
    protected JToolBar actionsPanel;
    public static final int TOOLBAR_BUTTON_SIZE = 20;

    public PropertyInspectorEx()
    {
        super();
        initActions();
        add(actionsPanel, BorderLayout.NORTH);
    }

    protected void initActions()
    {
        insertAction = new NewElementAction();
        removeItemAction = new RemoveAction();
        insertBeforeAction = new InsertAction(true);
        insertAfterAction = new InsertAction(false);
        itemUpAction = new MoveAction(true);
        itemDownAction = new MoveAction(false);

        insertAction.setEnabled(false);
        removeItemAction.setEnabled(false);
        insertBeforeAction.setEnabled(false);
        insertAfterAction.setEnabled(false);
        itemUpAction.setEnabled(false);
        itemDownAction.setEnabled(false);

        actionsPanel = new JToolBar();
        actionsPanel.setFloatable(false);
        actionsPanel.setVisible(false);

        JButton button;

        button = new JButton(insertAction);
        configureButton(button, insertAction, NewElementAction.TOOL_TIP_TEXT, NewElementAction.ICON_NAME);
        actionsPanel.add(button, 0);

        button = new JButton(removeItemAction);
        configureButton(button, removeItemAction, RemoveAction.TOOL_TIP_TEXT, RemoveAction.ICON_NAME);
        actionsPanel.add(button, 1);

        button = new JButton(insertBeforeAction);
        configureButton(button, insertBeforeAction, InsertAction.BEFORE_TOOL_TIP_TEXT, InsertAction.BEFORE_ICON_NAME);
        actionsPanel.add(button, 2);

        button = new JButton(insertAfterAction);
        configureButton(button, insertAfterAction, InsertAction.AFTER_TOOL_TIP_TEXT, InsertAction.AFTER_ICON_NAME);
        actionsPanel.add(button, 3);

        button = new JButton(itemUpAction);
        configureButton(button, itemUpAction, MoveAction.UP_TOOL_TIP_TEXT, MoveAction.UP_ICON_NAME);
        actionsPanel.add(button, 4);

        button = new JButton(itemDownAction);
        configureButton(button, itemDownAction, MoveAction.DOWN_TOOL_TIP_TEXT, MoveAction.DOWN_ICON_NAME);
        actionsPanel.add(button, 5);
    }

    protected void configureButton(AbstractButton button, Action action, String toolTipText, String iconName)
    {
        button.setAlignmentY(0.5f);

        Dimension btnSize = new Dimension(TOOLBAR_BUTTON_SIZE, TOOLBAR_BUTTON_SIZE);
        button.setSize(btnSize);
        button.setPreferredSize(btnSize);
        button.setMinimumSize(btnSize);
        button.setMaximumSize(btnSize);
        button.setToolTipText(toolTipText);

        if( iconName != null )
        {
            iconName = "com/developmentontheedge/beans/swing/resources/" + iconName;
            URL iconUrl = this.getClass().getClassLoader().getResource(iconName);
            if( iconUrl != null )
            {
                button.setIcon(new ImageIcon(iconUrl));
            }
        }
    }

    /**
     * Return row for "item"-s node of passed ArrayProperty
     */
    protected int getArrayRow(ArrayProperty arrayProperty, int item)
    {
        int row;
        Object[] path;
        if( item < 0 )
            path = arrayProperty.getPathToRoot();
        else
            path = arrayProperty.getPropertyAt(item).getPathToRoot();

        path[0] = getComponentModel();
        row = treeTable.getTree().getRowForPath(new TreePath(path));
        return row;
    }
    
    /**
     * Return array index for row number
     */
    protected int getArrayIndexByRow(ArrayProperty arrayProperty, int row)
    {
        int i;
        TreePath path = treeTable.getTree().getPathForRow(row);
        while(path.getParentPath() != null && path.getParentPath().getLastPathComponent() != arrayProperty)
            path = path.getParentPath();
        if(path.getParentPath() == null) return 0;
        for(i=0; i<arrayProperty.getPropertyCount(); i++)
        {
            if(path.getLastPathComponent() == arrayProperty.getPropertyAt(i))
                return i;
        }
        return 0;
    }

    @Override
    protected void setupPopupMenu(JPopupMenu popup)
    {
        super.setupPopupMenu(popup);
    }

    private void processInspectorEvent(InspectorEvent event)
    {
    }

    @Override
    protected void validateCell(int row, int column)
    {
        super.validateCell(row, column);
        // validate array related settings:
        boolean insertToEnd = false;
        boolean insertBefore = false;
        boolean insertAfter = false;
        boolean removeItem = false;
        boolean itemUp = false;
        boolean itemDown = false;
        Object value = null;
        if( row >= 0 && column >= 0 )
        {
            if( column == 0 )
                column++;
            value = treeTable.getValueAt(row, column);
        }
        Property p;
        if( ( value != null ) && ( value instanceof Property ) && ( ( p = (Property)value ) ).isEnabled() && !p.isReadOnly() )
        {
            // validate array element action
            if( p.getParent() instanceof ArrayProperty )
            {
                removeItem = true;
                insertBefore = true;
                insertAfter = true;
                itemUp = true;
                itemDown = true;
            }
            // validate array actions
            ArrayItem item = handleArraySelection(p, column);
            if( item.array != null )
            {
                insertToEnd = true;
            }
        }

        if( insertToEnd || insertBefore || insertAfter || removeItem || itemUp || itemDown )
        {
            actionsPanel.setVisible(true);
        }
        else
        {
            actionsPanel.setVisible(false);
        }

        enableAction(insertAction, insertToEnd);
        enableAction(removeItemAction, removeItem);
        enableAction(insertBeforeAction, insertBefore);
        enableAction(insertAfterAction, insertAfter);
        enableAction(itemUpAction, itemUp);
        enableAction(itemDownAction, itemDown);
    }

    protected void enableAction(Action action, boolean enable)
    {
        action.setEnabled(enable);
    }

    protected ArrayItem handleArraySelection(Property p, int column)
    {
        int arrayItem = -1;
        ArrayProperty arrayProperty = null;
        if( p instanceof ArrayProperty )
        {
            arrayProperty = (ArrayProperty)p;
            if( arrayProperty.getValue() == null )
            {
                Object array = Array.newInstance(arrayProperty.getItemClass(), 0);
                try
                {
                    arrayProperty.setValue(array);
                }
                catch( NoSuchMethodException e )
                {
                    Logger.getLogger().error( "handleArraySelection", e );
                }
            }
        }
        return new ArrayItem(arrayProperty, arrayItem);
    }

    /**
     *  invoke editor for selected cell
     */
    protected void editSelectedCell()
    {
        int row = treeTable.getSelectedRow();
        int column = treeTable.getSelectedColumn();
        editCell(row, column);
    }

    /**
     *  invoke editor for passed cell
     */
    public void editCell(int row, int column)
    {
        if( row < 0 || column < 0 )
        {
            return;
        }
        treeTable.editCellAt(row, column);
    }

    public static class ArrayItem
    {
        int index;
        ArrayProperty array;

        public ArrayItem(ArrayProperty array, int index)
        {
            this.array = array;
            this.index = index;
        }
    }


    ////////////////////////////////////////
    // actions related issues
    //
    protected Action insertAction;
    protected Action insertBeforeAction;
    protected Action insertAfterAction;
    protected Action removeItemAction;
    protected Action itemUpAction;
    protected Action itemDownAction;
    protected Action expandNodeAction;
    protected Action expandAllAction;
    protected Action collapseNodeAction;
    protected Action collapseAllAction;

    public class NewElementAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public static final String TOOL_TIP_TEXT = "Add element";
        public static final String ICON_NAME = "addArrayElement.gif";
        @Override
        public void actionPerformed(ActionEvent event)
        {
            int curRow = treeTable.getSelectedRow();
            int column = treeTable.getSelectedColumn();
            if( curRow < 0 || column < 0 )
                return;
            if( column == 0 )
                column++;
            Object value = treeTable.getValueAt(curRow, column);
            if( ! ( value instanceof Property ) )
                return;
            Property property = (Property)value;
            if( property != null )
            {
                curRow = doArrayAction(handleArraySelection(property, column), curRow);
                selectCell(curRow, column);
            }
        }

        public int doArrayAction(PropertyInspectorEx.ArrayItem item, int row)
        {
            if( item.array != null )
                try
                {
                    item.array.insertItem(item.array.getPropertyCount(), null);
                    processInspectorEvent(new InspectorEvent(PropertyInspectorEx.this, InspectorEvent.ARRAY_NODE_INSERTED, item.array,
                            item.index));
                    expandRow(row, true);
                    treeTable.updateUI();
                }
                catch( Exception e )
                {
                    Logger.getLogger().error("PropertyInspector.ArrayAction exception", e);
                }
            return row;
        }
    }

    public abstract class ArrayAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public abstract int doArrayAction(PropertyInspectorEx.ArrayItem item, int row);

        @Override
        public void actionPerformed(ActionEvent event)
        {
            int curRow = treeTable.getSelectedRow();
            int column = treeTable.getSelectedColumn();
            if( curRow < 0 || column < 0 )
                return;
            if( column == 0 )
                column++;
            Object value = treeTable.getValueAt(curRow, column);
            if( ! ( value instanceof Property ) )
                return;
            Property property = (Property)value;
            if( property != null )
            {
                if( property.getParent() instanceof ArrayProperty )
                {
                    property = property.getParent();
                    curRow = doArrayAction(handleArraySelection(property, column), curRow);
                    selectCell(curRow, column);
                }
            }
        }
    }

    public class InsertAction extends ArrayAction
    {
        private static final long serialVersionUID = 1L;
        public static final String BEFORE_TOOL_TIP_TEXT = "Insert before";
        public static final String AFTER_TOOL_TIP_TEXT = "Insert after";
        public static final String BEFORE_ICON_NAME = "insertBeforeArrayElement.gif";
        public static final String AFTER_ICON_NAME = "insertAfterArrayElement.gif";

        protected boolean before;

        public InsertAction(boolean before)
        {
            this.before = before;
        }

        @Override
        public int doArrayAction(PropertyInspectorEx.ArrayItem item, int row)
        {
            if( item.array != null )
                try
                {
                    int insertItem = getArrayIndexByRow(item.array, row);
                    if( !before )
                    {
                        item.index++;
                        insertItem++;
                        row++;
                    }
                    item.array.insertItem(insertItem, null);
                    processInspectorEvent(new InspectorEvent(PropertyInspectorEx.this, InspectorEvent.ARRAY_NODE_INSERTED, item.array,
                            item.index));
                    treeTable.updateUI();
                }
                catch( Exception e )
                {
                    Logger.getLogger().error("PropertyInspector.ArrayAction exception", e);
                }
            return row;
        }
    }


    public class RemoveAction extends ArrayAction
    {
        private static final long serialVersionUID = 1L;
        public static final String TOOL_TIP_TEXT = "Remove element";
        public static final String ICON_NAME = "removeArrayElement.gif";
        @Override
        public int doArrayAction(PropertyInspectorEx.ArrayItem item, int row)
        {
            if( item.array != null )
            {
                int removeItem = getArrayIndexByRow(item.array, row);
                // do removing...
                item.array.removeItem(removeItem);
                // now we send updating message
                processInspectorEvent(new InspectorEvent(PropertyInspectorEx.this, InspectorEvent.ARRAY_NODE_REMOVED, item.array,
                        item.index));
                treeTable.updateUI();
                row--;
            }
            return row;
        }
    }


    public class MoveAction extends ArrayAction
    {
        private static final long serialVersionUID = 1L;
        public static final String UP_TOOL_TIP_TEXT = "Move up";
        public static final String DOWN_TOOL_TIP_TEXT = "Move down";
        public static final String UP_ICON_NAME = "moveUpArrayElement.gif";
        public static final String DOWN_ICON_NAME = "moveDownArrayElement.gif";

        protected boolean up;

        public MoveAction(boolean up)
        {
            this.up = up;
        }

        @Override
        public int doArrayAction(PropertyInspectorEx.ArrayItem item, int row)
        {
            if( item.array != null )
            {
                int targetItem = getArrayIndexByRow(item.array, row);
                // do moving
                if( up )
                {
                    if( targetItem > 0 )
                    {
                        item.array.moveDown(--targetItem);
                        row--;
                    }
                }
                else
                {
                    if( targetItem < ( Array.getLength(item.array.getValue()) ) - 1 )
                    {
                        item.array.moveDown(targetItem);
                        row++;
                    }
                }
                processInspectorEvent(new InspectorEvent(PropertyInspectorEx.this, InspectorEvent.ARRAY_NODE_CHANGED, item.array,
                        item.index, 2));
                treeTable.updateUI();
            }
            return row;
        }
    }


    public class ExpandAllAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            JTree tree = treeTable.getTree();
            for( int i = 1; i < tree.getRowCount(); i++ )
                expandRow(i, false);
        }
    }


    public class ExpandNodeAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            int row = treeTable.getSelectedRow();
            int column = treeTable.getSelectedColumn();
            expandRow(row, true);
            selectCell(row, column);
        }
    }


    public class CollapseNodeAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            int row = treeTable.getSelectedRow();
            int column = treeTable.getSelectedColumn();
            collapseRow(row);
            selectCell(row, column);
        }
    }


    public class CollapseAllAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            JTree tree = treeTable.getTree();
            for( int i = 1; i < tree.getRowCount(); i++ )
                collapseRow(i);
        }
    }


    static class InspectorEvent extends EventObject
    {
        private static final long serialVersionUID = 1L;
        static final int ERROR = -1;
        static final int RESERVED = 0;
        static final int ARRAY_NODE_CHANGED = 1;
        static final int ARRAY_NODE_INSERTED = 2;
        static final int ARRAY_NODE_REMOVED = 3;
        int type = 0;
        ArrayProperty arrayProperty;
        Property property;
        int lastItem;
        int startItem;
        int count;

        InspectorEvent(PropertyInspector source, int type)
        {
            super(source);
            this.type = type;
        }

        InspectorEvent(PropertyInspector source, int type, ArrayProperty arrayProperty)
        {
            this(source, type);
            this.arrayProperty = arrayProperty;
        }

        InspectorEvent(PropertyInspector source, int type, ArrayProperty arrayProperty, int item)
        {
            this(source, type, arrayProperty);
            this.startItem = item;
        }

        InspectorEvent(PropertyInspector source, int type, ArrayProperty arrayProperty, Property lastProperty, int lastItem)
        {
            this(source, type, arrayProperty);
            this.property = lastProperty;
            this.lastItem = lastItem;
        }

        InspectorEvent(PropertyInspector source, int type, ArrayProperty arrayProperty, int startItem, int count)
        {
            this(source, type, arrayProperty);
            this.startItem = startItem;
            this.count = count;
        }

        public ArrayProperty getArrayProperty()
        {
            return arrayProperty;
        }

        public int getType()
        {
            return type;
        }

        public Property getProperty()
        {
            return property;
        }

        public int getLastItem()
        {
            return lastItem;
        }

        public int getStartItem()
        {
            return startItem;
        }

        public int getCount()
        {
            return count;
        }
    }
}
