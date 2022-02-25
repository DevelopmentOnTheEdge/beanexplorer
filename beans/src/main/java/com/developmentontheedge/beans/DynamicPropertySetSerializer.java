package com.developmentontheedge.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.developmentontheedge.beans.log.Logger;

/**
 * Allows serialize DynamicPropertySet in XML format. 
 *
 * @pending stored property can be only one of the allowed type:
 * String, Boolean, Int, Long, Float, Double, Preferences (this allows
 * to organise preferences in tree structure).
 */
public class DynamicPropertySetSerializer
{
    private static String encoding = "UTF-8";

    private static final Pattern OLD_PREFIX = Pattern.compile( "^com\\.beanexplorer\\.(beans\\.)" );
    private static final String NEW_PREFIX = "com.developmentontheedge.beans.";
    
    protected static final String DYNAMIC_PROPERTY_SET_ELEMENT = "dynamicPropertySet";
    protected static final String PROPERTY_ELEMENT = "property";

    protected static final String NAME_ATTR = "name";
    protected static final String TYPE_ATTR = "type";
    protected static final String VALUE_ATTR = "value";
    protected static final String DISPLAY_NAME_ATTR = "display-name";
    protected static final String SHORT_DESCRIPTION_ATTR = "short-description";
    protected static final String PROPERTY_EDITOR_ATTR = "property-editor";
    protected static final String HIDDEN_ATTR = "hidden";
    protected static final String EXPERT_ATTR = "expert";
    protected static final String READ_ONLY_ATTR = "read-only";
    protected static final String ORDERED = "ordered";

    protected Map registry = null;

    protected void error(String msg, Throwable t)
    {
        Logger.getLogger().error("Error: " + msg + ( t == null ? "" : ", exception - " + t ), t);
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    //

    DynamicPropertySet dpsLoaded;

    public void load(DynamicPropertySet dps, InputStream in, Map registry)
    {
        this.registry = registry;
        load(dps, in);
    }

    public void load(DynamicPropertySet dps, InputStream in)
    {
        dpsLoaded = dps;

        load(in, getClass().getClassLoader());
    }

    public void load(DynamicPropertySet dps, InputStream in, ClassLoader classLoader)
    {
        dpsLoaded = dps;

        load(in, classLoader);
    }

    public void load(InputStream in, ClassLoader classLoader)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler(classLoader);
            saxParser.parse(new InputSource(in), handler);
        }
        catch( Throwable t )
        {
            error("Could not parse", t);
        }
    }

    public void load(DynamicPropertySet dps, String fileName)
    {
        load(dps, fileName, getClass().getClassLoader());
    }

    public void load(DynamicPropertySet dps, String fileName, ClassLoader classLoader)
    {
        dpsLoaded = dps;

        File file = new File(fileName);
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SaxHandler saxHandler = new SaxHandler(classLoader);
            saxParser.parse(new File(fileName), saxHandler);
        }
        catch( Throwable t )
        {
            error("could not load preferences, file=" + file.getAbsolutePath(), t);
        }
    }

    protected class SaxHandler extends DefaultHandler
    {
        protected DynamicPropertySet dps;
        protected DynamicProperty property;
        protected Stack<DynamicPropertySet> parents = new Stack<>();
        protected ClassLoader classLoader;

        public SaxHandler(ClassLoader loader)
        {
            classLoader = loader;
            if( classLoader == null )
                classLoader = getClass().getClassLoader();
        }

        @Override
        public void startElement(String namespaceURI, String name, String qualifiedName, Attributes attrs) throws SAXException
        {
            if( name.length() == 0 )
                name = qualifiedName;

            if( DYNAMIC_PROPERTY_SET_ELEMENT.equals(name) )
            {
                if( dps == null )
                    dps = dpsLoaded;
                else
                {
                    parents.push(dps);

                    try
                    {
                        dps = (DynamicPropertySet)property.getType().newInstance();
                    }
                    catch( Throwable t )
                    {
                        DynamicPropertySetSerializer.this.error("Can instantiate DyanamicPropertySet, className=" + property.getType(), t);
                    }

                    // for backward compatibility
                    if( dps == null )
                        dps = new Preferences();

                    if( dps instanceof Preferences && property.getDescriptor().getValue(ORDERED) != null )
                    {
                        ( (Preferences)dps ).setSaveOrder(true);
                    }

                    property.setValue(dps);
                }
            }

            else if( PROPERTY_ELEMENT.equals(name) )
            {
                readProperty(attrs);
            }

            else
                DynamicPropertySetSerializer.this.error("Unknown tag <" + name + ">", null);
        }

        @Override
        public void endElement(String namespaceURI, String name, String qualifiedName) throws SAXException
        {
            if( name.length() == 0 )
                name = qualifiedName;

            if( DYNAMIC_PROPERTY_SET_ELEMENT.equals(name) )
            {
                if( parents.empty() )
                    dps = dpsLoaded;
                else
                    dps = parents.pop();
            }
        }

        protected void readProperty(Attributes attrs)
        {
            String name = attrs.getValue(NAME_ATTR);

            String strType = null;
            Object value = null;

            PropertyDescriptor descriptor = null;
            if( registry != null && registry.containsKey(name) )
            {
                DynamicProperty dp = (DynamicProperty)registry.get(name);
                descriptor = dp.getDescriptor();
                strType = dp.getType().getName();
            }
            else
            {
                try
                {
                    descriptor = new PropertyDescriptor(attrs.getValue(NAME_ATTR), null, null);
                }
                catch( IntrospectionException e )
                {
                    throw new RuntimeException( e );
                }
            }

            for( int i = 0; i < attrs.getLength(); i++ )
            {
                String attr = attrs.getLocalName(i);
                if( attr.length() == 0 )
                    attr = attrs.getQName(i);

                String valueStr = attrs.getValue(i);
                if( NAME_ATTR.equals(attr) )
                {
                    // do nothing - already processed
                }
                else if( TYPE_ATTR.equals(attr) )
                {
                    strType = valueStr;
                }
                else if( VALUE_ATTR.equals(attr) )
                {
                    try
                    {
                        try
                        {
                            if( strType.equals(String.class.getName()) )
                            {
                                value = valueStr.replaceAll("\\\\n", "\n");
                            }
                            else if( strType.equals(Boolean.class.getName()) )
                            {
                                value = Boolean.parseBoolean(valueStr);
                            }
                            else if( strType.equals(Integer.class.getName()) )
                            {
                                value = Integer.parseInt(valueStr);
                            }
                            else if( strType.equals(Double.class.getName()) )
                            {
                                value = Double.parseDouble(valueStr);
                            }
                        }
                        catch( NumberFormatException e )
                        {
                        }
                        if(value == null)
                        {
                            byte[] encodedBytes = Base64.getDecoder().decode( valueStr );
                            ObjectInputStreamWithLoader ois = new ObjectInputStreamWithLoader(new ByteArrayInputStream(encodedBytes),
                                    Thread.currentThread().getContextClassLoader());
                            value = ois.readObject();
                            ois.close();
                        }
                    }
                    catch( Exception e )
                    {
                        DynamicPropertySetSerializer.this.error("Could not load object from property " + descriptor.getName(), e);
                    }
                }
                else if( DISPLAY_NAME_ATTR.equals(attr) )
                    descriptor.setDisplayName(valueStr);

                else if( SHORT_DESCRIPTION_ATTR.equals(attr) )
                    descriptor.setShortDescription(valueStr);

                else if( HIDDEN_ATTR.equals(attr) )
                    descriptor.setHidden("true".equalsIgnoreCase(valueStr));

                else if( EXPERT_ATTR.equals(attr) )
                    descriptor.setExpert("true".equalsIgnoreCase(valueStr));

                else if( READ_ONLY_ATTR.equals(attr) )
                    descriptor.setValue(BeanInfoEx.READ_ONLY, "true".equalsIgnoreCase(valueStr));

                else if( PROPERTY_EDITOR_ATTR.equals(attr) )
                {
                    try
                    {
                        Class<?> c = classLoader.loadClass(OLD_PREFIX.matcher( valueStr ).replaceFirst( NEW_PREFIX ));
                        descriptor.setPropertyEditorClass(c);
                    }
                    catch( ClassNotFoundException e )
                    {
                        DynamicPropertySetSerializer.this.error("Could not load property editor class " + valueStr + " for property "
                                + descriptor.getName(), e);
                    }
                }
                else if( ORDERED.equals(attr) )
                    descriptor.setValue(ORDERED, "true".equalsIgnoreCase(valueStr));

                // just put unknown attributes into desriptor attributes
                else
                    descriptor.setValue(attr, valueStr);
            }

            if( strType == null )
            {
                DynamicPropertySetSerializer.this.error("Property type is missing for property " + descriptor.getName(), null);
                return;
            }

            Class<?> type = null;
            try
            {
                type = classLoader.loadClass(OLD_PREFIX.matcher( strType ).replaceFirst( NEW_PREFIX ));
            }
            catch( ClassNotFoundException e )
            {
                DynamicPropertySetSerializer.this.error(
                        "Could not find property value class'" + strType + "', property " + descriptor.getName(), e);
                return;
            }

            property = new DynamicProperty(descriptor, type, value);
            dps.add(property);

            return;
        }
    }

    //----- write preferences ----------------------------------------/

    protected int indentation = 0;
    protected int indent = 4;
    protected OutputStream out;

    public void save(String fileName, DynamicPropertySet dps) throws IOException
    {
        save(new FileOutputStream(fileName), dps);
    }

    /**
     * Writes preferences in XML format.
     *
     * @pending
     */
    public void save(OutputStream out, DynamicPropertySet dps, Map registry) throws IOException
    {
        this.registry = registry;
        save(out, dps);
    }

    /**
     * Writes preferences in XML format.
     *
     * @pending
     */
    public void save(OutputStream out, DynamicPropertySet dps) throws IOException
    {
        this.out = out;
        if( dps.asMap().size() > 0 )
        {
            writeln("<?xml version=" + quote("1.0") + " encoding=" + quote(encoding) + "?>");
            writeDynamicPropertySet(dps);
        }
    }

    protected void writeDynamicPropertySet(DynamicPropertySet dps) throws IOException
    {
        writeln("<" + DYNAMIC_PROPERTY_SET_ELEMENT + ">");
        indentation += indent;

        Iterator i = dps.propertyIterator();
        while( i.hasNext() )
        {
            writeProperty((DynamicProperty)i.next());
        }

        indentation -= indent;
        writeln("</" + DYNAMIC_PROPERTY_SET_ELEMENT + ">");
    }

    protected void writeProperty(DynamicProperty property) throws IOException
    {
        write("<" + PROPERTY_ELEMENT);

        boolean fullRecord = true;
        if( registry != null && registry.containsKey(property.getName()) )
            fullRecord = false;

        writeAttribute(NAME_ATTR, property.getName());
        if( fullRecord )
            writeAttribute(TYPE_ATTR, property.getType().getName());

        Object value = property.getValue();
        String strValue = null;
        try
        {
            if( value instanceof String )
            {
                strValue = ( (String)value ).replaceAll("\\n", "\\\\n");
            }
            else if(value instanceof Boolean)
            {
                strValue = ((Boolean)value).toString();
            }
            else if(value instanceof Integer)
            {
                strValue = ((Integer)value).toString();
            }
            else if(value instanceof Double)
            {
                strValue = ((Double)value).toString();
            }
            else
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(value);
                byte bytes[] = os.toByteArray();
                strValue = Base64.getEncoder().encodeToString( bytes );
                oos.close();
            }
        }
        catch( Exception e )
        {
            error("Cannot serialize value of property "+property.getName(), e);
        }
        if( value != null && ! ( value instanceof Preferences ) )
            writeAttribute(VALUE_ATTR, "" + strValue);

        if( fullRecord )
        {
            PropertyDescriptor descriptor = property.getDescriptor();
            writeAttribute(DISPLAY_NAME_ATTR, descriptor.getDisplayName());
            writeAttribute(SHORT_DESCRIPTION_ATTR, descriptor.getShortDescription());

            if( descriptor.getPropertyEditorClass() != null )
                writeAttribute(PROPERTY_EDITOR_ATTR, descriptor.getPropertyEditorClass().getName());

            if( descriptor.isHidden() )
                writeAttribute(HIDDEN_ATTR, "true");

            if( descriptor.isExpert() )
                writeAttribute(EXPERT_ATTR, "true");

            if( booleanFeature(descriptor, BeanInfoEx.READ_ONLY) )
                writeAttribute(READ_ONLY_ATTR, "true");
        }
        if( ! ( value instanceof Preferences ) )
        {
            out.write("/>".getBytes(encoding));
            out.write(" \n".getBytes(encoding));
        }
        else
        {
            if( ( (Preferences)value ).isSaveOrder() )
            {
                writeAttribute(ORDERED, "true");
            }

            out.write(">\n".getBytes(encoding));

            indentation += indent;
            writeDynamicPropertySet((Preferences)value);
            indentation -= indent;

            writeln("</" + PROPERTY_ELEMENT + ">");
        }
    }

    protected void writeAttribute(String attr, String value) throws IOException
    {
        out.write( ( " " + attr + "=" + quote(value) ).getBytes(encoding));
    }

    /**
     * @pending method support
     */
    protected boolean booleanFeature(PropertyDescriptor descriptor, String feature)
    {
        Object value = descriptor.getValue(feature);

        if( value instanceof Boolean )
            return ( (Boolean)value ).booleanValue();

        // pending
        //if (value instanceof Method)
        //  value = readValue((Method)value);

        return false;
    }

    protected void write(String exp) throws IOException
    {
        for( int i = 0; i < indentation; i++ )
            out.write(' ');

        out.write(exp.getBytes(encoding));
    }

    protected void writeln(String exp) throws IOException
    {
        write(exp);
        out.write(" \n".getBytes(encoding));
    }

    protected String quote(String s)
    {
        return "\"" + quoteCharacters(s) + "\"";
    }

    public static String quoteCharacters(String s)
    {
        StringBuffer result = null;
        for( int i = 0, max = s.length(), delta = 0; i < max; i++ )
        {
            char c = s.charAt(i);
            String replacement = null;

            if( c == '&' )
                replacement = "&amp;";
            else if( c == '<' )
                replacement = "&lt;";
            else if( c == '\r' )
                replacement = "&#13;";
            else if( c == '>' )
                replacement = "&gt;";
            else if( c == '"' )
                replacement = "&quot;";
            else if( c == '\'' )
                replacement = "&apos;";

            if( replacement != null )
            {
                if( result == null )
                    result = new StringBuffer(s);

                result.replace(i + delta, i + delta + 1, replacement);
                delta += ( replacement.length() - 1 );
            }
        }

        if( result == null )
            return s;

        return result.toString();
    }
}
