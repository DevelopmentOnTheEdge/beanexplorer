package com.developmentontheedge.beans;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simplifies creation of dynamic properties (see example below).
 *
 * <pre>
 *
 * DynamicPropertySet parameters = new DynamicPropertySetSupport();
 *
 * boolean condition = false;
 * String[] tags = new String[] { "one", "two", "three" };
 * String value = "two";
 *
 * DynamicPropertyBuilder property = new DynamicPropertyBuilder( "propname", String.class );
 * property.title( "Some title" );
 * property.tags( tags );
 * property.value( value );
 * property.onchange( "this.form.submit()" );
 * property.onclick( "this.form.submit()" );
 * property.columnSize( 200 );
 * property.hidden( condition );
 * property.nullable();
 * property.readonly();
 *
 * parameters.add( property.get() );
 *
 * // or you can use method chaining:
 *
 * parameters.add( new DynamicPropertyBuilder( "propname", String.class )
 *      .title( "Some title" )
 *      .tags( tags )
 *      .value( value )
 *      .onchange( "this.form.submit()" )
 *      .onclick( "this.form.submit()" )
 *      .columnSize( 200 )
 *      .hidden( condition )
 *      .nullable()
 *      .readonly()
 *      .get() );
 *
 * </pre>
 *
 * @author Andrey Anisimov
 * @see com.developmentontheedge.beans.DynamicProperty
 */
public class DynamicPropertyBuilder
{
    private final DynamicProperty property;

    public DynamicPropertyBuilder( String name, Class<?> clazz )
    {
        property = new DynamicProperty( name, name, clazz );
    }

    DynamicPropertyBuilder( DynamicProperty prop )
    {
        property = prop;
    }

    public DynamicPropertyBuilder title( String title )
    {
        property.setDisplayName( title );
        return this;
    }

    public DynamicPropertyBuilder value( Object value )
    {
        property.setValue( value );
        return this;
    }

    public DynamicPropertyBuilder readonly( boolean condition )
    {
        property.setReadOnly( condition );
        return this;
    }

    public DynamicPropertyBuilder readonly()
    {
        return readonly( true );
    }

    public DynamicPropertyBuilder nullable( boolean condition )
    {
        property.setCanBeNull( condition );
        return this;
    }

    public DynamicPropertyBuilder nullable()
    {
        return nullable( true );
    }

    public DynamicPropertyBuilder hidden( boolean condition )
    {
        property.setHidden( condition );
        return this;
    }

    public DynamicPropertyBuilder hidden()
    {
        return hidden( true );
    }

    public DynamicPropertyBuilder attr( String name, Object value )
    {
        property.setAttribute( name, value );
        return this;
    }

    public DynamicPropertyBuilder attrs( Map<String,Object> vals )
    {
        for( String key : vals.keySet() )
        {
            property.setAttribute( key, vals.get( key ) );
        }
        return this;
    }

    public DynamicPropertyBuilder noServerCheck()
    {
        property.setAttribute( BeanInfoConstants.SKIP_SERVER_NULL_CHECK, true );
        return this;
    }

    public DynamicPropertyBuilder dynamicTags( Object tags )
    {
        return attr( BeanInfoConstants.TAG_LIST_ATTR, tags ).
               attr( BeanInfoConstants.DYNAMIC_TAG_LIST_ATTR, true );
    }

    public DynamicPropertyBuilder customEditor( Object editor )
    {
        return attr( BeanInfoConstants.CUSTOM_EDITOR_ATTR, editor );
    }

    public DynamicPropertyBuilder tags( Object tags )
    {
        return attr( BeanInfoConstants.TAG_LIST_ATTR, tags );
    }

    public DynamicPropertyBuilder extraAttrs( Map<String, String> attrs )
    {
        return attr( BeanInfoConstants.EXTRA_ATTRS, attrs );
    }

    public DynamicPropertyBuilder extraAttr( String name, String value )
    {
        @SuppressWarnings("unchecked")
		Map<String, String> attrs = ( Map<String, String> )property.getAttribute( BeanInfoConstants.EXTRA_ATTRS );
        if( null == attrs )
        {
            attrs = new LinkedHashMap<>();
        }
        attrs.put( name, value );
        return extraAttrs( attrs );
    }

    public DynamicPropertyBuilder onchange( String javascript )
    {
        return extraAttr( "onchange", javascript );
    }

    public DynamicPropertyBuilder onclick( String javascript )
    {
        return extraAttr( "onclick", javascript );
    }

    public DynamicPropertyBuilder placeholder( String text )
    {
        return extraAttr( "placeholder", text );
    }

    public DynamicPropertyBuilder reloadOnChange()
    {
        return attr( BeanInfoConstants.RELOAD_ON_CHANGE, true );
    }

    public DynamicPropertyBuilder reloadOnClick()
    {
        return attr( BeanInfoConstants.RELOAD_ON_CLICK, true );
    }

    public DynamicPropertyBuilder external( String tableFrom, String column, String tableTo )
    {
        return attr( BeanInfoConstants.EXTERNAL_TAG_LIST, tableTo )
              .attr( BeanInfoConstants.ORIG_PROPERTY_ENTITY_ATTR, tableFrom )
              .attr( BeanInfoConstants.ORIG_PROPERTY_NAME_ATTR, column );
    }

    public DynamicPropertyBuilder autocomplete( String tableFrom, String column, String tableTo )
    {
        return attr( BeanInfoConstants.EXTERNAL_TAG_LIST, tableTo )
              .attr( BeanInfoConstants.ORIG_PROPERTY_ENTITY_ATTR, tableFrom )
              .attr( BeanInfoConstants.ORIG_PROPERTY_NAME_ATTR, column )
              .attr( BeanInfoConstants.USE_AUTOCOMPLETE, true );
    }

    public DynamicPropertyBuilder select2( String tableFrom, String column, String tableTo )
    {
        return attr( BeanInfoConstants.EXTERNAL_TAG_LIST, tableTo )
              .attr( BeanInfoConstants.ORIG_PROPERTY_ENTITY_ATTR, tableFrom )
              .attr( BeanInfoConstants.ORIG_PROPERTY_NAME_ATTR, column )
              .attr( BeanInfoConstants.USE_SELECT2, true );
    }

    public DynamicPropertyBuilder select2()
    {
        return attr( BeanInfoConstants.USE_SELECT2, true );
    }

    public DynamicPropertyBuilder chosen()
    {
        return attr( BeanInfoConstants.USE_CHOSEN, true );
    }

    public DynamicPropertyBuilder externalTagList( String tableName )
    {
        return attr( BeanInfoConstants.EXTERNAL_TAG_LIST, tableName );
    }

    public DynamicPropertyBuilder multiple()
    {
        return attr( BeanInfoConstants.MULTIPLE_SELECTION_LIST, true );
    }

    public DynamicPropertyBuilder inputType( String type )
    {
        return attr( BeanInfoConstants.CUSTOM_INPUT_TYPE_ATTR, type );
    }

    public DynamicPropertyBuilder columnSize( int size )
    {
        return attr( BeanInfoConstants.COLUMN_SIZE_ATTR, String.valueOf( size ) );
    }

    public DynamicPropertyBuilder size( int size )
    {
        return attr( BeanInfoConstants.INPUT_SIZE_ATTR, String.valueOf( size ) );
    }

    public DynamicPropertyBuilder groupId( String groupId )
    {
        return attr( BeanInfoConstants.GROUP_ID, groupId );
    }

    public DynamicPropertyBuilder groupName( String groupName )
    {
        return attr( BeanInfoConstants.GROUP_NAME, groupName );
    }

    public DynamicPropertyBuilder group( String groupId, String groupName )
    {
        return groupId( groupId ).groupName( groupName );
    }

    public DynamicPropertyBuilder group( String groupName )
    {
        return group( groupName, groupName );
    }

    public DynamicPropertyBuilder tabId( int tabId )
    {
        return attr( BeanInfoConstants.TAB_ID, tabId );
    }

    public DynamicPropertyBuilder tabName( String tabName )
    {
        return attr( BeanInfoConstants.TAB_NAME, tabName );
    }

    public DynamicPropertyBuilder tab( int tabId, String tabName )
    {
        return tabId( tabId ).tabName( tabName );
    }

    public DynamicPropertyBuilder rawValue( boolean rawValue )
    {
        return attr( BeanInfoConstants.RAW_VALUE, rawValue );
    }

    public DynamicPropertyBuilder rawValue()
    {
        return rawValue( true );
    }

    public DynamicPropertyBuilder rawValue( String value )
    {
        return rawValue().value( value );
    }

    public DynamicPropertyBuilder shortdesc( String desc )
    {
        property.setShortDescription( desc );
        return this;
    }

    public DynamicPropertyBuilder expert()
    {
        return expert( true );
    }

    public DynamicPropertyBuilder expert( boolean condition )
    {
        property.setExpert( condition );
        return this;
    }

    public DynamicPropertyBuilder parent( DynamicPropertySet parent )
    {
        property.setParent( parent );
        return this;
    }

    private void addToAttrMap( String attrName, Map<String,Object> map )
    {
        @SuppressWarnings("unchecked")
        Map<String,Object> curMap = ( Map<String,Object> )property.getAttribute( attrName );
        if( curMap == null )
        {
            property.setAttribute( attrName, new LinkedHashMap<>( map ) );
        }
        else
        {
            curMap.putAll( map );
        }
    }
//
//    public DynamicPropertyBuilder uniqueValidate( String entityName, String column, String message, String ... extras )
//    {
//        Validation.UniqueStruct us = new Validation.UniqueStruct();
//        us.entity = entityName;
//        us.column = column;
//        us.message = message;
//        if( extras.length > 0 )
//        {
//            LinkedHashMap<String,String> map = new LinkedHashMap<>( extras.length );
//            for( int i = 0; i < extras.length; i += 2 )
//            {
//                map.put( extras[ i ], extras[ i + 1 ] );
//            }
//            us.extraParams = map;
//        }
//        addToAttrMap( Validation.RULES_ATTR, Collections.singletonMap( Validation.UNIQUE, us ) );
//        return this;
//    }
//
//    public DynamicPropertyBuilder queryValidate( String entityName, String queryName, String message, String ... extras )
//    {
//        Validation.QueryStruct us = new Validation.QueryStruct();
//        us.entity = entityName;
//        us.query = queryName;
//        us.message = message;
//        if( extras.length > 0 )
//        {
//            LinkedHashMap<String,String> map = new LinkedHashMap<>( extras.length );
//            for( int i = 0; i < extras.length; i += 2 )
//            {
//                map.put( extras[ i ], extras[ i + 1 ] );
//            }
//            us.extraParams = map;
//        }
//        addToAttrMap( Validation.RULES_ATTR, Collections.singletonMap( Validation.QUERY, us ) );
//        return this;
//    }
//
//    public DynamicPropertyBuilder intervalCheck( String message, Object intervalFrom, Object intervalTo )
//    {
//        Validation.IntervalStruct us = new Validation.IntervalStruct();
//        us.intervalFrom = intervalFrom;
//        us.intervalTo = intervalTo;
//        us.message = message;
//        addToAttrMap( Validation.RULES_ATTR, Collections.singletonMap( Validation.INTERVAL, us ) );
//        return this;
//    }

    public DynamicPropertyBuilder type( Class<?> clazz )
    {
        property.setType( clazz );
        return this;
    }

    public DynamicProperty get()
    {
        return property;
    }
}
