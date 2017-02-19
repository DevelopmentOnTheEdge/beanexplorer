package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Special layout based on GridLayout. 
 * It is designed to be used by composite property editor. 
 */
public class GridLayout2 implements LayoutManager
{
    int hgap = 0;
    int vgap = 0;
    int rows = 1;
    int cols = 2;

    public GridLayout2()
    {
    }

    @Override
    public void addLayoutComponent(String name, Component comp)
    {
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        synchronized (parent.getTreeLock())
        {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = rows;
            int ncols = cols;

            if(nrows > 0)
            {
                ncols = (ncomponents + nrows - 1) / nrows;
            }
            else
            {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int w = 0;
            int h = 0;
            for(int i = 0 ; i < ncomponents ; i++)
            {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getPreferredSize();
                if(w < d.width)
                {
                    w = d.width;
                }
                if(h < d.height)
                {
                    h = d.height;
                }
            }
            return new Dimension(insets.left + insets.right + ncols*w + (ncols-1)*hgap,
                                 insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent)
    {
        synchronized (parent.getTreeLock())
        {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = rows;
            int ncols = cols;

            if(ncomponents == 0)
            {
                return;
            }
            if(nrows > 0)
            {
                ncols = (ncomponents + nrows - 1) / nrows;
            }
            else
            {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            //int w = parent.getWidth()  - (insets.left + insets.right); // original GridLayout code
            //int h = parent.getHeight() - (insets.top + insets.bottom); // original GridLayout code
            //w = (w - (ncols - 1) * hgap) / ncols;                      // original GridLayout code
            //h = (h - (nrows - 1) * vgap) / nrows;                      // original GridLayout code
            int w1 = parent.getWidth()  - (insets.left + insets.right);  // added
            int h1 = parent.getHeight() - (insets.top + insets.bottom);  // added
            int w = (w1 - (ncols - 1) * hgap) / ncols;                   // added
            int h = (h1 - (nrows - 1) * vgap) / nrows;                   // added

            //for(int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) // original GridLayout code
            for(int c = 0, x = insets.left ; c < ncols ; c++) // added
            {
                for(int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap)
                {
                    int i = r * ncols + c;
                    if(i < ncomponents)
                    {
                        //parent.getComponent(i).setBounds(x, y, w, h); // original GridLayout code
                        Component comp = parent.getComponent(i);        // added
                        int width = comp.getPreferredSize().width;      // added
                        if(c == ncols-1) // last column fit all remaining space (added)
                        {
                            width = w1 - x > 0 ? w1 - x : width;        // added
                        }
                        comp.setBounds(x, y, width, h);                 // added
                        //x += comp.getWidth() + hgap;                    // added
                        x += width + hgap;                              // added
                    }
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap +
        ",rows=" + rows + ",cols=" + cols + "]";
    }
}


