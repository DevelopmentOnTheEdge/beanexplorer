package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;

public interface PropertyEditorEx extends PropertyEditor
{
    
    /**
     * Gets a bean - owner of the property for which Property Inspector invokes this editor
     * 
     * @return the current bean 
     */
    public Object getBean();

    /**
     * Sets a bean - owner of the property for which Property Inspector invokes this editor
     * Thsi method allows the developer to access the bean
     * from the code that related to the properties
     * 
     * @param bean  the current bean
     */
    public void setBean(Object bean);

    public FeatureDescriptor getDescriptor();
    public void setDescriptor( FeatureDescriptor descriptor );

    /**
     * This method returns a Component that edits value of the property.
     * 
     * @param parent the Component representing Property Inspector that has requested the editor. 
     * @param isSelected  true if the cell is to be highlighted
     * @return editor Component
     */
    public Component getCustomEditor(Component parent, boolean isSelected);

    /**
     * This method returns a Component that renders value of the property.
     * Component is used for drawing only and will never obtain focus. Use {@link #getValue()} method to get current property value.
     * 
     * @param parent the Component representing Property Inspector that has requested a editor 
     * @param isSelected  true if the cell is to be highlighted
     * @param hasFocus    true if the cell has focus.
     * @return rendered Component
     */
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus);
}
