/** $Id: SimpleProperty.java,v 1.10 2001/03/23 10:06:16 zha Exp $ */

package com.developmentontheedge.beans.model;

import java.beans.PropertyDescriptor;

/**
 * @author Fedor A. Kolpakov
 * @version $Revision: 1.10 $
 */
public class SimpleProperty extends Property
    {
    /** Creates a new SimpleProperty. In general, this function must be called by ComponentFactory */
    protected SimpleProperty( Property parent, Object owner, PropertyDescriptor descriptor ) 
        {
        super( parent, owner, descriptor );
        }

    ////////////////////////////////////////
    // implemeents TreeNode interface
    //
    public boolean isLeaf()
        {
        return true;
        }

    public Property getPropertyAt( int i )
        {
        return null;
        }

    public int getPropertyCount()
        {
        return 0;
        }

    public Property findProperty( String name )
        {
        return null;
        }

    public String toString()
        {
        return getDisplayName();
        }
    }
