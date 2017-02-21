package com.developmentontheedge.beans.lesson05.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.developmentontheedge.beans.editors.CustomEditorSupport;

public class ScaleEditor extends CustomEditorSupport
{
    /**
     * Creates Editor or Renderer component
     * 
     * @param parent
     * @param isSelected
     * @return Editor or Renderer component
     */
    private Component createComponent(Component parent, boolean isSelected)
    {
        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        
        JButton bPlus = new JButton("+");
        JButton bMinus = new JButton("-");
        
        buttonPanel.add( bMinus );
        buttonPanel.add( bPlus );
        
        bMinus.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent e) 
                {
                    int v = ((Integer)getValue()).intValue();
                    if( v > 0 )
                        setValue(new Integer(v-1));
                }
            });


        bPlus.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent e) 
                {
                    int v = ((Integer)getValue()).intValue();
                    if( v <10 )
                        setValue(new Integer(v+1));
                }
            });
        
        buttonPanel.setPreferredSize ( new Dimension(100,15) );
        
        Integer value = (Integer)getValue();
        
        JLabel label = new JLabel();
        
        if (value != null)
        {
            label.setText(value.toString ());
        }
        
        setColor(label,isSelected,parent);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(buttonPanel,BorderLayout.EAST);
        panel.add(label,BorderLayout.CENTER);
        return panel;
    }
    
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus) 
    {
        if(isSelected)
            return createComponent(parent,isSelected);
        else
        {
            Integer value = (Integer)getValue();
        
            JLabel label = new JLabel();
        
            if (value != null)
            {
                label.setText(value.toString ());
            }
            
            setColor(label,isSelected,parent);
            
            return  label;
        }
    }
    
    public Component getCustomEditor(Component parent, boolean isSelected) 
    {
        return createComponent(parent,isSelected);
    }
    
    protected Object processValue() 
    {
        return null;
    }
}
