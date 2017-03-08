/** $Id: LOG4JLogger.java,v 1.8 2006/04/25 07:03:25 zha Exp $ */

package com.developmentontheedge.beans.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class LOG4JLogger extends AbstractLogger
{
    private Method getInstanceMethodFromClass;
    private Method getInstanceMethodFromString;
    private Method loggingMethod;
    private Method isEnabledForMethod;
    private final Map<String, Object> levels = new HashMap<>();
    public final static String MAIN_CLASS = "org.apache.log4j.Category";

    LOG4JLogger()
    {
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName( MAIN_CLASS );
        }
        catch( ClassNotFoundException ignore )
        {
        }

        try
        {
            getInstanceMethodFromClass = clazz.getMethod( "getInstance", Class.class );
            getInstanceMethodFromString = clazz.getMethod( "getInstance", String.class );

            Class<?> priorityClass = null;
            try
            {
                priorityClass = Class.forName( "org.apache.log4j.Priority" );
            }
            catch( ClassNotFoundException ignore )
            {
            }

            loggingMethod = clazz.getMethod( "log", priorityClass.getDeclaredField( "FATAL" ).getType(), Object.class, Throwable.class );

            levels.put( FATAL_TYPE, priorityClass.getDeclaredField( "FATAL" ).get( null ) );
            levels.put( ERROR_TYPE, priorityClass.getDeclaredField( "ERROR" ).get( null ) );
            levels.put( WARN_TYPE, priorityClass.getDeclaredField( "WARN" ).get( null ) );
            levels.put( INFO_TYPE, priorityClass.getDeclaredField( "INFO" ).get( null ) );
            levels.put( DEBUG_TYPE, priorityClass.getDeclaredField( "DEBUG" ).get( null ) );

            isEnabledForMethod = clazz.getMethod( "isEnabledFor", priorityClass.getDeclaredField( "FATAL" ).getType() );
        }
        catch( Exception exc )
        {
            System.err.println( "Cannot initialize LOG4J" );
            exc.printStackTrace( System.err );
        }
    }

    @Override
    public LoggingHandle getHandle(String requester)
    {
        try
        {
            return new LoggingHandle( getInstanceMethodFromString.invoke( null, requester ) );
        }
        catch( IllegalAccessException exc )
        {
        }
        catch( InvocationTargetException exc )
        {
        }
        return null;
    }

    @Override
    public LoggingHandle getHandle(Class requester)
    {
        try
        {
            return new LoggingHandle( getInstanceMethodFromClass.invoke( null, requester ) );
        }
        catch( IllegalAccessException exc )
        {
        }
        catch( InvocationTargetException exc )
        {
        }
        return null;
    }

    @Override
    protected void doLog(LoggingHandle handle, String type, String message, Throwable details)
    {
        try
        {
            loggingMethod.invoke( handle.handle, levels.get( type ), message, details );
        }
        catch( IllegalAccessException exc )
        {
        }
        catch( InvocationTargetException exc )
        {
        }
    }

    @Override
    protected boolean doIsLogEnabled(LoggingHandle handle, String type)
    {
        try
        {
            return Boolean.TRUE.equals( isEnabledForMethod.invoke( handle.handle, levels.get( type ) ) );
        }
        catch( IllegalAccessException exc )
        {
        }
        catch( InvocationTargetException exc )
        {
        }
        return false;
    }
}
