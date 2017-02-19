package com.developmentontheedge.beans;

import java.awt.Image;

/**
 *  This class represents image tied with resource name. Objects of this
 * class can be assigned to
 * {@link BeanInfoConstants#NODE_ICON_COLOR_16x16},
 * {@link BeanInfoConstants#NODE_ICON_COLOR_32x32},
 * {@link BeanInfoConstants#NODE_ICON_MONO_16x16},
 * {@link BeanInfoConstants#NODE_ICON_MONO_32x32},
 * attributes.
 */
public class IconResource
{
    /** This constant indicates that resource can't be found. */
    static public final String RESOURCE_NOT_FOUND = "Resource not found.";

    /**
     * @param resourceName Name of resource.
     * @param image Image.
     */
    public IconResource( String resourceName,Image image )
    {
        this.resourceName   = resourceName;
        this.image = image;
    }

    /** Name of this resource */
    private String resourceName;
    /**
     * Returns name of this resource.
     * @return Name of this resource.
     */
    public  String getResourceName()
    {
        return resourceName;
    }
    /**
     * Set resource name for this resource.
     * @param name Name of resource.
     */
    public  void setResourceName(String name)
    {
        resourceName = name;
    }

    /** Image of this resource */
    private final Image image;
    /**
     * Returns image of this resource.
     * @return Image of this resource.
     */
    public Image getImage()
    {
        return image;
    }
}