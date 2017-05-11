package com.developmentontheedge.beans;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TestClass extends JPanel
{
   public TestClass(int i)
   {
       this.i = i;
   }

   public TestClass(int i, Integer integer)
   {
       this(i);
       this.integer = integer;
   }

   public TestClass(int[] i, Integer[] integer, String string)
   {
       this(100, new Integer(200));
       this.string = string;
   }

   private int[] array = new int[0];
   public int[] getArray()
   {
      return array;
   }
   public void setArray(int[] array)
   {
      this.array = array;
   }
   public int getArray(int i)
   {
      return array[i];
   }
   public void setArray(int index, int value)
   {
      array[index] = value;
   }

   private String string = "Sample";
   public String getString()
   {
      return string;
   }
   public void setString(String string)
   {
      this.string = string;
      repaint();
   }

   private Font font = new Font(null, Font.PLAIN, 12);
   public Font getFont1()
   {
      return font;
   }
   public void setFont1(Font font)
   {
      this.font = font;
      repaint();
   }

   private Color color = Color.blue;
   public Color getColor()
   {
      return color;
   }
   public void setColor(Color color)
   {
      this.color = color;
      repaint();
   }

   private int i;
   public int getInt()
   {
      return i;
   }
   public void setInt(int i)
   {
      setBorder(new TitledBorder(""+i));
      this.i = i;
   }

   private Integer integer;
   public Integer getInteger()
   {
      return integer;
   }
   public void setInteger(Integer integer)
   {
      this.integer = integer;
   }

   public void paint(Graphics g)
   {
       Graphics2D g2 = (Graphics2D)g;

       FontMetrics fm = g2.getFontMetrics(font);
       int width = fm.stringWidth(string);
       int height = fm.getHeight();

       setPreferredSize(new Dimension(width+20, height+20));
       setSize(new Dimension(width+20, height+20));
       revalidate();

       super.paint(g);

       g2.setColor(color);
       g2.setFont(font);

       RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
       g2.setRenderingHints(hints);

       Rectangle rect = getVisibleRect();
       double x = rect.getCenterX() - fm.stringWidth(string)/2;
       double y = rect.getCenterY() + fm.getHeight()/3;

       g2.drawString(string, (int)x, (int)y);
   }
}
