package com.developmentontheedge.beans;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.LayoutManager;
import java.beans.BeanDescriptor;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Stream;

import com.developmentontheedge.beans.annot.ExpertProperty;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.editors.StringTagEditor;
import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.util.HtmlUtil;

/**
 * This class extends <code>SimpleBeanInfo</code> to provide ability to order the properties
 * of the component and define some extended attributes. <p>
 * Order of the properties is defined by the order in which they were created (or added) in
 * constructor. Usage example below demonstrates this concept and utility functions
 * for adding properties.
 * <code>BeanInfoEx</code> usage example: <pre>
 *
 * public class MyComponentBeanInfo extends BeanInfoEx
 * {
 *     public MyComponentBeanInfo()
 *     {
 *          super(MyComponent.class, "MyResourceBundle");
 *
 *         // add simple property
 *         add(new PropertyDescriptor("name", beanClass));
 *
 *         // add property with display name and description from resource bundle
 *         add(new PropertyDescriptor("size", beanClass),
 *             getResourceString("PN_SIZE"), getResourceString("PD_SIZE"));
 *
 *         // add property with specific property editor and description
 *         add(new PropertyDescriptor("font", beanClass), FontEditor.class,
 *             getResourceString("PN_FONT"), getResourceString("PD_FONT"));
 *
 *         // you can also create and customize a property and then add it
 *         PropertyDescriptor pd = new PropertyDescriptor("color", beanClass);
 *         pd.setPropertyEditorClass(ColorEditor.Class);
 *         ...
 *         add(pd);
 *
 *         // now MyComponentBeanInfo contains four properties
 *         // in following order: name, size, font, color.
 *
 *         // we also can some text descriptions for BeanDescriptor
 *         beanDescriptor.setDisplayName       (getResourceString("CN_MY_CLASS"));
 *         beanDescriptor.setShortDescription  (getResourceString("CD_MY_CLASS"));
 *     }
 * }
 * </pre>
 *
 * @todo provide method to set up common (parent) resource bundle.
 */
public class BeanInfoEx extends SimpleBeanInfo implements BeanInfoConstants
{
    public static final String HTML_DESCRIPTION_PROPERTY = "htmlDescription";

    ////////////////////////////////////////
    // Internal variables
    //

    /** Bean class. */
    protected Class<?> beanClass;

    /** Bean descriptor. */
    protected BeanDescriptor beanDescriptor;

    /**
     * Resource bundle containing necessary String data.
     * This resource bundle contains join of childResources with parentResources.
     */
    protected ResourceBundle resources = null;

    /** Resource bundle containing necessary String data. */
    protected ResourceBundle childResources = null;

    /**
     * Common description is located in com.developmentontheedge.beans.MessagesBundle.
     * This bundle is used as parent bundle for resources.
     */
    protected static ResourceBundle parentResources = null;

    ////////////////////////////////////////
    // Constructors
    //

    public BeanInfoEx(Class<?> beanClass)
    {
        this( beanClass, beanClass.getName() + "MessageBundle" );
    }

    public BeanInfoEx(Class<?> beanClass, boolean stubResource)
    {
        this( beanClass, stubResource ? StubMessageBundle.class.getName() : beanClass.getName() + "MessageBundle" );
    }

    /**
     * Initialize BeanInfoEx.
     * @param beanClass class of Component corresponding to this BeanInfoEx.
     * @param resourceBundleName name of ResourceBundle containing needed String data.
     */
    public BeanInfoEx(Class<?> beanClass, String resourceBundleName)
    {
        this.beanClass = beanClass; // this must be the first statement!

        initResources( resourceBundleName );

        beanDescriptor = new BeanDescriptor( beanClass );

        String className = getClass().getName();
        String beanClassName = beanClass.getName();

        String bareName = beanClassName.substring( beanClassName.lastIndexOf( "." ) + 1 );

        boolean ourHack = className.equals( "com.developmentontheedge.beans.swing.infos." + bareName + "BeanInfo" )
                || className.equals( "com.developmentontheedge.beans.awt.infos." + bareName + "BeanInfo" );

        if( !className.startsWith( beanClassName ) && !ourHack )
        {
            logError( "Bean class \"" + beanClassName + "\" mismatch BeanInfo \"" + className + "\" class!" );
        }

        try
        {
            initProperties();
        }
        catch( Exception e )
        {
            logError( "Can not initialize properties", e );
        }
        PropertyName propertyName = beanClass.getAnnotation( PropertyName.class );
        if( propertyName != null )
            beanDescriptor.setDisplayName( convertString( propertyName.value() ) );
        PropertyDescription propertyDescription = beanClass.getAnnotation( PropertyDescription.class );
        if( propertyDescription != null )
            setHtmlDescription( beanDescriptor, convertString( propertyDescription.value() ) );

        ExpertProperty expertProperty = beanClass.getAnnotation( ExpertProperty.class );
        if( expertProperty != null )
            beanDescriptor.setExpert( true );
    }

    private static void setHtmlDescription(FeatureDescriptor descriptor, String description)
    {
        String textDescription = HtmlUtil.stripHtml( description );
        if( !textDescription.equals( description ) )
        {
            descriptor.setValue( HTML_DESCRIPTION_PROPERTY, description );
        }
        descriptor.setShortDescription( textDescription );
    }

    private String convertString(String value)
    {
        if( value == null )
            return null;
        String[] fields = value.split( "\\$" );
        for( int i = 0; i < fields.length; i++ )
        {
            if( i % 2 != 0 )
            {
                try
                {
                    fields[i] = beanClass.getField( fields[i] ).get( beanClass ).toString();
                }
                catch( Exception e )
                {
                }
            }
        }
        return String.join( "", fields );
    }

    /**
     * Shouldn't be called directly - for obfuscation trick
     * We create dummy instances to call getClass() upon them
     */
    protected BeanInfoEx()
    {
    }

    /**
     * Returns icon of specified type.
     * @return Icon of specified type.
     */
    @Override
    public Image getIcon(int iconKind)
    {
        Object res = null;
        switch( iconKind )
        {
            case ICON_COLOR_16x16:
                res = getBeanDescriptor().getValue( NODE_ICON_COLOR_16x16 );
                break;
            case ICON_MONO_16x16:
                res = getBeanDescriptor().getValue( NODE_ICON_MONO_16x16 );
                break;
            case ICON_COLOR_32x32:
                res = getBeanDescriptor().getValue( NODE_ICON_COLOR_32x32 );
                break;
            case ICON_MONO_32x32:
                res = getBeanDescriptor().getValue( NODE_ICON_MONO_32x32 );
                break;
        }
        if( res != null )
        {
            if( res instanceof IconResource )
                return ( (IconResource)res ).getImage();
            else if( res instanceof Image )
                return (Image)res;
        }
        return super.getIcon( iconKind );
    }


    /**
     * This method must be overriden to add the properties for the bean.
     */
    protected void initProperties() throws Exception
    {
    }

    ////////////////////////////////////////
    // Properties order issues
    //

    /** Vector of properties. */
    protected Vector<PropertyDescriptor> properties = new Vector<>();

    /**
     * Gets the Bean's PropertyDescriptors. <p>The properties are stored in the
     * <code>properties</code>  vector
     * and cas to <code>PropertyDescriptor[]</code> by this function.
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return properties.toArray( new PropertyDescriptor[properties.size()] );
    }

    private void annotate(PropertyDescriptor pd)
    {
        PropertyName propertyName = null;
        PropertyDescription propertyDescription = null;
        ExpertProperty expertProperty = null;
        Method writeMethod = pd.getWriteMethod();
        if( writeMethod != null )
        {
            propertyName = writeMethod.getAnnotation( PropertyName.class );
            propertyDescription = writeMethod.getAnnotation( PropertyDescription.class );
            expertProperty = writeMethod.getAnnotation( ExpertProperty.class );
        }
        Method readMethod = pd.getReadMethod();
        if( readMethod != null )
        {
            propertyName = readMethod.getAnnotation( PropertyName.class );
            propertyDescription = readMethod.getAnnotation( PropertyDescription.class );
            expertProperty = readMethod.getAnnotation( ExpertProperty.class );
        }
        if( propertyName != null )
            pd.setDisplayName( convertString( propertyName.value() ) );
        if( propertyDescription != null )
            setHtmlDescription( pd, convertString( propertyDescription.value() ) );
        if( expertProperty != null )
            pd.setExpert( true );
    }

    /** Adds the specified property. */
    public void add(PropertyDescriptor pd)
    {
        properties.add( pd );
        annotate( pd );
    }

    /** Inserts the property into the specified position. */
    public void add(int index, PropertyDescriptor pd)
    {
        properties.add( index, pd );
        annotate( pd );
    }

    /**
     * Adds the property with the given display name and description.
     * @param pd PropertyDescriptor to be added.
     * @param displayName a (localized) display name for this property.
     * @param description a (localized) short description to be associated with this property.
     */
    public void add(PropertyDescriptor pd, String displayName, String description)
    {
        add( properties.size(), pd, displayName, description );
    }

    /**
     * Inserts the property with the given display name and description at the specified position.
     * @param index - index at which the specified property is to be inserted.
     * @param pd PropertyDescriptor to be added.
     * @param displayName a (localized) display name for this property.
     * @param description a (localized) short description to be associated with this property.
     */
    public void add(int index, PropertyDescriptor pd, String displayName, String description)
    {
        pd.setDisplayName( displayName );
        if( description != null )
            pd.setShortDescription( description );
        add( index, pd );
    }

    /**
     * Adds the property with the specified PropertyEditor.
     * @param pd PropertyDescriptor to be added.
     * @param propertyEditorClass the Class for the desired PropertyEditor.
     */
    public void add(PropertyDescriptor pd, Class<?> propertyEditor)
    {
        add( properties.size(), pd, propertyEditor );
    }

    /**
     * Inserts the property with the specified PropertyEditor at the specified position.
     * @param index - index at which the specified property is to be inserted.
     * @param pd PropertyDescriptor to be added.
     * @param propertyEditorClass the Class for the desired PropertyEditor.
     */
    public void add(int index, PropertyDescriptor pd, Class<?> propertyEditor)
    {
        pd.setPropertyEditorClass( propertyEditor );
        add( index, pd );
    }

    /**
     * Adds the property with the specified PropertyEditor and short description.
     * @param propertyEditorClass the Class for the desired PropertyEditor.
     * @param pd PropertyDescriptor to be added.
     * @param displayName a (localized) display name for this property.
     * @param description a (localized) short description to be associated with this property.
     */
    public void add(PropertyDescriptor pd, Class<?> propertyEditor, String displayName, String description)
    {
        add( properties.size(), pd, propertyEditor, displayName, description );
    }

    /**
     * Inserts the property with the specified PropertyEditor and short description
     * at the specified position.
     * @param index - index at which the specified property is to be inserted.
     * @param pd PropertyDescriptor to be added.
     * @param propertyEditorClass the Class for the desired PropertyEditor.
     * @param displayName a (localized) display name for this property.
     * @param description a (localized) short description to be associated with this property.
     */
    public void add(int index, PropertyDescriptor pd, Class<?> propertyEditor, String displayName, String description)
    {
        pd.setDisplayName( displayName );
        pd.setPropertyEditorClass( propertyEditor );
        if( description != null )
            pd.setShortDescription( description );
        add( index, pd );
    }

    /** Adds the specified property as 'hidden'. */
    public void addHidden(PropertyDescriptor pd)
    {
        pd.setHidden( true );
        add( pd );
    }

    /** Adds the specified property as 'hidden'. */
    public void addHidden(PropertyDescriptor pd, Class<?> propertyEditor)
    {
        pd.setPropertyEditorClass( propertyEditor );
        pd.setHidden( true );
        add( pd );
    }


    /**
     * Finds the property index by its programmatic name.
     */
    public int findPropertyIndex(String programmaticName)
    {
        for( int i = 0; i < properties.size(); i++ )
        {
            PropertyDescriptor pd = properties.elementAt( i );
            if( pd.getName().equals( programmaticName ) )
                return i;
        }

        return properties.size();
    }

    /**
     * Finds PropertyDescriptor by property programmatic name.
     */
    public PropertyDescriptor findPropertyDescriptor(String programmaticName)
    {
        for( PropertyDescriptor pd : properties )
        {
            if( pd.getName().equals( programmaticName ) )
                return pd;
        }

        return null;
    }

    ////////////////////////////////////////
    // BeanInfoEx properties
    //

    /** Display name method. */
    protected Method displayNameMethod = null;

    /**
     * Sets a method for calculating display name of the bean.
     * @param method Method for calculating display name of the bean.
     *  Signature is <code>String X();</code>.
     */
    public void setDisplayNameMethod(Method method)
    {
        displayNameMethod = method;
    }

    /** Help id. */
    protected String helpId = null;

    /**
     * This attribute is used to set context for some help system that can be possibly defined in an application.
     * It can be some identifier of help resource or help message itself as well.
     * @param context identifier
     */
    public void setHelpId(String helpId)
    {
        this.helpId = helpId;
    }

    /** Visible flag */
    protected Boolean hidden = null;

    /**
     * Hides/shows properties of the bean.
     * @param Flag that indicates visibility of the bean's properties.
     */
    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    /** Can be removed by child flag */
    protected Boolean substituteByChild = null;

    /**
     * Sets 'substitute by child' flag.
     * If the bean has only one visible child property it will be used
     * instead of the bean if this flag is specified
     * @param substituteByChild new value of 'substitute by child' flag.
     */
    public void setSubstituteByChild(boolean substituteByChild)
    {
        this.substituteByChild = substituteByChild;
    }

    /** Indicates whether a bean is read only. */
    protected Boolean readOnly = null;

    /**
     * Sets/Resets read only flag for all bean's properties.
     * @param readOnly Flag that indicates 'readOnly' attributes.
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /** Indicates whether a bean should hide its children. */
    protected Boolean hideChildren = null;

    /**
     * Set flag indicates whether a bean should hide its children.
     * @param hide Flag indicates whether a bean should hide its children.
     */
    public void setHideChildren(boolean hide)
    {
        this.hideChildren = hide;
    }

    /** Indicates whether the bean should be considered as simple. */
    protected Boolean simple = null;

    /**
     * Set flag indicates whether the bean should be considered as a simple property.
     * @param value Flag indicates whether the bean should be considered as a simple property.
     */
    public void setSimple(boolean value)
    {
        simple = value;
    }

    protected Boolean noRecursionCheck = null;

    /**
     * This flag is inspired by classes like <code>java.awt.Dimension</code>
     * which contain property of the type <code>java.awt.Dimension</code>
     * thus causing endless loop while recursively introspecting beans.
     * Normally we suppress recursion but in some situations this
     * screens out required properties. In such situation the developer
     * can use this flag to aviod recusrion suppresion, but he may be
     * required to write BeanInfo for the class in this case.
     */
    public void setNoRecursionCheck(boolean noRecursionCheck)
    {
        this.noRecursionCheck = noRecursionCheck;
    }

    /** Property editor for a bean as a whole. */
    protected Class<?> beanEditorClass;

    /**
     * Set editor for a bean as a whole.
     * @param editorClass  the Class object of the Java class that implements
     *          the bean's Customizer.  For example sun.beans.OurButtonCustomizer.class.
     */
    public void setBeanEditor(Class<?> editorClass)
    {
        beanEditorClass = editorClass;
    }

    protected String compositeEditorPropertyList;
    protected LayoutManager compositeEditorLayoutManager;

    /**
     * Sets up a composite editor using some properties of the bean.
     * @param propertyList List of property names for composite editor.
     *  <p>Semicolon is used as a delimiter of property names .
     *  <p>Nested subproperties can be used.
     *  <p> <i>Examples:</i>
     *  <ul>
     *      <li><code>"color;width;stroke"</code></li>
     *      <li><code>"pen/color;pen/width;brush/stroke"</code></li>
     *  </ul>
     * @param layoutManager Layout manager to arrange property renderers/editors inside the composite editor.
     *  <p> Currently meaningful layout managers are only:
     *  <code>GridLayout</code> and <code>FlowLayout</code>.
     */
    public void setCompositeEditor(String propertyList, LayoutManager layoutManager)
    {
        compositeEditorPropertyList = propertyList;
        compositeEditorLayoutManager = layoutManager;
    }

    protected Dimension editorPreferredSize;

    /**
     * This method allows the developer to specify preferred size
     * of the property when it is displayed in some visual UI
     * like PropertyInspector or DialogPropertyInspector
     */
    public void setEditorPreferredSize(Dimension editorPreferredSize)
    {
        this.editorPreferredSize = editorPreferredSize;
    }

    /**
     * Returns bean descriptor.
     * @return Bean descriptor.
     */
    @Override
    public BeanDescriptor getBeanDescriptor()
    {
        if( beanEditorClass != null )
        {
            BeanDescriptor bd = new BeanDescriptor( beanClass, beanEditorClass );
            bd.setDisplayName( beanDescriptor.getDisplayName() );
            bd.setShortDescription( beanDescriptor.getShortDescription() );
            beanDescriptor = bd;
        }

        if( displayNameMethod != null )
            beanDescriptor.setValue( BEAN_DISPLAY_NAME, displayNameMethod );

        if( hidden != null )
            beanDescriptor.setHidden( hidden.booleanValue() );

        if( substituteByChild != null )
            beanDescriptor.setValue( SUBSTITUTE_BY_CHILD, substituteByChild );

        if( noRecursionCheck != null )
            beanDescriptor.setValue( NO_RECURSION_CHECK, noRecursionCheck );

        if( helpId != null )
            beanDescriptor.setValue( HELP_ID, helpId );

        if( simple != null )
            beanDescriptor.setValue( SIMPLE, simple );

        if( hideChildren != null )
            beanDescriptor.setValue( HIDE_CHILDREN, hideChildren );

        if( editorPreferredSize != null )
            beanDescriptor.setValue( EDITOR_PREFERRED_SIZE, editorPreferredSize );

        if( compositeEditorPropertyList != null )
            beanDescriptor.setValue( COMPOSITE_EDITOR_PROPERTY_LIST, compositeEditorPropertyList );

        if( compositeEditorLayoutManager != null )
        {
            beanDescriptor.setValue( COMPOSITE_EDITOR_LAYOUT_MANAGER, compositeEditorLayoutManager );
        }

        PropertyDescriptor[] props = getPropertyDescriptors();
        beanDescriptor.setValue( ORDER, props );

        if( readOnly != null && Boolean.TRUE.equals( readOnly ) )
        {
            beanDescriptor.setValue( READ_ONLY, readOnly );
            for( int i = 0; i < props.length; i++ )
            {
                props[i].setValue( READ_ONLY, readOnly );
            }
        }

        return beanDescriptor;
    }

    ////////////////////////////////////////
    // utilites
    //

    /**
     * Returns string from the resource bundle for the specified key.
     * If the string is absent the key string is returned instead and
     * the message is printed in <code>log4j.Category</code> for the component.
     */
    public String getResourceString(String key)
    {
        try
        {
            return resources.getString( key );
        }
        catch( Throwable t )
        {
            logError( "Missing resource <" + key + "> in " + beanClass );
        }

        return key;
    }

    /** Initialize resources necessary to retrieve localized strings. */
    protected void initResources(String resourceBundleName)
    {
        try
        {
            if( resourceBundleName == null )
            {
                resources = parentResources;
                childResources = resources;
                return;
            }

            ClassLoader cl = beanClass.getClassLoader();

            childResources = ( cl != null ) ? ResourceBundle.getBundle( resourceBundleName, Locale.getDefault(), cl ) : ResourceBundle
                    .getBundle( resourceBundleName, Locale.getDefault() );

            if( parentResources == null )
            {
                resources = childResources;
                return;
            }

            // if childResources & parentResources both != null
            // we should create new ResourceBundle,
            // thatis join of them.
            resources = new ResourceBundle()
            {
                /**
                 * Override of ResourceBundle, same semantics. The only difference, if
                 * resource is not found,
                 * it will be search in parentResources.
                 */
                @Override
                protected Object handleGetObject(String key) throws MissingResourceException
                {
                    MissingResourceException mre;
                    try
                    {
                        return childResources.getObject( key );
                    }
                    catch( MissingResourceException e )
                    {
                        mre = e;
                        try
                        {
                            return parentResources.getObject( key );
                        }
                        catch( MissingResourceException ee )
                        {
                        }
                        throw mre;
                    }
                }

                /** Implementation of ResourceBundle.getKeys. */
                @Override
                public Enumeration<String> getKeys()
                {
                    Enumeration<String> result = new Enumeration<String>()
                    {
                        final Enumeration<String> childKeys = childResources.getKeys();
                        final Enumeration<String> parentKeys = parentResources.getKeys();
                        String temp = null;
                        @Override
                        public boolean hasMoreElements()
                        {
                            if( temp == null )
                            {
                                nextElement();
                            }
                            return temp != null;
                        }
                        @Override
                        public String nextElement()
                        {
                            String returnVal = temp;
                            if( childKeys.hasMoreElements() )
                            {
                                temp = childKeys.nextElement();
                                return returnVal;
                            }
                            temp = null;
                            while( temp == null && parentKeys.hasMoreElements() )
                            {
                                temp = parentKeys.nextElement();
                                // check if childResources bundle contains the temp as key
                                try
                                {
                                    childResources.getObject( temp );
                                    temp = null;
                                }
                                catch( MissingResourceException e )
                                {
                                }
                            }
                            return returnVal;
                        } // nextElement
                    }; // Enumeration
                    return result;
                } // getKeys
            }; // ResourceBundle
        } // try
        catch( MissingResourceException mre )
        {
            logError( "Resource '" + resourceBundleName + "' can not be initilized", mre );
        }
    }

    protected void logError(String message, Throwable t)
    {
        Logger.getLogger().error( message, t );
    }

    protected void logError(String message)
    {
        logError( message, null );
    }

    public static final class LambdaTagEditor extends StringTagEditor
    {
        protected static final String TAG_SUPPLIER_FUNCTION = "tagSupplierFunction";

        @Override
        public String[] getTags()
        {
            @SuppressWarnings ( "unchecked" )
            Function<Object, Stream<String>> fn = (Function<Object, Stream<String>>)getDescriptor().getValue( TAG_SUPPLIER_FUNCTION );
            Stream<String> stream = fn.apply( getBean() );
            return stream == null ? new String[0] : stream.toArray( String[]::new );
        }
    }

    public PropertyDescriptorBuilder property(String name)
    {
        return new PropertyDescriptorBuilder( name );
    }

    public PropertyDescriptorBuilder property(PropertyDescriptorEx pd)
    {
        return new PropertyDescriptorBuilder( pd );
    }

    public void add(String name)
    {
        property( name ).add();
    }

    public void addWithTags(String name, String ... tags)
    {
        property( name ).tags( tags ).add();
    }

    public void addWithTags(PropertyDescriptorEx pd, String ... tags)
    {
        property( pd ).tags( tags ).add();
    }

    public void add(String name, Class<?> editor) throws IntrospectionException
    {
        add( new PropertyDescriptorEx( name, beanClass ), editor );
    }

    public void addHidden(String name) throws IntrospectionException
    {
        PropertyDescriptorEx pd = new PropertyDescriptorEx( name, beanClass );
        pd.setHidden( true );
        add( pd );
    }

    public void addExpert(String name) throws IntrospectionException
    {
        PropertyDescriptorEx pd = new PropertyDescriptorEx( name, beanClass );
        pd.setExpert( true );
        add( pd );
    }

    public void addExpert(String name, Class<?> editor) throws IntrospectionException
    {
        PropertyDescriptorEx pd = new PropertyDescriptorEx( name, beanClass );
        pd.setExpert( true );
        add( pd, editor );
    }

    public void addHidden(PropertyDescriptorEx pde, String hiddenMethodName) throws SecurityException, NoSuchMethodException
    {
        pde.setHidden( beanClass.getMethod( hiddenMethodName ) );
        add( pde );
    }

    public void addHidden(PropertyDescriptorEx pde, Class<?> editor, String hiddenMethodName) throws SecurityException,
            NoSuchMethodException
    {
        pde.setHidden( beanClass.getMethod( hiddenMethodName ) );
        add( pde, editor );
    }

    public void addHidden(String name, String hiddenMethodName) throws SecurityException, NoSuchMethodException, IntrospectionException
    {
        addHidden( new PropertyDescriptorEx( name, beanClass ), hiddenMethodName );
    }

    public void addReadOnly(String name, String readOnlyMethodName) throws SecurityException, NoSuchMethodException, IntrospectionException
    {
        addReadOnly( new PropertyDescriptorEx( name, beanClass ), readOnlyMethodName );
    }

    public void addReadOnly(PropertyDescriptorEx pde, String readOnlyMethodName) throws SecurityException, NoSuchMethodException
    {
        pde.setReadOnly( beanClass.getMethod( readOnlyMethodName ) );
        add( pde );
    }


    public void addHidden(String name, Class<?> editor, String hiddenMethodName) throws SecurityException, NoSuchMethodException,
            IntrospectionException
    {
        addHidden( new PropertyDescriptorEx( name, beanClass ), editor, hiddenMethodName );
    }

    public class PropertyDescriptorBuilder
    {
        protected final PropertyDescriptorEx pd;

        public PropertyDescriptorBuilder(String name)
        {
            try
            {
                this.pd = new PropertyDescriptorEx( name, beanClass );
            }
            catch( IntrospectionException e )
            {
                throw new RuntimeException( e );
            }
        }

        public PropertyDescriptorBuilder(PropertyDescriptorEx pd)
        {
            this.pd = pd;
        }

        public void add()
        {
            BeanInfoEx.this.add( pd );
        }

        protected PropertyDescriptorBuilder tagsFunction(Function<Object, Stream<String>> tagsSupplier)
        {
            pd.setValue( LambdaTagEditor.TAG_SUPPLIER_FUNCTION, tagsSupplier );
            pd.setPropertyEditorClass( LambdaTagEditor.class );
            return this;
        }

        public PropertyDescriptorBuilder tags(String ... tags)
        {
            return tagsFunction( bean -> Stream.of( tags ) );
        }

        public PropertyDescriptorBuilder editor(Class<?> editor)
        {
            pd.setPropertyEditorClass( editor );
            return this;
        }

        public PropertyDescriptorBuilder title(String title)
        {
            pd.setDisplayName( getResourceString( title ) );
            return this;
        }

        public PropertyDescriptorBuilder description(String description)
        {
            pd.setShortDescription( getResourceString( description ) );
            return this;
        }

        public PropertyDescriptorBuilder titleRaw(String title)
        {
            pd.setDisplayName( title );
            return this;
        }

        public PropertyDescriptorBuilder descriptionRaw(String description)
        {
            pd.setShortDescription( description );
            return this;
        }

        public PropertyDescriptorBuilder numberFormat(String pattern)
        {
            pd.setNumberFormat( pattern );
            return this;
        }

        public PropertyDescriptorBuilder hidden()
        {
            pd.setHidden( true );
            return this;
        }

        public PropertyDescriptorBuilder expert()
        {
            pd.setExpert( true );
            return this;
        }

        public PropertyDescriptorBuilder readOnly()
        {
            pd.setReadOnly( true );
            return this;
        }

        public PropertyDescriptorBuilder canBeNull()
        {
            pd.setCanBeNull( true );
            return this;
        }

        public PropertyDescriptorBuilder hidden(String methodName)
        {
            try
            {
                pd.setHidden( beanClass.getMethod( methodName ) );
            }
            catch( NoSuchMethodException | SecurityException e )
            {
                throw new RuntimeException( e );
            }
            return this;
        }

        public PropertyDescriptorBuilder readOnly(String methodName)
        {
            try
            {
                pd.setReadOnly( beanClass.getMethod( methodName ) );
            }
            catch( NoSuchMethodException | SecurityException e )
            {
                throw new RuntimeException( e );
            }
            return this;
        }

        public PropertyDescriptorBuilder simple()
        {
            pd.setSimple( true );
            return this;
        }
    }
}
