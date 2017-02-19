package com.developmentontheedge.beans.model;

import java.awt.Color;
import java.awt.Event;
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.FeatureDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.beans.IconResource;
import com.developmentontheedge.beans.log.Logger;

public class ComponentFactory implements InternalConstants
{
    public static final String DEFAULT_SWING_BEANINFO_PATH = "com.developmentontheedge.beans.swing.infos";
    public static final String DEFAULT_AWT_BEANINFO_PATH = "com.developmentontheedge.beans.awt.infos";
    public static final int DEFAULT_MAX_PROPERTY_LEVEL = 7;

    private static final HashMap<String, Class<?>> classList = new HashMap<>();
    protected static final WeakHashMap<Object, WeakReference<ComponentModel>> instanceList = new WeakHashMap<>();

    private static String[] originalBeanInfoSearchPath;

    /**
     * Adds <code>DEFAULT_BEANINFO_PATH</code>
     * to introspector BeanInfo search path.
     */
    static
    {
        Introspector.flushCaches();
        originalBeanInfoSearchPath = Introspector.getBeanInfoSearchPath();

        setSwingMode();
    }

    /**
     * Initialize a vector of properties for the specified object instance (<code>owner</code>)
     * or object class (<code>c</code>).
     * @param owner the properties owner. If null, then new instance of the
     * specified class <code>c</code> will be created
     * @param c the object class
     * @param info <code>BeanInfo</code> for the specified object.
     * If null, then bean info will be got through the Introspector.
     * @param parent the property parent
     * @param vProperties vector where properties will be added.
     * @pending medium 1) There is possible deep nesting of properties.
     * We can restrict the nesting level by some value, for example 20.
     * But should we do it? At the moment we are just ignoring this
     * issue to optimise the output.
     * @pending medium 2) This is a BIG question what should we do when
     * this excptions is thrown. At the moment we are just ignoring them.
     * The other question - should we distinguish the exception subtypes:
     * <ul>
     * <li><code>InstantiationException</code></li>
     * <li><code>IllegalAccessException</code></li>
     * </ul>
     * Currently we cath all <code>Throwable</code> exceptions and log it
     * in log4j.DEBUG category.
     */
    protected static void createProperties(Object owner, Class<?> c, BeanInfo info, Property parent, Vector<Property> vProperties)
    {
        if( owner instanceof Property.PropWrapper )
            owner = ( (Property.PropWrapper)owner ).getOwner();
        try
        {
            // initialize the possible null values
            if( owner == null )
            {
                if( c.toString().startsWith( "interface" ) || ( c.getModifiers() & Modifier.ABSTRACT ) != 0 )
                {
                    // do not bother creating interfaces
                    Logger.getLogger().warn(
                            "Cannot create properties for \"" + c + "\" in " + parent.getCompleteName()
                                    + " since it is interface or abstract class" );
                    return;
                }
                if( findDefaultPublicConstructor( c ) == null )
                {
                    Object obj = findStaticInstance( c );
                    if( obj == null )
                    {
                        Logger.getLogger().warn(
                                "createProperties: No default constructor and no static instance found (owner is null) in "
                                        + parent.getCompleteName() );
                        return;
                    }
                    owner = obj;
                }
                else
                {
                    owner = c.newInstance();
                }
            }

            if( info == null )
            {
                info = Introspector.getBeanInfo( c );
            }

            // build Dynamic properties
            if( owner instanceof DynamicPropertySet )
            {
                Iterator<DynamicProperty> iterator = ( (DynamicPropertySet)owner ).propertyIterator();
                while( iterator.hasNext() )
                {
                    DynamicProperty dynamicProperty = iterator.next();
                    if( dynamicProperty != null )
                    {
                        Property p = createProperty( dynamicProperty.getType(), owner, dynamicProperty.getDescriptor(), parent );
                        if( p != null /* && !p.getName().equals( "class" ) */)
                        {
                            vProperties.addElement( p );
                            // commented out since it is now initialized lazily
                            // p.addPropertyChangeListener( parent );
                        }
                    }
                    else
                        Logger.getLogger().error( "Iterator.next() returned null dynamic property" );
                }
            }
            // build composite property from beanInfo
            else
            {
                PropertyDescriptor[] properties = (PropertyDescriptor[])info.getBeanDescriptor().getValue( BeanInfoEx.ORDER );
                if( properties == null )
                {
                    properties = info.getPropertyDescriptors();
                }

                PropertyDescriptor descr;
                Class<?> propType;
                Property p;
                for( int i = 0; i < properties.length; i++ )
                {
                    descr = properties[i];
                    propType = derivePropertyType( owner, descr );
                    p = createProperty( propType, owner, descr, parent );
                    if( p != null /* && !p.getName().equals( "class" ) */)
                    {
                        vProperties.addElement( p );
                        // commented out since it is now initialized lazily
                        // p.addPropertyChangeListener( parent );
                    }
                }
            }
        }
        catch( Throwable t )
        {
            // PENDING 2.
            Logger.getLogger().error( "Error when creating properties in " + parent.getCompleteName(), t );
        }
    }

    /**
     * @pending high #1 type definition. Currently type is defined through
     * <code>descriptor.getPropertyType()</code>. But we have a problem
     * when a property value is type subclass with other subproperties.
     * So we need to get type of property <b>value</b>.
     * Possible we need to check type every time, when property value is changed
     * and rebuild its model if the type is changed.
     * @todo medium comments and docs
     */
    protected static Property createProperty(Class<?> type, Object owner, PropertyDescriptor descriptor, Property parent)
            throws IntrospectionException
    {
        Method readMethod = descriptor.getReadMethod();
        if( readMethod != null /* && !( owner instanceof DynamicPropertySet ) */)
        {
            if( !readMethod.getDeclaringClass().isAssignableFrom( owner.getClass() ) )
            {
                Logger.getLogger().error(
                        "Ignoring " + parent.getCompleteName() + "/" + descriptor.getName() + " since it's read method ("
                                + readMethod.getDeclaringClass().getName() + "." + readMethod.getName()
                                + ") doesn't belong to this bean class (" + owner.getClass().getName()
                                + "). Perhaps PropertyDescriptor is incorrect." );
                return null;
            }

            try
            {
                // if we will survive through this
                // then we can get value in the property
                readMethod.invoke( owner, (Object[])null );
            }
            catch( IllegalAccessException e )
            {
                return null;
            }
            catch( InvocationTargetException e )
            {
                return null;
            }
            catch( Exception e )
            {
                Logger.getLogger().error(
                        "Error creating property '" + descriptor.getName() + "' in " + parent.getCompleteName() + ", owner = " + owner
                                + ", readMethod = " + readMethod, e );
                return null;
            }
        }

        if( descriptor instanceof IndexedPropertyDescriptor || ( type.isArray() && !byte[].class.equals( type ) ) ) // byte[].class actually is String
        {
            ArrayProperty arrayProperty = new ArrayProperty( parent, owner, descriptor );
            Object itemPrototype = descriptor.getValue( "item-prototype" );//TODO: move constant to other place, I don't know where.
            if( itemPrototype != null )
            {
                arrayProperty.setItemPrototype( itemPrototype );
            }
            if( !arrayProperty.isHideChildren() )
                return arrayProperty;
        }

        // ignore properties that we can only write
        // i.e. only method like setXXXX is defined
        // for instance Rectangle.setRect
        if( readMethod == null && descriptor.getWriteMethod() != null )
        {
            // but don't ignore blindly...
            // try to create an object using default constructor
            // in this case we can edit property but cannot read
            // And only if it is not possible to create such dummy instance then ignore
            try
            {
                type.newInstance();
            }
            catch( Exception e )
            {
                //return null; it seems to work correct if we continue without return null
            }
        }

        String typeName = type.getName();
        BeanInfo typeBeanInfo = Introspector.getBeanInfo( type );
        
        BeanDescriptor beanDescriptor = typeBeanInfo.getBeanDescriptor();
        
        if( beanDescriptor != null )
            synchronized( beanDescriptor )
            {
               //Following line will init FeatureDescriptor.table to prevent further race conditions
                beanDescriptor.attributeNames();
            }
        
        descriptor = merge( beanDescriptor, descriptor, owner instanceof DynamicPropertySet );

        // define whether the property is simple or composite
        boolean isSimple = ( type.isPrimitive() || typeName.indexOf( "java.lang" ) >= 0 )
                && ( typeName.indexOf( "java.lang.Object" ) == -1 );

        if( !isSimple && descriptor.getValue( BeanInfoEx.SIMPLE ) != null )
        {
            isSimple = descriptor.getValue( BeanInfoEx.SIMPLE ).equals( Boolean.TRUE );
        }

        if( isSimple )
        {
            return new SimpleProperty( parent, owner, descriptor );
        }

        // define whether the property is DynamicPropertySet
        if( DynamicPropertySet.class.isAssignableFrom( type ) )
        {
            return new DynamicSetProperty( parent, owner, descriptor, typeBeanInfo );
        }

        // supress recursion
        if( parent != null )
        {
            Object noRecursion = descriptor.getValue( BeanInfoEx.NO_RECURSION_CHECK );
            if( noRecursion != null && noRecursion.equals( Boolean.TRUE ) )
            {
                // do nothing  
            }
            else if( parent.isRecursive( type ) )
            {
                return null;
            }
        }

        return new CompositeProperty( parent, owner, descriptor, typeBeanInfo );
    }

    /**
     * This method is used to detect real class of the property
     * when property is declared as returng an interface.
     * We try to get a class from the instance of the property
     * and use it rather than that declared in PropertyDescriptor
     */
    protected static Class<?> derivePropertyType(Object owner, PropertyDescriptor descr)
    {
        Class<?> propType = descr.getPropertyType();
        // some getter method are not safe to invoke
        // for example java.awt.EventQueue.getNextEvent()
        // causes introspection to block
        // For now such thing are simple hardcoded
        boolean isSafeToInvoke = ! ( propType != null && Event.class.isAssignableFrom( propType ) );
        if( owner != null && isSafeToInvoke )
        {
            // normally if property is declared as returning an interface type
            // Introspector returns this interface type
            // regardless what is a real class that implements this interface
            // here we are trying to fix it
            Method method = descr.getReadMethod();
            if( method != null )
            {
                try
                {
                    Object propInstance = method.invoke( owner, (Object[])null );
                    if( propInstance != null )
                    {
                        Class<?> tmpClass = propInstance.getClass();
                        if( tmpClass != null )
                        {
                            propType = tmpClass;
                        }
                    }
                }
                catch( Exception ignore )
                {
                }
            }
        }
        return propType;
    }

    protected static BeanEvent createEvent(Property ownerProperty, EventSetDescriptor esd)
    {
        try
        {
            BeanEvent be = new BeanEvent( ownerProperty, esd );
            EventListener listener = (EventListener)Proxy.newProxyInstance( esd.getListenerType().getClassLoader(),
                    new Class[] {esd.getListenerType()}, be );
            Method method = esd.getAddListenerMethod();
            method.invoke( ownerProperty.getValue(), new Object[] {listener} );
            return be;
        }
        catch( Exception exc )
        {
            if( exc instanceof IllegalAccessException || exc instanceof InvocationTargetException )
            {
                Logger.getLogger().error( "Error adding listener for the event \"" + esd.getName() + "\"", exc );
            }
        }

        return null;
    }

    protected static BeanMethod createMethod(Object owner, MethodDescriptor md)
    {
        BeanMethod bm = new BeanMethod( owner, md );
        return bm;
    }

    /** Return Class object for class with the specified name. */
    public static Class<?> forName(String aName)
    {
        try
        {
            String sName = null;
            Class<?> c = null;
            Class<?> classes[] = getLoadedClasses();
            BeanInfo beanInfo;
            for( int i = 0; i < classes.length; i++ )
            {
                c = classes[i];
                beanInfo = Introspector.getBeanInfo( c );
                sName = beanInfo.getBeanDescriptor().getName();
                if( aName.equals( sName ) )
                {
                    return c;
                }
            }
        }
        catch( IntrospectionException exc )
        {
        }
        Logger.getLogger().error( "ComponentFactory cannot find class '" + aName + "'" );
        return null;
    }

    /**
     * @todo trial check commented
     */
    private static ComponentModel createComponentModel(Object comp)
    {
        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_regtrial_dialog$

        try
        {
            Class<?> c = comp.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo( c );
            ComponentModel model = new ComponentModel( comp, beanInfo );
            return model;
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "ComponentFactory cannot create model for " + comp, e );
        }
        return null;
    }

    /*
    public static void loadClass( URL [] urlList, String className )
    {
        URLClassLoader classLoader = new URLClassLoader( urlList, null );
        try
        {
            String name = className;
            //@pending generalize
            int end = name.indexOf( ".class" );
            if ( end != -1 ) { name = name.substring( 0, end ); }
            name = name.replace( '/', '.' );
            name = name.replace( '\\', '.' );
            Class c = classLoader.loadClass( name );
            classList.put( name, c );
        }
        catch( Exception e )
        {
            Logger.error( cat, "loadClass exception:", e );
        }
    }
    */

    /**
     * filter passed model including only properties which names passed as array of strings
     * @todo Trial check commented
     */
    public static ComponentModel filterComponentModel(ComponentModel origin, String[] filter)
    {
        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_regtrial_dialog$


        if( filter == null )
        {
            return origin;
        }
        // constructor without setting the presentation peer
        ComponentModel filtered = new ComponentModel( origin.getBean(), origin.getBeanInfo() );
        filtered.properties = new Vector<>();

        Property property;
        for( int i = 0; i < filter.length; i++ )
        {
            property = origin.findProperty( filter[i] );
            if( property != null )
            {
                filtered.properties.addElement( property );
                if( filter[i].indexOf( "/" ) < 0 ) // i.e. only for top level properties
                {
                    property.addPropertyChangeListener( filtered );
                }
            }
            else
            {
                Logger.getLogger().error( "When filtering property not found: " + filter[i] + " in " + filtered.getName() );
            }
        }

        filtered.setExpanded( true );
        filtered.propertiesInitialized = true;
        return filtered;
    }

    /** filter passed model excluding properties of the superclass */
    public static ComponentModel filterByRemovingParentProperties(ComponentModel origin)
    {
        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_regtrial_dialog$

        try
        {
            // constructor without setting the presentation peer
            ComponentModel filtered = new ComponentModel( origin.getBean(), origin.getBeanInfo() );
            filtered.properties = new Vector<>();

            BeanInfo parentBeanInfo = Introspector.getBeanInfo( origin.getBean().getClass().getSuperclass() );
            PropertyDescriptor[] pds = parentBeanInfo.getPropertyDescriptors();

            Property property;
            String name;
            boolean hasParent;
            for( int i = 0; i < origin.getPropertyCount(); i++ )
            {
                property = origin.getPropertyAt( i );
                name = property.getName();
                hasParent = false;

                for( int j = 0; j < pds.length; j++ )
                {
                    if( name.equals( pds[j].getName() ) )
                    {
                        hasParent = true;
                        break;
                    }
                }

                if( !hasParent )
                {
                    filtered.properties.addElement( property );
                    property.addPropertyChangeListener( filtered );
                }
                else
                    filtered.addPropertyToRemove( property.getCompleteName() );
            }

            filtered.setExpanded( true );
            filtered.propertiesInitialized = true;
            return filtered;
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "ComponentFactory cannot create filtered model for " + origin, e );
        }

        return origin;
    }



    ////////////////////////////////////////
    // component loading & access functions
    //
    private static class URLClassLoaderEx extends URLClassLoader
    {
        public URLClassLoaderEx()
        {
            super( new URL[0] );
        }

        @Override
        public void addURL(URL url)
        {
            super.addURL( url );
        }
    }


    private static URLClassLoaderEx classLoader = new URLClassLoaderEx();

    /**
     * Load all components from all jar files on the specified path.
     * @todo Trial check commented!!
     */
    public static void loadComponents(String path, String subdir)
    {

        //$check_register_message_box$
        //$check_trial_dialog$
        //$check_regtrial_dialog$

        File[] files = null;
        int i = 0;
        try
        {
            File dir = new File( subdir.length() == 0 ? path : path + '/' + subdir );
            if( dir.isDirectory() )
            {
                files = dir.listFiles();
            }
            else
            {
                files = new File[] {dir};
            }
            for( i = 0; i < files.length; i++ )
            {
                if( files[i].isDirectory() )
                {
                    loadComponents( path, subdir.length() == 0 ? files[i].getName() : subdir + '/' + files[i].getName() );
                }
                else if( files[i].getName().endsWith( ".jar" ) )
                {
                    loadComponents( files[i] );
                }
            }
        }
        catch( Throwable e )
        {
            Logger.getLogger().error( "Error while loading components", e );
        }
    }

    protected static void loadComponents(File f)
    {
        String compName = "";
        compName = f.getName();
        String path = "file:" + f.getPath();
        try
        {
            URL url = new URL( path );
            classLoader.addURL( url );
            try (JarFile jar = new JarFile( f ))
            {
                Manifest mf = jar.getManifest();
                Map<String, Attributes> entries = mf.getEntries();
                Iterator<String> names = entries.keySet().iterator();
                String name;
                while( names.hasNext() )
                {
                    name = names.next();
                    if( name.endsWith( ".gif" ) || name.endsWith( ".GIF" ) || name.endsWith( ".jpg" ) || name.endsWith( ".JPG" )
                            || name.endsWith( ".jpeg" ) || name.endsWith( ".JPEG" ) )
                    {
                        continue;
                    }
                    // format name
                    int end = name.indexOf( ".class" );
                    if( end != -1 )
                    {
                        name = name.substring( 0, end );
                    }
                    name = name.replace( '/', '.' );
                    name = name.replace( '\\', '.' );
                    try
                    {
                        classLoader.getURLs();
                        Class<?> c = classLoader.loadClass( name );
                        classList.put( name, c );
                    }
                    catch( Throwable e )
                    {
                        Logger.getLogger().error( "Error while loading component \"" + compName + ":" + name + "\"", e );
                    }
                }
            }
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "Error while loading component \"" + compName + "\"", e );
        }
    }

    /** Returns icon for the specified class. */
    public static Image getIcon(Class<?> c)
    {
        return getIcon( c, BeanInfo.ICON_COLOR_32x32 );
    }

    /** Returns icon for the specified class. */
    public static Image getIcon(Class<?> c, int iconKind)
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo( c );
            return beanInfo.getIcon( iconKind );
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "Error while getting icon for \"" + c + "\"", e );
        }
        return null;
    }

    /** Returns Array of classes for all loaded classes */
    public static Class<?>[] getLoadedClasses()
    {
        return classList.values().toArray( new Class[] {} );
    }

    /** Return ComponentModel instance for class with the specified name. */
    public static ComponentModel getModel(String aName)
    {
        Class<?> clazz = forName( aName );
        return getModel( clazz );
    }

    public static ComponentModel getModel(Class<?> c)
    {
        try
        {
            return getModel( c.newInstance() );
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "ComponentFactory cannot instantiate " + c, e );
        }
        return null;
    }

    public static ComponentModel getModel(Object bean) // throws NoSuchMethodException
    {
        return getModel( bean, false );
    }

    public static ComponentModel getModel(Object bean, boolean bIgnoreCache) // throws NoSuchMethodException
    {
        // if component model itself is supplied
        // just use it itself
        if( bean instanceof ComponentModel )
            return (ComponentModel)bean;

        ComponentModel model = null;
        if( !bIgnoreCache )
            model = getFromComponentCache( bean );
        if( model == null )
        {
            model = createComponentModel( bean );
            if( model != null && !bIgnoreCache )
            {
                putIntoComponentCache( bean, model );
            }

            // Fedor: I think, it can be really slow.
            // My suggestion is to have double hash:
            // 1 - hash by class (the class wull not be changed)
            // 2 - hash by object of the specified class
            // Now I turn off this option

            /*
                        // Iterate through all registered components
                        // It is possible that bean's hashCode was changed
                        // (see java.awt.Dimension for example).
                        // In this case we can find a model only
                        // iterating through all possible values
                        Iterator iterator = instanceList.values().iterator();
                        while ( iterator.hasNext() )
                        {
                            ComponentModel mdl = ( ComponentModel )iterator.next();
                            if ( mdl.getBean().equals( bean ) ) // Or should it be reference comparison?
                            {
                                iterator.remove(); // remove underlying entry since it is no longer valid
                                instanceList.put( bean, mdl );
                                model = mdl;
                                break;
                            }
                        }
            */
        }
        return model;
    }

    public static DynamicPropertySet toDPS(ComponentModel model) throws Exception
    {
        DynamicPropertySet dpsObj = new DynamicPropertySetSupport();
        int limit = model.getPropertyCount();
        for( int i = 0; i < limit; i++ )
        {
            Property property = model.getPropertyAt( i );
            FeatureDescriptor desc = property.getDescriptor();
            if( desc instanceof PropertyDescriptor )
            {
                dpsObj.add( new DynamicProperty( (PropertyDescriptor)desc, property.getValueClass(), property.getValue() ) );
            }
            else
            {
                dpsObj.add( new DynamicProperty( property.getName(), property.getDisplayName(), property.getValueClass(), property
                        .getValue() ) );
            }
        }

        return dpsObj;
    }

    /**
     * Merges information from <code>PropertyDescriptor</code> and <code>BeanDescriptor</code>.
     * <p>See <a href="../_doc/concepts.html"> the document</a> for details.
     * @pending high meriging for attributes that are Methods are meningless because method belong
     * to the different class than that associated with BeanDescriptor. We need some graceful
     * solutions for this. Perhaps methods should be replaced with some delegates
     * - anonymous classes
     * @returns PropertyDescriptor that is result of merge of specified <code>beanDescriptor</code>
     * and <code>propertyDescriptor</code>. It can be null if both of them were null.
     */
    static protected PropertyDescriptor merge(BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, boolean isDynamic)
    {
        if( beanDescriptor == null )
        {
            return propertyDescriptor;
        }

        processIcons( beanDescriptor );
        try
        {
            if( propertyDescriptor == null )
            {
                PropertyDescriptor result = new PropertyDescriptor( beanDescriptor.getName(), null, null );
                Class<?> editor = beanDescriptor.getCustomizerClass();
                if( editor != null && PropertyEditor.class.isAssignableFrom( editor ) )
                {
                    result.setPropertyEditorClass( editor );
                }

                result.setDisplayName( beanDescriptor.getDisplayName() );
                result.setShortDescription( beanDescriptor.getShortDescription() );

                Enumeration<?> e = beanDescriptor.attributeNames();
                while( e.hasMoreElements() )
                {
                    String atr = (String)e.nextElement();
                    result.setValue( atr, beanDescriptor.getValue( atr ) );
                }
                result.setValue( BEAN_DESCRIPTOR, beanDescriptor );
                return result;
            }
            else
            {
                return new MergedPropertyDescriptor( propertyDescriptor, beanDescriptor, isDynamic );
            }
        }
        catch( Throwable t )
        {
            Logger.getLogger().error( "Error during merge", t );
        }
        return propertyDescriptor;
    }

    private static void processIcons(BeanDescriptor beanDescriptor)
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo( beanDescriptor.getBeanClass() );

            Image icon = beanInfo.getIcon( BeanInfo.ICON_COLOR_16x16 );
            if( icon != null && beanDescriptor.getValue( BeanInfoEx.NODE_ICON_COLOR_16x16 ) == null )
                beanDescriptor.setValue( BeanInfoEx.NODE_ICON_COLOR_16x16, new IconResource( null, icon ) );

            icon = beanInfo.getIcon( BeanInfo.ICON_MONO_16x16 );
            if( icon != null && beanDescriptor.getValue( BeanInfoEx.NODE_ICON_MONO_16x16 ) == null )
                beanDescriptor.setValue( BeanInfoEx.NODE_ICON_MONO_16x16, new IconResource( null, icon ) );

            icon = beanInfo.getIcon( BeanInfo.ICON_COLOR_32x32 );
            if( icon != null && beanDescriptor.getValue( BeanInfoEx.NODE_ICON_COLOR_32x32 ) == null )
                beanDescriptor.setValue( BeanInfoEx.NODE_ICON_COLOR_32x32, new IconResource( null, icon ) );

            icon = beanInfo.getIcon( BeanInfo.ICON_MONO_32x32 );
            if( icon != null && beanDescriptor.getValue( BeanInfoEx.NODE_ICON_MONO_32x32 ) == null )
                beanDescriptor.setValue( BeanInfoEx.NODE_ICON_MONO_32x32, new IconResource( null, icon ) );
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "Error when process icons for " + beanDescriptor.getBeanClass(), e );
        }
    }

    ////////////////////////////////////////
    // update issues
    //

    /** Updates property descriptor. */
    public static void updateDescriptor(Property property, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor)
    {
        property.descriptor = merge( beanDescriptor, propertyDescriptor, false );
    }

    public static void recreateChildProperties(Property p)
    {
        if( ! ( p instanceof CompositeProperty ) || ! ( (CompositeProperty)p ).propertiesInitialized )
        {
            return;
        }

        CompositeProperty property = (CompositeProperty)p;

        if( property.properties != null )
            property.properties.removeAllElements();

        createProperties( property.getValue(), property.getBean().getClass(), property.getBeanInfo(), property, property.properties );
        ///*
        if( property instanceof ComponentModel && ( (ComponentModel)property ).hasPropertiesToRemove() )
        {
            String[] properties = ( (ComponentModel)property ).getPropertiesToRemove();
            for( int i = 0; i < properties.length; i++ )
            {
                Property propertyToRemove = property.findProperty( properties[i] );
                if( propertyToRemove != null )
                {
                    property.properties.removeElement( propertyToRemove );
                }
            }
        }
        //*/
    }


    /** Rearrange properties order according to specified comparator. */
    public static void sort(CompositeProperty cp, Comparator<Property> comparator)
    {
        Collections.sort( cp.properties, comparator );
    }

    //////////////////////////////////////////////////

    public static void setSwingMode()
    {
        String[] newPath = new String[originalBeanInfoSearchPath.length + 1];
        newPath[0] = DEFAULT_SWING_BEANINFO_PATH;
        System.arraycopy( originalBeanInfoSearchPath, 0, newPath, 1, originalBeanInfoSearchPath.length );
        Introspector.setBeanInfoSearchPath( newPath );
    }

    public static void setAWTMode()
    {
        String[] newPath = new String[originalBeanInfoSearchPath.length + 1];
        newPath[0] = DEFAULT_AWT_BEANINFO_PATH;
        System.arraycopy( originalBeanInfoSearchPath, 0, newPath, 1, originalBeanInfoSearchPath.length );
        Introspector.setBeanInfoSearchPath( newPath );
    }

    static void putIntoComponentCache(Object bean, ComponentModel model)
    {
        synchronized( instanceList )
        {
            instanceList.put( bean, new WeakReference<>( model ) );
        }
    }

    static ComponentModel getFromComponentCache(Object bean)
    {
        synchronized( instanceList )
        {
            WeakReference<?> ref = instanceList.get( bean );
            ComponentModel model = (ComponentModel) ( ref == null ? null : ref.get() );
            return model;
        }
    }

    static Constructor<?> findDefaultPublicConstructor(Class<?> clazz)
    {
        Constructor<?> constructor = null;
        Constructor<?>[] cList = clazz.getConstructors();
        for( int i = 0; i < cList.length; i++ )
        {
            if( cList[i].getParameterTypes().length != 0 )
            {
                continue;
            }
            constructor = cList[i];
            break;
        }
        return constructor;
    }

    public static Object instanceForClass(Class<?> c)
    {
        Object param = null;

        if( c.isArray() )
        {
            param = null;
        }
        else if( c.toString().equals( "boolean" ) || c == Boolean.class )
        {
            param = Boolean.TRUE;
        }
        else if( c.toString().equals( "int" ) || c == Integer.class )
        {
            param = 1;
        }
        else if( c.toString().equals( "long" ) || c == Long.class )
        {
            param = 1L;
        }
        else if( c.toString().equals( "float" ) || c == Float.class )
        {
            param = 1.0f;
        }
        else if( c.toString().equals( "double" ) || c == Double.class )
        {
            param = 1.0;
        }
        else if( c == Color.class )
        {
            param = Color.white;
        }
        else if( c == String.class )
        {
            param = "";
        }
        else if( c == Object.class )
        {
            param = new Object();
        }
        else if( c.isInterface() )
        {
            param = null;
        }
        else
        {
            param = null;

            Constructor<?> constructor = null;
            try
            {
                constructor = c.getConstructor( (Class[])null );
            }
            catch( Exception e )
            {
            }

            try
            {
                if( constructor != null ) // default constructor
                {
                    param = constructor.newInstance( (Object[])null );
                }
                else
                {
                    /* recuirsive solution, don't delete, can be used in the future
                    param = new Constructors(c.getConstructors());
                    */
                    param = null;
                }
            }
            catch( Exception e )
            {
                //e.printStackTrace(); // debug
                param = null;
            }
        }
        return param;
    }

    public static Object findStaticInstance(Class<?> clazz) throws Exception
    {
        // first iterate through static member attributes
        Field[] fList = clazz.getFields();
        for( int i = 0; i < fList.length; i++ )
        {
            if( ( fList[i].getModifiers() & Modifier.STATIC ) == 0 )
            {
                continue;
            }
            if( fList[i].getType().equals( clazz ) )
            {
                return fList[i].get( null );
            }
        }

        // first iterate through static member functions
        Method[] mList = clazz.getMethods();
        for( int i = 0; i < mList.length; i++ )
        {
            if( ( mList[i].getModifiers() & Modifier.STATIC ) == 0 )
            {
                continue;
            }
            if( mList[i].getParameterTypes().length != 0 )
            {
                continue;
            }
            if( mList[i].getReturnType().equals( clazz ) )
            {
                return mList[i].invoke( null, new Object[0] );
            }
        }
        return null;
    }

    static private SimpleProperty simplePropertyClassRetriver = new SimpleProperty( null, null, null );
    public static Class<? extends SimpleProperty> getPropertyClassInObfuscatedVersion()
    {
        return simplePropertyClassRetriver.getClass();
    }

    public static String getHTMLClassName(Class<?> clazz)
    {
        return "<html><code>" + getClassName( clazz ) + "</code></html>";
    }

    public static String getClassName(Class<?> clazz)
    {
        if( clazz == null )
        {
            return "";
        }
        else if( clazz.toString().equals( "boolean" ) )
        {
            return "boolean";
        }
        else if( clazz.toString().equals( "int" ) )
        {
            return "int";
        }
        else if( clazz.toString().equals( "long" ) )
        {
            return "long";
        }
        else if( clazz.toString().equals( "float" ) )
        {
            return "float";
        }
        else if( clazz.toString().equals( "double" ) )
        {
            return "double";
        }
        else if( clazz.toString().equals( "byte" ) )
        {
            return "byte";
        }
        else if( clazz.toString().equals( "char" ) )
        {
            return "char";
        }
        else if( clazz.toString().equals( "short" ) )
        {
            return "short";
        }

        StringTokenizer strTok = new StringTokenizer( clazz.getName(), "." );
        String name = null;
        while( strTok.hasMoreTokens() )
        {
            name = strTok.nextToken();
        }

        if( name.startsWith( "[" ) || name.endsWith( ";" ) ) // array
        {
            String fullName = clazz.getName();
            int dim = fullName.lastIndexOf( "[" );
            String type = fullName.substring( dim + 1, dim + 2 );

            StringBuffer buff = new StringBuffer( "" );
            if( type.equals( "L" ) ) // class
            {
                buff.append( name.substring( 0, name.length() - 1 ) );
            }
            else if( type.equals( "B" ) ) // class or interface
            {
                buff.append( "byte" );
            }
            else if( type.equals( "C" ) ) // char
            {
                buff.append( "char" );
            }
            else if( type.equals( "D" ) ) // double
            {
                buff.append( "double" );
            }
            else if( type.equals( "F" ) ) // float
            {
                buff.append( "float" );
            }
            else if( type.equals( "I" ) ) // int
            {
                buff.append( "int" );
            }
            else if( type.equals( "J" ) ) // long
            {
                buff.append( "long" );
            }
            else if( type.equals( "S" ) ) // short
            {
                buff.append( "short" );
            }
            else if( type.equals( "Z" ) ) // boolean
            {
                buff.append( "boolean" );
            }

            while( dim-- >= 0 )
            {
                buff.append( "[]" );
            }

            name = buff.toString();
        }
        return name;
    }
}
