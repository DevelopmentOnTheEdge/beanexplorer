package com.developmentontheedge.beans;

import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.developmentontheedge.beans.log.Logger;

/**
 * Default implementation of DynamicPropertySet
 *
 * @see DynamicPropertySet
 */
public class DynamicPropertySetSupport extends AbstractDynamicPropertySet
{
    private static final long serialVersionUID = 1L;
	/**
     * Vector of properties.
     */
    protected ArrayList<DynamicProperty> properties = new ArrayList<>();
    protected boolean useAddIndexes;
    protected Map<String, DynamicProperty> propHash = 
        new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
    protected Map<String, DynamicProperty> propDisplayNameHash;
 
    protected boolean bAddAlways = false;

    public DynamicPropertySetSupport()
    {
        this(true);
    }

    public DynamicPropertySetSupport( DynamicPropertySet dps, boolean useAddIndexes )
    {
        this( useAddIndexes );
        dps.forEach( dp ->
        {
            try
            {
                add( cloneProperty( dp ) );
            }
            catch( Exception wierd )
            {
                Logger.getLogger().error( "Unable to clone property " + dp.getName() + ", message = " + wierd.getMessage() );
            }
        } );
    }

    public DynamicPropertySetSupport( DynamicPropertySet dps )
    {
        this( dps, dps instanceof DynamicPropertySetSupport && ( ( DynamicPropertySetSupport )dps ).isUseAddIndexes() );
    }

    public DynamicPropertySetSupport( Map<String,?> map )
    {
        this( true );
        map.entrySet().stream().forEach( e -> {
            build( e.getKey(), e.getValue() != null ? e.getValue().getClass() : String.class ).value( e.getValue() );
        } );
    }

    public DynamicPropertySetSupport( boolean useAddIndexes )
    {
        this.useAddIndexes = useAddIndexes;
        if( useAddIndexes )
        {
            propDisplayNameHash = new HashMap<>();
        }
    }
    
    public boolean isUseAddIndexes()
    {
        return this.useAddIndexes;
    }

    /**
     * Find the property descriptor by its name.
     * NOTE!!!! findProperty MUST only use hashes - no other actions!
     */
    @Override
	protected DynamicProperty findProperty( String name )
    {
        DynamicProperty ret = propHash.get( name );
        if( ret == null && useAddIndexes )
        {
            ret = propDisplayNameHash.get( name );
        }
        return ret;
    }

    @Override
	public void renameProperty( String from, String to )
    {
        DynamicProperty property = findProperty( from );
        if( property == null )
        {
            return;
        }
        property.setName( to );
        propHash.put( to, property );
        propHash.remove( from );
    }

    protected final void addProperty( DynamicProperty property )
    {
        addProperty( properties.size(), property );
    }

    protected final void addProperty( int index, DynamicProperty property )
    {
        String pname = property.getName();
        if( !bAddAlways && propHash.get( pname ) != null )
        {
            replaceWith( pname, property );
            return;
        }

        properties.add( index, property );
        propHash.put( pname, property );
        if( useAddIndexes )
        {
            propDisplayNameHash.put( property.getDisplayName(), property );
        }
        property.setParent( this );
    }

    /**
     * Adds the specified property.
     */
    @Override
	public void add( DynamicProperty property )
    {
        addProperty( property );
    }

    /**
     * Adds the specified property to the specified index.
     */
    public void add( int index, DynamicProperty property )
    {
        addProperty( index, property );
    }

    @Override
	public boolean addBefore( String propName, DynamicProperty property )
    {
        DynamicProperty beforeProperty = findProperty( propName );
        int ind = properties.indexOf( beforeProperty );
        if( ind >= 0 )
        {
            add( ind, property );
            return true;
        }
        else
        {
            add( property );
            return false;
        }
    }

    @Override
	public boolean addAfter( String propName, DynamicProperty property )
    {
        DynamicProperty afterProperty = findProperty( propName );
        int ind = properties.indexOf( afterProperty );
        if( ind >= 0 )
        {
            add( ind + 1, property );
            return true;
        }
        else
        {
            add( property );
            return false;
        }
    }

    public DynamicPropertyBuilder build( String name, Class<?> clazz )
    {
        DynamicPropertyBuilder builder = new DynamicPropertyBuilder( name, clazz );
        add( builder.get() );
        return builder; 
    }

    /**
     * Checks if property does exist in this set
     */
    @Override
	public boolean contains( DynamicProperty property )
    {
        return properties.contains( property );
    }

    @Override
	public Object remove( String name )
    {
        DynamicProperty property = findProperty( name );
        if( properties.remove( property ) )
        {
            Object retValue = property.getValue();
            propHash.remove( property.getName() );
            if( useAddIndexes )
            {
                propDisplayNameHash.remove( property.getDisplayName() );
            }
            property.setParent( null );
            return retValue;
        }
        return null;
    }

    @Override
	public boolean moveTo( String name, int index )
    {
        if( index > properties.size() - 1 )
        {
            return false;
        }
        DynamicProperty property = findProperty( name );
        int oldIndex = properties.indexOf( property );
        if( oldIndex == -1 )
        {
            return false;
        }
        if( oldIndex == index )
        {
            return true;
        }
        properties.remove( oldIndex );
        properties.add( index, property );
        return true;
    }

    public boolean moveBefore( String name, String nameBefore )
    {
        for( int i = 0; i < properties.size(); i++ )
        {
            if( nameBefore.equalsIgnoreCase( properties.get( i ).getName() ) )
            {
                return moveTo( name, i );
            }
        }
        return false;
    }

    public boolean moveAfter( String name, String nameAfter )
    {
        for( int i = 0; i < properties.size(); i++ )
        {
            if( nameAfter.equalsIgnoreCase( properties.get( i ).getName() ) )
            {
                return moveTo( name, i + 1 );
            }
        }
        return false;
    }

    @Override
	public boolean replaceWith( String name, DynamicProperty prop )
    {
        DynamicProperty old = findProperty( name );
        if( old == null )
        {
            return false;
        }
        int index = properties.indexOf( old );
        if( index == -1 )
        {
            return false;
        }

        if( useAddIndexes )
        {
            propDisplayNameHash.remove( old.getDisplayName() );
        }
        properties.set( index, prop );
        propHash.put( prop.getName(), prop );
        if( useAddIndexes )
        {
            propDisplayNameHash.put( prop.getDisplayName(), prop );
        }

        return true;
    }

    /**
     * Creates new dynamic bean containing properties present in <i>sample</i>
     * but missing in this bean
     */
    public DynamicPropertySet diff( DynamicPropertySet sample )
    {
        DynamicPropertySetSupport diff = new DynamicPropertySetSupport();
        boolean isEmpty = true;
        for( Iterator<DynamicProperty> entries = sample.propertyIterator(); entries.hasNext(); )
        {
            DynamicProperty prop = entries.next();
            if( propHash.containsKey( prop.getName() ) )
            {
                continue;
            }
            isEmpty = false;
            if( prop.hasDescriptor() )
            {
                diff.add( new DynamicProperty( prop.getDescriptor(), prop.getType(), prop.getValue() ) );
            }
            else
            {
                DynamicProperty newProp = new DynamicProperty( prop.getName(), prop.getType(), prop.getValue() ); 
                newProp.setDisplayName( prop.getDisplayName() );
                newProp.setShortDescription( prop.getShortDescription() );
                newProp.setExpert( prop.isExpert() );
                newProp.setHidden( prop.isHidden() );
                Enumeration<String> attributeNames = prop.attributeNames();
                while( attributeNames.hasMoreElements() )
                {
                    String attribute = attributeNames.nextElement();
                    newProp.setAttribute( attribute, prop.getAttribute( attribute ) );
                }

                diff.add( newProp );
            } 
        }
        if( isEmpty )
        {
            return null;
        }
        return diff;
    }

    public static DynamicProperty cloneProperty( DynamicProperty prop )
        throws java.beans.IntrospectionException
    {
        if( prop.hasDescriptor() )
        { 
            PropertyDescriptor propertyDescriptor = prop.getDescriptor();
            PropertyDescriptor result = new PropertyDescriptor( propertyDescriptor.getName(),
                propertyDescriptor.getReadMethod(),
                propertyDescriptor.getWriteMethod() );
            result.setBound( propertyDescriptor.isBound() );
            result.setConstrained( propertyDescriptor.isConstrained() );
            result.setExpert( propertyDescriptor.isExpert() );
            result.setHidden( propertyDescriptor.isHidden() );
            result.setPreferred( propertyDescriptor.isPreferred() );

            if( !propertyDescriptor.getName().equals( propertyDescriptor.getDisplayName() ) )
            {
                result.setDisplayName( propertyDescriptor.getDisplayName() );
            }

            if( !propertyDescriptor.getDisplayName().equals( propertyDescriptor.getShortDescription() ) )
            {
                result.setShortDescription( propertyDescriptor.getShortDescription() );
            }

            result.setPropertyEditorClass( propertyDescriptor.getPropertyEditorClass() );
            Enumeration<String> e = propertyDescriptor.attributeNames();
            while ( e.hasMoreElements() )
            {
                String atr = e.nextElement();
                result.setValue( atr, propertyDescriptor.getValue( atr ) );
            }

            if( prop.getValue() instanceof DynamicPropertySetSupport )
            {
                DynamicProperty newProp = new DynamicProperty( result, prop.getType(), 
                   ( ( DynamicPropertySetSupport )prop.getValue() ).clone() );
                return newProp;  
            }
            else
            {
                DynamicProperty newProp = new DynamicProperty( result, prop.getType(), prop.getValue() );
                return newProp;  
            }
        }
        else
        {
            DynamicProperty newProp = null; 
            if( prop.getValue() instanceof DynamicPropertySetSupport )
            {
                newProp = new DynamicProperty( prop.getName(), prop.getType(), 
                   ( ( DynamicPropertySetSupport )prop.getValue() ).clone() );
            }
            else
            {
                newProp = new DynamicProperty( prop.getName(), prop.getType(), prop.getValue() );
            }

            newProp.setHidden( prop.isHidden() );
            newProp.setExpert( prop.isExpert() );
            newProp.setDisplayName( prop.getDisplayName() );
            newProp.setShortDescription( prop.getShortDescription() );
            Enumeration<String> attributeNames = prop.attributeNames();
            while( attributeNames.hasMoreElements() )
            {
                String attribute = attributeNames.nextElement();
                newProp.setAttribute( attribute, prop.getAttribute( attribute ) );
            }

            return newProp;  
        }
    }

    @Override
	public Object clone()
    {
        DynamicPropertySetSupport retVal = (DynamicPropertySetSupport)super.clone();
        retVal.pcSupport = null;
        retVal.properties = new ArrayList<>();
        retVal.propHash = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
        if(retVal.useAddIndexes)
        {
            retVal.propDisplayNameHash = new HashMap<>();
        }
        for( Iterator<DynamicProperty> iter = propertyIterator(); iter.hasNext(); )
        {
            DynamicProperty prop = iter.next();

            try
            {
                retVal.add( cloneProperty( prop ) );
            }
            catch( java.beans.IntrospectionException wierd )
            {
                Logger.getLogger().error( "Unable to clone property " + prop.getName(), wierd );
            }
        }
        return retVal;
    }

    @Override
	public Iterator<String> nameIterator()
    {
        return new Iterator<String>()
        {
            int current = 0;

            @Override
			public boolean hasNext()
            {
                return current < properties.size();
            }

            @Override
			public String next()
            {
                return ( properties.get( current++ ) ).getName();
            }

            @Override
			public void remove()
            {
                throw new UnsupportedOperationException(
                        "DynamicPropertySetSupport.nameItearator() does not support remove()" );
            }
        };
    }

    @Override
	public Iterator<DynamicProperty> propertyIterator()
    {
        return properties.iterator();
    }

    @Override
    public Spliterator<DynamicProperty> spliterator()
    {
        return properties.spliterator();
    }

    @Override
	public Map<String, Object> asMap()
    {
        HashMap<String, Object> viewMap = new HashMap<>( properties.size() );
        for( Iterator<Map.Entry<String, DynamicProperty>> entries = propHash.entrySet().iterator(); entries.hasNext(); )
        {
            Map.Entry<String, DynamicProperty> entry = entries.next();
            DynamicProperty prop = entry.getValue();
            viewMap.put( entry.getKey(), prop.getValue() );
        }

        return Collections.unmodifiableMap( viewMap );
    }

    public void setAttributeToAllChildren( String attrName, Object attrValue )
        throws Exception
    {
        for( DynamicProperty prop : this )
        {
            prop.setAttribute( attrName, attrValue );
            prop.setAttributeToAllChildren( attrName, attrValue );
        } 
    }

    public void setCanBeNullToAllChildren( boolean isNull )
    {
        for( DynamicProperty prop : this )
        {
            prop.setCanBeNull( isNull );

            Object value = prop.getValue();

            if( value instanceof DynamicPropertySetSupport )
            {
                ( ( DynamicPropertySetSupport )value ).setCanBeNullToAllChildren( isNull );
            }
            else if( value instanceof DynamicPropertySetSupport[] )
            {
                for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
                {
                    propBean.setCanBeNullToAllChildren( isNull );
                }
            }
        }
    }

    public void setExpertToAllChildren( boolean expert )
        throws Exception
    {
        for( DynamicProperty prop : this )
        {
            prop.setExpert( expert );

            Object value = prop.getValue();

            if( value instanceof DynamicPropertySetSupport )
            {
                ( ( DynamicPropertySetSupport )value ).setExpertToAllChildren( expert );
            }
            else if( value instanceof DynamicPropertySetSupport[] )
            {
                for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
                {
                     propBean.setExpertToAllChildren( expert );
                }
            }
        } 
    }

    public void setReadOnlyToAllChildren( boolean expert )
        throws Exception
    {
        for( DynamicProperty prop : this )
        {
            prop.setReadOnly( expert );

            Object value = prop.getValue();

            if( value instanceof DynamicPropertySetSupport )
            {
                ( ( DynamicPropertySetSupport )value ).setReadOnlyToAllChildren( expert );
            }
            else if( value instanceof DynamicPropertySetSupport[] )
            {
                for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
                {
                     propBean.setReadOnlyToAllChildren( expert );
                }
            }
        } 
    }

    @Override
	public String toString()
    {
        StringWriter out = new StringWriter();
        printProperties( out, "" );
        return "DPS(" + getClass().getName() + "):" + out.toString();
    }

    protected void printProperties( Writer out, String offset )
    {
        try
        {
            for( int i = 0; i < properties.size(); i++ )
            {
                out.write( "\n  " + offset + ( i + 1 ) + ". " );

                DynamicProperty dp = properties.get( i );
                String valStr = "";
                if( dp.getValue() instanceof DynamicPropertySetSupport ) 
                {
                    StringWriter rout = new StringWriter();
                    ( ( DynamicPropertySetSupport )dp.getValue() ).printProperties( rout, "   " + offset );
                    valStr = "DPS:" + rout.toString();
                }
                else if( dp.getValue() instanceof DynamicPropertySetSupport[] ) 
                {
                    int ind = 0; 
                    StringWriter rout = new StringWriter();
                    for( DynamicPropertySetSupport arrBean : ( DynamicPropertySetSupport[] )dp.getValue() )
                    {
                        rout.write( "\n     " + offset + "[" + ( ind++ ) + "]:" );
                        arrBean.printProperties( rout, "      " + offset );
                    }
                    valStr = "Array:" + rout.toString();
                }
                else if( dp.getValue() instanceof DynamicPropertySet[] ) 
                {
                    int ind = 0; 
                    StringWriter rout = new StringWriter();
                    for( DynamicPropertySet arrBean : ( DynamicPropertySet[] )dp.getValue() )
                    {
                        rout.write( "\n     " + offset + "[" + ( ind++ ) + "]:" );
                        ((DynamicPropertySetSupport)arrBean).printProperties( rout, "      " + offset );
                    }
                    valStr = "Array:" + rout.toString();
                }
                else if( dp.getAttribute( BeanInfoConstants.ARRAY_ITEM_TEMPLATE ) instanceof DynamicPropertySetSupport ) 
                {
                    DynamicPropertySetSupport arrBean = ( DynamicPropertySetSupport )dp.getAttribute( BeanInfoConstants.ARRAY_ITEM_TEMPLATE );
                    StringWriter rout = new StringWriter();
                    rout.write( "\n     " + offset + "[]:" );
                    dp.getAttribute( BeanInfoConstants.ARRAY_ITEM_TEMPLATE );
                    arrBean.printProperties( rout, "      " + offset );
                    valStr = "Array:" + rout.toString();
                }
                else if( dp.getValue() instanceof Object[] ) 
                {
                    valStr = Arrays.asList( ( Object[] )dp.getValue() ).toString();
                }
                else
                {
                    valStr = "" + dp.getValue();
                }
                out.write( dp.getName() + " (" + dp.getType() + ") - " + valStr );
            }
        }
        catch( Exception e )
        {
        }
    }

    @Override
	public int size()
    {
        return properties.size();
    }

    public DynamicPropertySetSupport skipLevel()
    {
        for( DynamicProperty prop : this )
        {
            return ( DynamicPropertySetSupport )prop.getValue();
        }
        return null;
    }

    private final static String[] dictionaryWords = {
            "id", "phone", "number", "background", "name", "value", "column", "description", "code",
            "type", "size", "begin", "end", "price", "status", 
            "border", "borders", "rows", "link", "text", "header"
    };

    /**
     * Tries to represent column name as some text label for column:
     *
     * <br/><br/>1. Column name may contain "___":
     * then "___" will be removed.
     *
     * <br/><br/>2. Each group of lowercase symbols, started with uppercase symbols, comprehends as word, and this group separated from other
     * group with spaces. Example: "groupCombineInformation" will be converted to "group Combine Information"
     *
     * <br/><br/>3. Tries to find words, delimited by '_' symbol. All of '_' symbols exchanged for spaces, and each resulting word will be
     * rewritten with first uppercase letter.
     *
     * <br/><br/>If second and third case was ineffective, then this method looks if some of words, specified below, ends the
     * string, and then founded word will be separated with the space:
     *
     * <br/><br/>"id", "phone", "number", "background", "name", "value", "column", "description", "code", "type", "size", "begin", "end", "price"
     *
     * @param colName column Name
     * @return formatted text label
     */

    public static String makeBetterDisplayName( String colName )
    {
        if( colName == null || colName.length() < 1 )
        {
            return colName;
        }

        StringBuffer label = new StringBuffer( "" );
        if( Character.isLowerCase( colName.charAt( 0 ) ) ||
                colName.length() > 1 && Character.isUpperCase( colName.charAt( 0 ) ) && Character.isLowerCase( colName.charAt( 1 ) ) )
        {
            label.append( Character.toUpperCase( colName.charAt( 0 ) ) );
            int size = colName.length();
            for( int j = 1; j < size; j++ )
            {
                char cur = colName.charAt( j );
                if( Character.isUpperCase( cur ) && Character.isLowerCase( colName.charAt( j - 1 ) ) )
                {
                    label.append( ' ' );
                    label.append( cur );
                }
                else
                {
                    label.append( cur );
                }
            }
        }

        StringTokenizer st = new StringTokenizer( label.length() > 0 ? label.toString() : colName, "_" );
        if( st.countTokens() > 1 )
        {
            if( label.length() > 0 )
            {
                label = new StringBuffer();
            }
            boolean isFirst = true;
            while( st.hasMoreTokens() )
            {
                String word = st.nextToken();
                if( "".equals( word ) )
                {
                    continue;
                }
                if( !isFirst )
                {
                    label.append( ' ' );
                }
                word = word.toLowerCase();
                label.append( Character.toUpperCase( word.charAt( 0 ) ) );
                label.append( word.substring( 1 ) );
                isFirst = false;
            }
        }
        else if( label.length() == 0 || 
                 colName.equals( colName.toLowerCase() ) && label.toString().indexOf( " " ) < 0 )
        {
            label.setLength( 0 ); 
            label.append( Character.toUpperCase( colName.charAt( 0 ) ) );
            label.append( colName.substring( 1 ).toLowerCase() );
            int labLen = label.length();
            for( String dictWord : dictionaryWords )
            {
                if( labLen <= dictWord.length() )
                {
                    continue;
                }
                if( !label.toString().endsWith( dictWord ) )
                {
                    continue;
                }
                int diff = labLen - dictWord.length();
                label.setCharAt( diff, Character.toUpperCase( label.charAt( diff ) ) );
                label.insert( diff, ' ' );
                break;
            }
        }

        return label.toString();
    }

    private Object removeFromMap( Map map, Object element )
    {
        if( map.containsKey( element ) )
        {
            return map.remove( element );
        }
        else
        {
            return null;
        }
    }

    private String asString( Object o )
    {
        return o != null ? o.toString() : null;
    }

    static final List<String> beanInfoConstants = new ArrayList<>();
    static {
        Field[] fields = BeanInfoConstants.class.getDeclaredFields();
        for (Field f : fields)
        {
            if (Modifier.isStatic(f.getModifiers())) {
                beanInfoConstants.add(f.getName());//f.get(null).toString()
            }
        }
    }
}
