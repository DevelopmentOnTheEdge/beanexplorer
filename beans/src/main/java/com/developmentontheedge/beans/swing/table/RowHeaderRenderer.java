package com.developmentontheedge.beans.swing.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.developmentontheedge.beans.editors.CustomEditorSupport;

public class RowHeaderRenderer extends CustomEditorSupport
{
    /**
     * This method returns a Component that renders value of the property.
     * Component is used for drawing only and will never obtain focus. Use {@link #getValue()} method to get current property value.
     *
     * @param parent the Component representing Property Inspector that has requested a editor
     * @param isSelected  true if the cell is to be highlighted
     * @param hasFocus    true if the cell has focus.
     * @return rendered Component
     */
    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        DefaultTableCellRenderer label = new DefaultTableCellRenderer();
        if(parent instanceof JTable)
        {
            JTableHeader header = ((JTable)parent).getTableHeader();
            if (header != null)
            {
                label.setForeground(header.getForeground());
                label.setBackground(header.getBackground());
                label.setFont(header.getFont());
            }
        }
        label.setText((bean == null) ? "" : ""+((RowHeaderBean)bean).getNumber());
        label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
}

