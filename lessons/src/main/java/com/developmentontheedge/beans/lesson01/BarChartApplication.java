package com.developmentontheedge.beans.lesson01;

import java.beans.Introspector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.developmentontheedge.beans.lesson01.barchart.BarChart;
import com.developmentontheedge.beans.lesson01.barchart.Column;

import com.developmentontheedge.beans.swing.PropertyInspector;

public class BarChartApplication extends JFrame
{
    public BarChartApplication()
    {
        super("Bean Explorer Sample Application");

        // initialize the bean
        Column[] columns = {new Column("Q1", 5,  Color.blue, true ),
                            new Column("Q2", 8,  Color.red, true ),
                            new Column("Q3", 10, Color.magenta, true ),
                            new Column("Q4", 7,  Color.pink, true )};
        BarChart bean = new BarChart("Quarter report", columns);

        // and show it
        initializePropertyInspector( bean );
    }

    public void initializePropertyInspector( BarChart bean )
    {
        // initialize property inspector
        PropertyInspector propertyInspector = new PropertyInspector();

        // Visualize the bean in BeanExplorer
        propertyInspector.explore(bean);
 
        // create panes for bean's view and for the PropertyInspector
        JPanel beanPane = new JPanel(new BorderLayout());
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

    static public void main(String[] args)
    {
        BarChartApplication app = new BarChartApplication();
 
        app.pack();
        app.centerWindow();
    }
}