package com.developmentontheedge.beans.lesson13;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import com.developmentontheedge.beans.swing.PropertyInspector;
import com.developmentontheedge.beans.undo.TransactionUndoManager;

import com.developmentontheedge.beans.lesson13.barchart.BarChart;
import com.developmentontheedge.beans.lesson13.barchart.Column;

public class BarChartApplication extends JFrame
{
    public BarChartApplication()
    {
        super("Bean Explorer Sample Application : Lesson 13");

        // create undo/redo toolbar
        initializeToolBar();

        // initialize the bean
        Column[] columns = {new Column("Q1", 5,  Color.blue, true ),
                            new Column("Q2", 8,  Color.red, true ),
                            new Column("Q3", 10, Color.magenta, true ),
                            new Column("Q4", 7,  Color.pink, true )};
        BarChart bean = new BarChart("Quarter report", columns);

        // and show it
        initializePropertyInspector( bean );
    }

    public void initializeToolBar()
    {
        buttonUndo = new JButton("Undo");
        buttonUndo.setEnabled( false );
        buttonUndo.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent evt )
            {
                undoManager.undo();
                updateToolBar();
            }
        } );
        buttonRedo = new JButton("Redo");
        buttonRedo.setEnabled( false );
        buttonRedo.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent evt )
            {
                undoManager.redo();
                updateToolBar();
            }
        } );

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable( false );
        toolBar.add( buttonUndo );
        toolBar.add( buttonRedo );
        getContentPane().add( toolBar,BorderLayout.NORTH );
    }

    public void initializePropertyInspector( BarChart bean )
    {
        // initialize property inspector
        PropertyInspector propertyInspector = new PropertyInspector();
        // create undo manager and attach it to property inspector
        undoManager = new TransactionUndoManager()
        {
            public void completeTransaction()
            {
                super.completeTransaction();
                updateToolBar();
            }
        };
        propertyInspector.addTransactionListener( undoManager );

        // Visualize the bean in BeanExplorer
        propertyInspector.explore(bean);
        propertyInspector.setPreferredSize(new Dimension(300,400));

        // create panes for bean's view and for the PropertyInspector
        JPanel beanPane = new JPanel(new BorderLayout());
        beanPane.setPreferredSize(new Dimension(150,400));
        beanPane.add(bean, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bean, propertyInspector);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    public void centerWindow()
    {
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;

        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;

        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        setVisible(true);
    }

    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
            System.exit(0);
    }

    private void updateToolBar()
    {
        buttonUndo.setEnabled( undoManager.canUndo() );
        buttonRedo.setEnabled( undoManager.canRedo() );
    }

    static public void main(String[] args)
    {
        BarChartApplication app = new BarChartApplication();
        app.pack();
        app.centerWindow();
    }

    private JButton buttonUndo;
    private JButton buttonRedo;
    private TransactionUndoManager undoManager;
}