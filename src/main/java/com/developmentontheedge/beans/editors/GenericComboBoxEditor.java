package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * ComboBox which can contain different sets of values
 * It's implementation is non-trivial due to difficulty of passing to BE Editor any information except class name
 * This problem was worked around by passing additional information in the value
 */
public class GenericComboBoxEditor extends CustomEditorSupport
{
    private JComboBox<String> comboBox = new JComboBox<>();

    /**
     * Creates Editor or Renderer component
     * 
     * @param parent
     * @param isSelected
     * @return Editor or Renderer component
     */
    private Component createComponent(Component parent, boolean isSelected)
    {
        comboBox = new JComboBox<>();

        GenericComboBoxItem value = getValueItem();
        if( value == null )
            return comboBox;

        String[] vals = getTags();
        if( vals == null )
            return comboBox;
        for( String str : vals )
            comboBox.addItem( str );

        comboBox.setSelectedItem( value.toString() );

        final String key = value.getKey();
        comboBox.addActionListener( ae -> doSet( key ) );

        return comboBox;
    }

    private GenericComboBoxItem getValueItem()
    {
        Object valueObj = getValue();
        if( valueObj == null )
            return null;
        Object valueStr = valueObj;
        String key = (String)getDescriptor().getValue( "key" );
        if( key == null )
            key = "";

        Object[] availVals = getAvailableValues();
        GenericComboBoxItem value = null;
        if( availVals == null )
        {
            Object[] tagList = (Object[])getDescriptor().getValue( "tagList" );
            if( tagList == null )
                throw new IllegalStateException( "TagList is null" );
            value = new GenericComboBoxItem( key, valueStr, tagList );
        }
        else
            value = new GenericComboBoxItem( key, valueStr, availVals );
        return value;
    }

    @Override
    public void setAsText(String newValue) throws IllegalArgumentException
    {
        GenericComboBoxItem newValueItem = null;
        GenericComboBoxItem valueItem = getValueItem();
        Object[] availableValues = valueItem == null ? getAvailableValues() : valueItem.getAvailableValues();
        for( Object value : availableValues )
        {
            if( value.toString().equals( newValue ) )
                newValueItem = new GenericComboBoxItem( valueItem == null ? null : valueItem.getKey(), newValue, availableValues );
        }
        if( newValueItem != null )
        {
            setValue( newValueItem.getValue() );
        }
    }

    @Override
    public String[] getTags()
    {
        Object[] vals = null;

        GenericComboBoxItem value = getValueItem();
        if( value != null )
        {
            if( value.getAvailableValues() != null )
                getDescriptor().setValue( "tagList", value.getAvailableValues() );
            vals = value.getAvailableValues();
        }
        else
            vals = getAvailableValues();

        if( vals == null )
            return null;

        return Stream.of( vals ).map( Object::toString ).toArray( String[]::new );
    }

    protected Object[] getAvailableValues()
    {
        return null;
    }

    private void doSet(String key)
    {
        setValue( new GenericComboBoxItem( key, comboBox.getSelectedItem().toString(), getValueItem().getAvailableValues() ).getValue() );
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        GenericComboBoxItem item = getValueItem();
        JLabel label = new JLabel( item == null ? "" : item.toString() );
        return label;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        return createComponent( parent, isSelected );
    }
    
    /**
     * Item in GenericComboBoxEditor
     */
    static class GenericComboBoxItem
    {
        private String key = null;
        private int index = 0;
        private Object value = "";
        private Object[] availableValues;
        
        protected GenericComboBoxItem(String key, Object value, Object[] availableValues)
        {
            this.availableValues = availableValues;
            this.key = key;
            if(availableValues == null || availableValues.length == 0) return;
            if(value != null)
            {
                for(int i=0; i<availableValues.length; i++)
                {
                    if(availableValues[i].toString().equals(value.toString()))
                    {
                        this.value = availableValues[i];
                        this.index = i;
                        return;
                    }
                }
            }
            this.value = availableValues[0];
        }
        
        protected String getKey()
        {
            return key;
        }
        
        protected int getIndex()
        {
            return index;
        }
        
        public Object[] getAvailableValues()
        {
            return availableValues;
        }
        
        public Object getValue()
        {
            return value;
        }
        
        @Override
        public String toString()
        {
            return getValue().toString();
        }
        
        @Override
        public int hashCode()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o)
        {
            if(o == null || !(o instanceof GenericComboBoxItem)) return false;
            GenericComboBoxItem item = (GenericComboBoxItem)o;
            if(item.index != index) return false;
            if((value == null && item.value != null) || (value != null && !value.equals(item.value))) return false;
            if((key == null && item.key != null) || (key != null && !key.equals(item.key))) return false;
            return true;
        }
    }
}