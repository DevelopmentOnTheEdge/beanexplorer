package com.developmentontheedge.beans.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.developmentontheedge.beans.swing.table.BeanTableModelAdapter;
import com.developmentontheedge.beans.swing.table.Column;
import com.developmentontheedge.beans.swing.table.ColumnModel;
import com.developmentontheedge.beans.swing.table.RowModel;
import com.developmentontheedge.beans.swing.table.RowModelEvent;
import com.developmentontheedge.beans.swing.table.SortedColumnModel;
import com.developmentontheedge.beans.swing.table.SortedTableModel;

/**
 * Paginate tabular property inspector
 */
public class PagedTabularPropertyInspector extends TabularPropertyInspector
{
    private static final long serialVersionUID = 1L;
    protected JButton firstButton;
    protected JButton previousButton;
    protected JEditorPane pageLabel;
    protected JLabel pageCountLabel;
    protected JButton nextButton;
    protected JButton lastButton;
    protected JComboBox entries;

    protected int[] pages;

    public PagedTabularPropertyInspector(int[] pages)
    {
        super();
        this.pages = pages;
        sortEnabled = false;
    }

    @Override
    public void setSortEnabled(boolean enabled)
    {
        //sort is blocked now
    }

    @Override
    public void explore(RowModel rowModel, ColumnModel columnModel)
    {
        unregisterListeners();
        if( rowModel != null )
        {
            tableModel = new PaginateBeanTableModelAdapter(rowModel, columnModel, pages[0]);

            tableModel.setRowHeader(getRowHeader());
            sortEnabled = columnModel instanceof SortedColumnModel;
            table.setModel(tableModel);

            if( getRowHeader() )
            {
                TableColumn col = table.getColumnModel().getColumn(0);
                col.setResizable(false);
                col.setMinWidth(30);
                col.setPreferredWidth(30);
                col.setMaxWidth(30);
            }

            //initHeaders();
            if( table.getColumnCount() > 8 )
            {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
            else
            {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }

            if( tableModel instanceof SortedTableModel )
            {
                ( (SortedTableModel)tableModel ).sort();
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
        fillEntriesBox();
        updateControls();
    }

    @Override
    protected void initTable()
    {
        super.initTable();
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel pagePanel = new JPanel();
        firstButton = new JButton("First");
        firstButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if( tableModel != null )
                {
                    ( (PaginateBeanTableModelAdapter)tableModel ).setCurrentPage(1);
                    updateControls();
                }
            }
        });
        pagePanel.add(firstButton);
        previousButton = new JButton("Previous");
        previousButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if( tableModel != null )
                {
                    int currentPage = ( (PaginateBeanTableModelAdapter)tableModel ).getCurrentPage();
                    if( currentPage > 1 )
                    {
                        currentPage--;
                        ( (PaginateBeanTableModelAdapter)tableModel ).setCurrentPage(currentPage);
                        updateControls();
                    }
                }
            }
        });
        pagePanel.add(previousButton);
        pagePanel.add(new JLabel("Page "));
        pageLabel = new JEditorPane();
        pageLabel.setPreferredSize(new Dimension(50, 20));
        pageLabel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent ke)
            {
                if( ke.getKeyCode() == ke.VK_ENTER )
                {
                    ke.consume();
                    if( tableModel != null )
                    {
                        try
                        {
                            int page = Integer.parseInt(pageLabel.getText().trim());
                            int pageCount = ( (PaginateBeanTableModelAdapter)tableModel ).getPageCount();
                            if( page >= 1 && page <= pageCount )
                            {
                                ( (PaginateBeanTableModelAdapter)tableModel ).setCurrentPage(page);
                            }
                        }
                        catch( NumberFormatException e )
                        {
                        }
                    }
                    updateControls();
                }
            }
        });
        pagePanel.add(pageLabel);
        pagePanel.add(new JLabel(" of "));
        pageCountLabel = new JLabel();
        pagePanel.add(pageCountLabel);
        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if( tableModel != null )
                {
                    int currentPage = ( (PaginateBeanTableModelAdapter)tableModel ).getCurrentPage();
                    if( currentPage < ( (PaginateBeanTableModelAdapter)tableModel ).getPageCount() )
                    {
                        currentPage++;
                        ( (PaginateBeanTableModelAdapter)tableModel ).setCurrentPage(currentPage);
                        updateControls();
                    }
                }
            }
        });
        pagePanel.add(nextButton);
        lastButton = new JButton("Last");
        lastButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if( tableModel != null )
                {
                    ( (PaginateBeanTableModelAdapter)tableModel ).setCurrentPage( ( (PaginateBeanTableModelAdapter)tableModel )
                            .getPageCount());
                    updateControls();
                }
            }
        });
        pagePanel.add(lastButton);
        controlPanel.add(pagePanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.add(new JLabel("Show "));
        entries = new JComboBox();
        entries.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if( tableModel != null )
                {
                    Object selectedObject = e.getItem();
                    int pageSize = Integer.parseInt(selectedObject.toString());
                    ( (PaginateBeanTableModelAdapter)tableModel ).setPageSize(pageSize);
                    updateControls();
                }
            }
        });
        rightPanel.add(entries);
        rightPanel.add(new JLabel(" entries"));
        controlPanel.add(rightPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);
    }

    protected void fillEntriesBox()
    {
        entries.removeAllItems();
        for( int p : pages )
        {
            entries.addItem(Integer.toString(p));
        }
    }

    protected void updateControls()
    {
        int currentPage = ( (PaginateBeanTableModelAdapter)tableModel ).getCurrentPage();
        int pageCount = ( (PaginateBeanTableModelAdapter)tableModel ).getPageCount();

        firstButton.setEnabled(currentPage > 1);
        previousButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pageCount);
        lastButton.setEnabled(currentPage < pageCount);

        pageLabel.setText(Integer.toString(currentPage));
        pageCountLabel.setText(Integer.toString(pageCount));
    }

    public static class PaginateBeanTableModelAdapter extends BeanTableModelAdapter implements SortedTableModel
    {
        protected int pageSize;
        protected int currentPage;

        public PaginateBeanTableModelAdapter(RowModel rm, ColumnModel columnModel, int pageSize)
        {
            super(rm, columnModel);
            this.pageSize = pageSize;
            this.currentPage = 1;
        }

        public int getCurrentPage()
        {
            return currentPage;
        }

        public int getPageCount()
        {
            int rowCount = super.getRowCount();
            if( rowCount % pageSize == 0 )
            {
                return rowCount / pageSize;
            }
            else
            {
                return ( rowCount / pageSize ) + 1;
            }
        }

        public void setCurrentPage(int currentPage)
        {
            this.currentPage = currentPage;
            fireTableDataChanged();
        }

        public void setPageSize(int pageSize)
        {
            this.pageSize = pageSize;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount()
        {
            int rowCount = super.getRowCount();
            rowCount -= pageSize * ( currentPage - 1 );
            return Math.min(rowCount, pageSize);
        }

        @Override
        public Object getValueAt(int row, int column)
        {
            return super.getValueAt(getModelRow(row), column);
        }

        @Override
        public void setValueAt(Object aValue, int row, int column)
        {
            super.setValueAt(aValue, getModelRow(row), column);
        }

        @Override
        public Object getModelForRow(int row)
        {
            return super.getModelForRow(getModelRow(row));
        }

        protected int getModelRow(int viewRow)
        {
            return ( pageSize * ( currentPage - 1 ) ) + viewRow;
        }

        @Override
        synchronized public void propertyChange( final PropertyChangeEvent evt )
        {
            super.propertyChange( evt );
            String name = evt.getPropertyName();
            if ( evt.getSource() != columnModel )
            {
                if ( name.equals( "sorting" ) )
                {
                    Column column = ( Column )evt.getSource();
                    if ( !column.getEnabled() )
                    {
                        return;
                    }
                    sort();
                }
            }
        }

        @Override
        synchronized public void tableChanged( RowModelEvent evt )
        {
            super.tableChanged( evt );
            sort();
        }

        @Override
        public void sort()
        {
            if(getColumnModel() instanceof SortedColumnModel)
                ((SortedColumnModel)getColumnModel()).sort();
            fireTableDataChanged();
        }
    }
}
