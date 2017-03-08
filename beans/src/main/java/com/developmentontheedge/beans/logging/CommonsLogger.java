package com.developmentontheedge.beans.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class CommonsLogger extends AbstractLogger
{
    private Method getInstanceMethodFromClass;
    private Method getInstanceMethodFromString;
    private final Map<String, Method> methods = new HashMap<>();
    private final Map<String, Method> enabledMethods = new HashMap<>();
    public final static String MAIN_CLASS = "org.apache.commons.logging.LogFactory";

    CommonsLogger()
    {
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName( MAIN_CLASS );
        }
        catch( ClassNotFoundException ignore )
        {
        }

        Class<?> logClass = null;
        try
        {
            logClass = Class.forName( "org.apache.commons.logging.Log" );
        }
        catch( ClassNotFoundException ignore )
        {
        }

        try
        {
            getInstanceMethodFromClass = clazz.getMethod( "getLog", Class.class );
            getInstanceMethodFromString = clazz.getMethod( "getLog", String.class );


            methods.put( FATAL_TYPE, logClass.getMethod( FATAL_TYPE, Object.class, Throwable.class ) );
            methods.put( ERROR_TYPE, logClass.getMethod( ERROR_TYPE, Object.class, Throwable.class ) );
            methods.put( WARN_TYPE, logClass.getMethod( WARN_TYPE, Object.class, Throwable.class ) );
            methods.put( INFO_TYPE, logClass.getMethod( INFO_TYPE, Object.class, Throwable.class ) );
            methods.put( DEBUG_TYPE, logClass.getMethod( DEBUG_TYPE, Object.class, Throwable.class ) );

            enabledMethods.put( FATAL_TYPE, logClass.getMethod( "isFatalEnabled" ) );
            enabledMethods.put( ERROR_TYPE, logClass.getMethod( "isErrorEnabled" ) );
            enabledMethods.put( WARN_TYPE, logClass.getMethod( "isWarnEnabled" ) );
            enabledMethods.put( INFO_TYPE, logClass.getMethod( "isInfoEnabled" ) );
            enabledMethods.put( DEBUG_TYPE, logClass.getMethod( "isDebugEnabled" ) );

        }
        catch( Exception exc )
        {
            System.err.println( "Cannot initialize CommonsLogger" );
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
    public LoggingHandle getHandle(Class<?> requester)
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
            methods.get( type ).invoke( handle.handle, message, details );
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
            return Boolean.TRUE.equals( enabledMethods.get( type ).invoke( handle.handle, new Object[0] ) );
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
