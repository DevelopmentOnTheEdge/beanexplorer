package com.developmentontheedge.beans.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.developmentontheedge.beans.model.Property;

/**
 * Common interface for all property inspectors.
 * Methods of this interface allow to implement specific mouse processing for PropertyInspector<br><br>
 * Example of using AbstractPropertyInspector interface
 *
 * <pre>
 *
 *       ...
 *       final AbstractPropertyInspector propertyInspector = new PropertyInspector();
 *       propertyInspector.addMouseListener(new MouseAdapter()
 *                                          {
 *                                               public void mouseClicked(MouseEvent e)
 *                                               {
 *                                                   Point pt = e.getPoint();
 *                                                   Property property = propertyInspector.getProperty(pt);
 *                                                   property.setValue(new Boolean(true));
 *                                               }
 *                                          });
 *       ...
 * </pre>
 *
 * @pending name is not good.  It should be  MouseProcessor  like
 */
public interface AbstractPropertyInspector
{
    /**
     * Find Property object at specified point.
     *
     * @param pt A point of mouse cursor location.
     * @return Property at specified Point.
     */
    Property getProperty( Point pt );

    /**
     * Determines a rectangle for the cell at specified point.
     *
     * @param pt a point of mouse cursor location.
     * @return a rectangle for the cell at specified Point.
     */
    Rectangle getCellRect(Point pt);

    /**
     * Clears a PropertyInspector.
     */
    public void clear();

    /**
     * Adds the specified mouse listener to receive mouse events from Property Inspector.
     *
     * @param listener the mouse listener.
     */
    void addMouseListener( MouseListener listener );

    /**
     * Removes the specified mouse listener so that it no longer receives mouse events from Property Inspector.
     *
     * @param listener the mouse listener
     */
    void removeMouseListener( MouseListener listener );

    /**
     * Adds the specified mouse motion listener to receive mouse motion events from Property Inspector.
     *
     * @param listener the mouse motion listener
     */
    void addMouseMotionListener( MouseMotionListener listener );

    /**
     * Removes the specified mouse motion listener so that it no longer receives mouse motion events from Property Inspector.
     *
     * @param listener the mouse motion listener
     */
    void removeMouseMotionListener( MouseMotionListener listener );

}
