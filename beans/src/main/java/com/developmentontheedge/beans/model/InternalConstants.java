/** $Id: InternalConstants.java,v 1.2 2001/08/04 16:25:59 zha Exp $ */
package com.developmentontheedge.beans.model;

/**
 * Constants for internal usage only.
 *
 * The problem results from interaction of <code>PropertyWrapper</code> with
 * <code>ComponentFactory.merge</code> function.
 */
public interface InternalConstants
{
    /** Stores reference to original <code>BeanDescriptor</code>. */
    String BEAN_DESCRIPTOR = "bean descriptor";

    /** Stores reference to original <code>PropertyDescriptor</code>. */
    String PROPERTY_DESCRIPTOR = "property descriptor";

    /**
     * Stores reference to bean editor class.
     * <code>BeanDescriptor</code> has only <code>getCustomiserClass</code>
     * but has not <code>setCustomiserClass</code> method.
     */
    String BEAN_EDITOR_CLASS = "bean-editor-class";

    /**
     * This attribute is used internally by array properties
     * to store an index of element
     */
    String CHILD_INDEX = "childIndex";

}


