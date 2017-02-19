package com.developmentontheedge.beans.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class ColorComboBox extends JComboBox<Color>
{
    private static final long serialVersionUID = 1L;
    private Color  bColor;
    private Color  fColor;
    private Color  curColor;
    private String curText;

    public final static String CUSTOM_TEXT = "more colors...";
    private final static int   MAX_HIGHT = 15;
    private final static int   REN_WIDTH=30;
    private DefaultComboBoxModel<Color> model = null;
    Vector<PropertyChangeListener> listeners;

    public ColorComboBox()
    {
        this(null);
    }                                        

    public ColorComboBox(Color current)
    {
        model = new DefaultComboBoxModel<>();

        if( current != null )
        { 

            if( !(current instanceof NamedColor) )
                current = new NamedColor(current, "current");

            model.addElement(current);
        }

        model.addElement(new NamedColor(Color.gray,    "gray"));
        model.addElement(new NamedColor(Color.red,     "red"));
        model.addElement(new NamedColor(Color.blue,    "blue"));
        model.addElement(new NamedColor(Color.green,   "green"));
        model.addElement(new NamedColor(Color.yellow,  "yellow"));
        model.addElement(new NamedColor(Color.white,   "white"));
        model.addElement(new NamedColor(Color.black,   "black"));
        model.addElement(new NamedColor(Color.cyan,    "cyan"));
        model.addElement(new NamedColor(Color.magenta, "magenta"));
        model.addElement(new NamedColor(Color.pink,    "pink"));
        model.addElement(new NamedColor(Color.orange,  "orange"));

        model.addElement(new NamedColor(Color.white,   CUSTOM_TEXT, false));

        setModel(model);

        addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                  NamedColor color = (NamedColor)getSelectedItem();
                  Color oldValue = curColor ;
                  Color newValue = null ;
                  
                  if( !color.name.equals(CUSTOM_TEXT)) 
                      newValue = color;
                  else
                  {
                      newValue = JColorChooser.showDialog( null, "Choose color", oldValue );

                      if (newValue != null)
                      {
                          oldValue = color;
                          newValue = new NamedColor(newValue,    CUSTOM_TEXT);
                          model.removeElement( oldValue );
                          model.addElement( newValue );
                          model.setSelectedItem(newValue);
                      }
                  }

                  if (newValue != null)
                  {
                      fireColorValueChanged(oldValue,newValue);
                      curColor = newValue;
                  }
              }
        });

        installAncestorListener();
        setOpaque(true);
        updateUI();

        setSelectedIndex(0);
        setRenderer(new ComboBoxRenderer());
        setMaximumRowCount(7);
    }

    public void addColorPropertyChangeListener(PropertyChangeListener listener)
    {
        if (listeners == null)
            listeners = new Vector<>();
        listeners.add(listener);
    }

    protected void fireColorValueChanged(Object oldValue,Object newValue)
    {
        if (listeners == null)
            return;

        PropertyChangeEvent evt = new PropertyChangeEvent(this,"Color",oldValue,newValue);

        for (int i=0; i<listeners.size(); i++)
        {
            PropertyChangeListener l = listeners.get(i);
            l.propertyChange(evt);
        }
    }

    public static ColorComboBox getInstance(Color color)
    {
        return new ColorComboBox( color );
    }

    public static Component getValueRenderer(final Color color)
    {
        return new Component()
        {
            @Override
            public void paint(Graphics g)
            {
                g.setColor(color);
                g.fillRect(9,5,REN_WIDTH,8);

                g.setColor(Color.black);
                g.drawRect(9,5,REN_WIDTH,8);

                String text = getText(color);

                if (text == null)
                    text = "custom";

                g.setFont(f);
                g.setColor(Color.black);
                g.drawString(text,20+REN_WIDTH,12);
            }
        };
    }

    public static String getText(Color color)
    {
        if (color==null)
            return null;

        if( color instanceof NamedColor )
            return ((NamedColor)color).name;

        if (color.equals(Color.red))           return "red"        ;
        else if (color.equals(Color.gray))     return "gray"       ;
        else if (color.equals(Color.red))      return "red"        ;
        else if (color.equals(Color.blue))     return "blue"       ;
        else if (color.equals(Color.green))    return "green"      ;
        else if (color.equals(Color.yellow))   return "yellow"     ;
        else if (color.equals(Color.white))    return "white"      ;
        else if (color.equals(Color.black))    return "black"      ;
        else if (color.equals(Color.cyan))     return "cyan"       ;
        else if (color.equals(Color.magenta))  return "magenta"    ;
        else if (color.equals(Color.pink))     return "pink"       ;
        else if (color.equals(Color.orange))   return "orange"     ;

        return null;  
    }
    
    ///////////////////////////////////////////////////////////////////
    // Named color
    //

    public static class NamedColor extends Color
    {
        private static final long serialVersionUID = 1L;
        
        public NamedColor(Color c, String name)
        {
            this(c, name, true);
        }

        public NamedColor(Color c, String name, boolean showColor)
        {
            super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            this.name = name;
            this.showColor = showColor;
        }
        
        protected String name;
        public String getName()
        {
            return name;
        }

        protected boolean showColor;
        public boolean isShowColor()
        {
            return showColor;
        }

        @Override
        public String toString() 
        {
             return name + ", " + super.toString() + ", showColor=" + showColor;
        }

        @Override
        public boolean equals(Object obj) 
        {
            if(obj instanceof NamedColor)
                return name.equals (((NamedColor)obj).name) && super.equals(obj);
            return super.equals(obj);    
        }
    }

    ///////////////////////////////////////////////////////////////////
    // ComboBoxRenderer
    //

    static private Font f = new Font( "Arial", Font.PLAIN, 11 );
    class ComboBoxRenderer extends JPanel implements ListCellRenderer<Color>
    {
        private static final long serialVersionUID = 1L;
        
        NamedColor color;

        @Override
        public void paint(Graphics g)
        {
            Dimension dim = getSize();

            int y = 1;
            int height = dim.height;
            y = (dim.height - 8)/2;
            if (dim.height >= MAX_HIGHT)
            {
                y = (dim.height - 8)/2;
                dim.height = MAX_HIGHT;
            }

            g.setColor(bColor);
            g.fillRect(0,0,dim.width-1,dim.height-1);

            if( color.showColor )
            {
                g.setColor(color);
                g.fillRect(5,y,REN_WIDTH,8);

                g.setColor(Color.black);
                g.drawRect(5,y,REN_WIDTH,8);
            }

            g.setColor(fColor);
            g.setFont(f);

            if( color.showColor )
                 g.drawString(color.name,20+REN_WIDTH,y+9);
            else
                 g.drawString(color.name,5,y+9);
        }

        /**
         * Return a component that has been configured to display the specified
         * value. That component's <code>paint</code> method is then called to
         * "render" the cell.  If it is necessary to compute the dimensions
         * of a list because the list cells do not have a fixed size, this method
         * is called to generate a component on which <code>getPreferredSize</code>
         * can be invoked.
         *
         * @param list The JList we're painting.
         * @param value The value returned by list.getModel().getElementAt(index).
         * @param index The cells index.
         * @param isSelected True if the specified cell was selected.
         * @param cellHasFocus True if the specified cell has the focus.
         * @return A component whose paint() method will render the specified value.
         *
         * @see JList
         * @see ListSelectionModel
         * @see ListModel
         */
        @Override
        public Component getListCellRendererComponent (
                                                      JList<? extends Color> list,
                                                      Color value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)
        {
            if (isSelected)
            {
                bColor = list.getSelectionBackground();
                fColor = list.getSelectionForeground();
            }
            else
            {

                bColor = list.getBackground();
                fColor = list.getForeground();
            }

            if ((index != -1))
            {
                Dimension dim = new Dimension(100,15);
                setSize(dim);
                setPreferredSize(dim);
            }

            color = (NamedColor) value;
            return this;
        }
    }
}
