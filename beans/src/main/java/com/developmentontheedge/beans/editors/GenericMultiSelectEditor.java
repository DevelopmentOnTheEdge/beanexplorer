package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.SystemColor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * MultiSelect which can contain different sets of values
 */
public class GenericMultiSelectEditor extends CustomEditorSupport
{
    private JButton button = null;
    private MultiSelectPopup popup;
    
    /**
     * Creates Editor or Renderer component
     * 
     * @param parent
     * @param isSelected
     * @return Editor or Renderer component
     */
    private Component createComponent(Component parent, boolean isSelected)
    {
        button = new JButton("");
        button.setHorizontalAlignment(SwingConstants.LEFT);

        popup = new MultiSelectPopup(button);
        
        GenericMultiSelectItem value = getValueItem();
        if(value == null) return button;
        
        final String key = value.getKey();
        Object[] vals = value.getAvailableValues();
        if(vals == null) return button;
        popup.getList().setListData(vals);

        popup.getList().setSelectedIndices(value.getIndexes());
        updateButtonText();

        popup.getList().addListSelectionListener(event -> doSet(key));

        return button;
    }
    
    /**
     * Returns text describing current selection
     */
    protected String getText(List<Object> vals)
    {
        StringBuffer result = new StringBuffer();
        if(vals.isEmpty())
        {
            return "(no selection)";
        }
        if(vals.size()>1)
        {
            result.append( "[" + ( String.valueOf( vals.size() ) ) + "] " );
        }
        for(int i=0; i<vals.size(); i++)
        {
            if(i>0) result.append(", ");
            result.append(vals.get( i ).toString());
            if(result.length() > 100) break;
        }
        return result.toString();
    }
    
    private void updateButtonText()
    {
        button.setText(getText(popup.getList().getSelectedValuesList()));
    }

    private void doSet(String key)
    {
        setValue( new GenericMultiSelectItem( key, popup.getList().getSelectedValuesList().toArray(), getValueItem()
                .getAvailableValues() ).getValues() );
        updateButtonText();
    }
    
    public void setStringValue(String[] val)
    {
        GenericMultiSelectItem value = getValueItem();
        setValue(new GenericMultiSelectItem(value.getKey(), val, value.getAvailableValues()).getValues());
    }
    
    protected Object[] getAvailableValues()
    {
        return null;
    }
    
    private GenericMultiSelectItem getValueItem()
    {
        Object valueObj = getValue();
        if(valueObj == null) valueObj = new Object[]{};
        GenericMultiSelectItem value = null;
        if(valueObj instanceof Object[])
        {
            Object[] valuesArray = (Object[])valueObj;
            String key = (String)getDescriptor().getValue("key");
            if(key == null)
                key = "";
            
            Object[] availVals = getAvailableValues();
            if( availVals == null )
                throw new IllegalStateException( "No vals" );
            value = new GenericMultiSelectItem(key, valuesArray, availVals);
        }
        return value;
    }

    @Override
    public String[] getTags()
    {
        GenericMultiSelectItem value = getValueItem();
        if(value != null)
        {
            return Stream.of( value.getAvailableValues() ).map( Object::toString ).toArray( String[]::new );
        }
        return null;
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        return createComponent(parent, isSelected);
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        return createComponent(parent, isSelected);
    }
    
    @SuppressWarnings ( "serial" )
    private static class MultiSelectPopup extends JPopupMenu
    {
        private JList<Object> listBox;
        private JScrollPane scrollPane;
        private Insets insets;
        private JButton button;
        // Used to correctly handle click on button, when popup is opened
        // TODO: implement more clean solution
        private static long deactivationTime = 0;

        public MultiSelectPopup(JButton button)
        {
            this.button = button;
            listBox = new JList<>();
            listBox.setBackground(SystemColor.control);
            listBox.setVisibleRowCount(20);
            scrollPane = new JScrollPane(listBox);
            scrollPane.setBorder(null);
            scrollPane.setFocusable(false);
            scrollPane.getVerticalScrollBar().setFocusable(false);
            setOpaque(false);
            add(scrollPane);
            setFocusable(false);
            setBorder(new BevelBorder(BevelBorder.LOWERED));
            insets = getBorder().getBorderInsets(this);
            addPopupMenuListener(new PopupMenuListener()
            {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
                {
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
                {
                    deactivationTime = System.currentTimeMillis();
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent arg0)
                {
                }
            });
            button.addActionListener(ae -> {
                if( isVisible() || System.currentTimeMillis() - deactivationTime < 200 )
                    return;
                show();
            });
        }

        public JList<Object> getList()
        {
            return listBox;
        }

        @Override
        public void show()
        {
            if( listBox.getPreferredSize() == null || System.currentTimeMillis() - deactivationTime < 200 )
                return;
            setPreferredSize(new Dimension(button.getWidth(), Math.min(listBox.getPreferredScrollableViewportSize().height,
                    listBox.getPreferredSize().height)
                    + insets.top + insets.bottom));
            show(button, 0, button.getHeight());
            listBox.requestFocus();
        }

        @Override
        public void hide()
        {
            setVisible(false);
        }
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        setStringValue(new String[] {text});
    }
    
    private static class GenericMultiSelectItem
    {
        private String key = null;
        private Object[] availableValues;
        private Object[] values;
        private int[] indexes;
        
        protected GenericMultiSelectItem(String key, Object[] values, Object[] availableValues)
        {
            this.availableValues = availableValues;
            if(availableValues == null) return;
            this.key = key;
            if(values == null) return;

            selectValues( values );
        }
        
        protected String getKey()
        {
            return key;
        }
        
        protected int[] getIndexes()
        {
            return indexes;
        }
        
        public Object[] getAvailableValues()
        {
            return availableValues;
        }
        
        public Object[] getValues()
        {
            if( availableValues == null )
                return new Object[] {};
            return values;
        }
        
        @Override
        public int hashCode()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o)
        {
            if(o == null || !(o instanceof GenericMultiSelectItem)) return false;
            GenericMultiSelectItem item = (GenericMultiSelectItem)o;
            if((key == null && item.key != null) || (key != null && !key.equals(item.key))) return false;
            if(!Arrays.equals( indexes, item.indexes )) return false;
            if(!Arrays.equals(availableValues, item.availableValues)) return false;
            return true;
        }
        
        private void selectValues(Object[] values)
        {
            if(availableValues == null) return;

            Map<String, Integer> valToIdx = new HashMap<>();
            for(int i = 0; i < availableValues.length; i++)
                valToIdx.put( availableValues[i].toString(), i );

            this.indexes = Stream.of( values ).map( val -> valToIdx.get( val.toString() ) ).filter( Objects::nonNull )
                    .mapToInt( Integer::intValue ).toArray();
            
            this.values = (Object[])Array.newInstance( availableValues.getClass().getComponentType(), this.indexes.length );
            for(int i=0; i<this.indexes.length; i++)
            {
                this.values[i] = availableValues[this.indexes[i]];
            }
        }
    }
}