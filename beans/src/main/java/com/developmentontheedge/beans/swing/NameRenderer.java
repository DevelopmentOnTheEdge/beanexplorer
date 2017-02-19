package com.developmentontheedge.beans.swing;

import java.awt.Component;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.Property;

/** @todo comments */
public class NameRenderer implements TreeCellRenderer
{
    protected DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    /**
     * Creates the component which will represent name of the property for the current cell.
     * @todo comments
     */
    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean isSelected,
    boolean isExpanded, boolean isLeaf, int row, boolean hasFocus )
    {
        boolean isMotif = UIManager.getLookAndFeel().getClass().getName().equals( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" );

        // restore icons
        defaultRenderer.setLeafIcon( defaultRenderer.getDefaultLeafIcon() );
        defaultRenderer.setOpenIcon( defaultRenderer.getDefaultOpenIcon() );
        defaultRenderer.setClosedIcon( defaultRenderer.getDefaultClosedIcon() );
        if ( value instanceof Property )
        {
            Image img = ( ( Property )value ).getIcon();
            if ( img != null )
            {
                Icon icon = new ImageIcon( img );
                if ( isLeaf )
                {
                    defaultRenderer.setLeafIcon( icon );
                }
                else if ( isExpanded )
                {
                    defaultRenderer.setOpenIcon( icon );
                }
                else
                {
                    defaultRenderer.setClosedIcon( icon );
                }
            }
            else if ( value instanceof ArrayProperty )
            {
            }

        }
        Component comp = defaultRenderer.getTreeCellRendererComponent( tree,
        value instanceof Property ? ( ( Property )value ).getDisplayName() : value,
        isSelected, isExpanded, isLeaf, row, hasFocus );

        if ( isSelected && isMotif )
        {
            // override from JTable
            comp.setForeground( UIManager.getColor( "Table.selectionForeground" ) );
            ( ( JComponent )comp ).setOpaque( true );
        }

        return comp;
    }
}
