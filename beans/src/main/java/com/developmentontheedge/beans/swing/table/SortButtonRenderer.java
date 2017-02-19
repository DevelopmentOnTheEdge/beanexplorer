package com.developmentontheedge.beans.swing.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Render column header as button with field name and arrow indicates sort order.
 */
public class SortButtonRenderer extends JButton implements TableCellRenderer
{
    static private int FONT_SIZE = 12;
    private final JButton downButton,upButton;
    private final Column fieldOptions;

    /**
     * Create renderer for column described by specified field options.
     */
    public SortButtonRenderer( Column fieldOptions )
    {
        this.fieldOptions = fieldOptions;

        setMargin(new Insets(0,0,0,0));
        setHorizontalTextPosition(LEFT);
        setIcon(new BlankIcon());
        Font font = new Font("Arial",Font.PLAIN,FONT_SIZE);
        setFont(font);

        downButton = new JButton();
        downButton.setFont(font);
        downButton.setMargin(new Insets(0,0,0,0));
        downButton.setHorizontalTextPosition(LEFT);
        downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
        downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

        upButton = new JButton();
        upButton.setFont(font);
        upButton.setMargin(new Insets(0,0,0,0));
        upButton.setHorizontalTextPosition(LEFT);
        upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
        upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
    }

    /**
     * Returns renderer.
     * @return renderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {
        JButton button = this;
        if(fieldOptions instanceof ColumnWithSort)
        {
            switch( ((ColumnWithSort)fieldOptions).getSorting() )
            {
                case ColumnWithSort.SORTING_ASCENT:
                    button = upButton;
                    break;
                case ColumnWithSort.SORTING_DESCENT:
                    button = downButton;
                    break;
            }
        }
        button.setText((value ==null) ? "" : value.toString());
        return button;
    }
}
