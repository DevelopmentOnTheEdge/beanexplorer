package com.developmentontheedge.beans.lesson08;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import com.developmentontheedge.beans.lesson08.barchart.BarChart;
import com.developmentontheedge.beans.lesson08.barchart.Column;

import com.developmentontheedge.beans.swing.PropertyInspector;

public class BarChartApplication extends JFrame
{
    private PropertyInspector propertyInspector;
    private BarChart bean;
    
    JCheckBox usualCheckBox     = new JCheckBox( "Usual",    true  );
    JCheckBox expertCheckBox    = new JCheckBox( "Expert",   false );
    JCheckBox hiddenCheckBox    = new JCheckBox( "Hidden",   false );
    JCheckBox preferredCheckBox = new JCheckBox( "Preferred",false );
    
    public BarChartApplication()
    {
        super("Bean Explorer Sample Application");

        // initialise the bean
        Column[] columns = {new Column("Q1", 5,  Color.blue, true ),
                            new Column("Q2", 8,  Color.red, true ),
                            new Column("Q3", 10, Color.magenta, true ),
                            new Column("Q4", 7,  Color.pink, true )};
        
        bean = new BarChart("Quarter report", columns);
        
        // initialize property inspector
        JPanel panel = new JPanel(new BorderLayout());
        propertyInspector = new PropertyInspector();
        propertyInspector.explore(bean);
        
        // create the 
        JPanel beanPane = new JPanel(new BorderLayout());
        beanPane.add(bean, BorderLayout.CENTER);
        
        ToolPanel  toolPanel = new ToolPanel();

        bean.setPreferredSize             ( new Dimension(300,150) );
        toolPanel.setPreferredSize        ( new Dimension(350,250) );
        toolPanel.setMinimumSize          ( new Dimension(350,250) );
        propertyInspector.setPreferredSize( new Dimension(360,400) );

        JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT,    toolPanel,     bean );
        JSplitPane splitPane     = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,  splitPaneLeft, propertyInspector );
        
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

    protected  void updateShowMode()
    {
        int commonFlag = PropertyInspector.SHOW_USUAL;
        if(usualCheckBox.isSelected())
        {
            expertCheckBox.setEnabled(false);
            hiddenCheckBox.setEnabled(false);
            preferredCheckBox.setEnabled(false);
        }
        else
        {
            expertCheckBox.setEnabled(true);   
            hiddenCheckBox.setEnabled(true);   
            preferredCheckBox.setEnabled(true);
            
            if (expertCheckBox.isSelected())
                commonFlag |= PropertyInspector.SHOW_EXPERT;
                
            if (hiddenCheckBox.isSelected())
                commonFlag |= PropertyInspector.SHOW_HIDDEN;
        
            if (preferredCheckBox.isSelected())
                commonFlag |= PropertyInspector.SHOW_PREFERRED;
        }
        
        propertyInspector.setPropertyShowMode(commonFlag);
        propertyInspector.explore( bean );
    }
    
    public class ToolPanel extends JPanel
    {
        public ToolPanel()
        {
            super( new GridBagLayout() );

            JPanel              buttonPanel         = new JPanel();
            JPanel              expandPanel         = new JPanel(new FlowLayout());
            
            JButton             collapseButton      = new JButton("Collapse");
            JButton             expandButton        = new JButton("Expand");
            
            JPanel              textFieldPanel      = new JPanel();
            final JTextField    textField           = new JTextField("1");
            JLabel              label               = new JLabel("Level");
            
            textField.setPreferredSize(new Dimension(30,20));
            
            textFieldPanel.add(label);
            textFieldPanel.add(textField);
            
            expandPanel.add(expandButton);
            expandPanel.add(textFieldPanel);
            
            expandPanel.setBorder(BorderFactory.createEtchedBorder());
            
            final JCheckBox     showGridCheckBox    = new JCheckBox( "Show  grid",   true );
            final JCheckBox     showTooltipCheckBox = new JCheckBox( "Show Tooltip", true );
            final JCheckBox     showRootCheckBox    = new JCheckBox( "Show Root",    true );
            
            TitledBorder  titledBorder  = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(148, 145, 140))," Visible ");
            
            BoxLayout boxLayout = new BoxLayout(buttonPanel,BoxLayout.Y_AXIS);
            buttonPanel.setLayout(boxLayout);
            
            buttonPanel.setBorder(titledBorder);
            this.add(buttonPanel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
                                                         GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 30, 15));
            buttonPanel.add( usualCheckBox     );
            buttonPanel.add( expertCheckBox    );
            buttonPanel.add( hiddenCheckBox    );
            buttonPanel.add( preferredCheckBox );
            updateShowMode();
            
            this.add(expandPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
            this.add(collapseButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                                          GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 60, 0, 0), 9, 0));
            this.add(showGridCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
            this.add(showTooltipCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            this.add(showRootCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            
            ActionListener actionListener  = new ActionListener()
                                             {
                                                 public void actionPerformed(ActionEvent e)
                                                 {
                                                     updateShowMode();
                                                 }
                                             };
            
            usualCheckBox.addActionListener    ( actionListener  );
            expertCheckBox.addActionListener   ( actionListener  );
            hiddenCheckBox.addActionListener   ( actionListener  );
            preferredCheckBox.addActionListener( actionListener  );
            
            expandButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        
                        try
                        {
                            int value = Integer.parseInt( textField.getText() );
                            propertyInspector.expand( value );  
                        }
                        catch(NumberFormatException ex)
                        {
                            JOptionPane.showMessageDialog(BarChartApplication.this, "Value of field \"level\" should be integer.");
                        }
                    }
                });
            
            collapseButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                          propertyInspector.collapseAll(); 
                    }
                });
                
            showGridCheckBox.addActionListener(new ActionListener()   
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        propertyInspector.setShowGrid( showGridCheckBox.isSelected());  
                    }
                });
                
            showTooltipCheckBox.addActionListener(new ActionListener()    
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        propertyInspector.setShowToolTip( showTooltipCheckBox.isSelected());  
                    }
                });
            
            showRootCheckBox.addActionListener(new ActionListener()    
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        propertyInspector.setRootVisible( showRootCheckBox.isSelected() );
                    }
                });
        }
    }
    
    static public void main( String[] args )
    {
        BarChartApplication app = new BarChartApplication();
        app.pack();
        app.centerWindow();
    }
}