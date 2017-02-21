package com.developmentontheedge.beans.lesson09;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.developmentontheedge.beans.lesson09.barchart.BarChart;
import com.developmentontheedge.beans.lesson09.barchart.Column;

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

/*->*/
    public void initializePropertyInspector( BarChart bean )
    {
        final JTabbedPane inspectorPane = new JTabbedPane();

        // initialize property inspector
        PropertyInspector propertyInspector = new PropertyInspector();
        propertyInspector.explore(bean, new String[] { "title", "orientation", "drawFont", "preferredSize", "barSpacing", "scale" } );
        inspectorPane.add( "View", propertyInspector );

        final Column []columns = bean.getColumns();
        for( int i = 0; i < columns.length; i++ )
        {
            final int index = i;
            PropertyInspector pi = new PropertyInspector();
            String item = "[" + index + "]";

            pi.explore(bean, new String[] { 
                       "columns/" + item + "/label", 
                       "columns/" + item + "/value", 
                       "columns/" + item + "/color", 
                       "columns/" + item + "/visible" } );             

            // In fact, the same thing can be done simplier by the following statement
            //
            // pi.explore( columns[ index ] );             
            //
            // But our purpose is to demonstrate how powerful is BeanExplorer, right?
            // So we perefer to use complicated way
            // Feel free to use any approach but rememebr that filtering
            // uses less memory than 'explore' since memory occupied
            // by properties is reused in filtered views.

            inspectorPane.add( columns[ index ].getLabel(), pi );

            // Since we used column labels to title our tabs 
            // we want to update titles when corresponding 'label' property changes
            // the following code will do the trick
            pi.addPropertyChangeListener( "label", new PropertyChangeListener()
            {
                 public void propertyChange( PropertyChangeEvent evt )
                 {
                      inspectorPane.setTitleAt( index + 1, columns[ index ].getLabel() );
                 }
            } );
        }

        // create panes for bean's view and for the PropertyInspector
        JPanel beanPane = new JPanel(new BorderLayout());
        beanPane.add(bean, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bean, inspectorPane);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }
/*--*/

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