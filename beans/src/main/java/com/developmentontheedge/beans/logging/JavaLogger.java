package com.developmentontheedge.beans.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class JavaLogger extends AbstractLogger
{
    private final String fileHandlerPattern;
    private Method getInstanceMethodFromString;
    private Method loggingMethod;
    private Method isLoggableMethod;
    private final Map<String, Object> levels = new HashMap<>();
    public final static String MAIN_CLASS = "java.util.logging.Logger";

    JavaLogger(String fileHandlerPattern)
    {
        this.fileHandlerPattern = fileHandlerPattern;
        Class<?> clazz = java.util.logging.Logger.class;

        try
        {
            getInstanceMethodFromString = clazz.getMethod( "getLogger", String.class );

            Class<?> levelClass = java.util.logging.Level.class;

            loggingMethod = clazz.getMethod( "log", levelClass.getDeclaredField( "SEVERE" ).getType(), String.class,
                    Throwable.class );

            levels.put( FATAL_TYPE, levelClass.getDeclaredField( "SEVERE" ).get( null ) );
            levels.put( ERROR_TYPE, levelClass.getDeclaredField( "SEVERE" ).get( null ) );
            levels.put( WARN_TYPE, levelClass.getDeclaredField( "WARNING" ).get( null ) );
            levels.put( INFO_TYPE, levelClass.getDeclaredField( "INFO" ).get( null ) );
            levels.put( DEBUG_TYPE, levelClass.getDeclaredField( "FINE" ).get( null ) );

            isLoggableMethod = clazz.getMethod( "isLoggable", levelClass.getDeclaredField( "SEVERE" ).getType() );

        }
        catch( Exception exc )
        {
            System.err.println( "Cannot initialize Java Logging" );
            exc.printStackTrace( System.err );
        }
    }

    @Override
    public LoggingHandle getHandle(String requester)
    {
        try
        {
            Object loggingHandleSeed = getInstanceMethodFromString.invoke( null, new Object[] {requester} );
            Class<?> c = Class.forName( "com.beanexplorer.logging.JavaLoggerInitializer" );
            c.getMethod( "initHandlers", Object.class, String.class ).invoke( null, loggingHandleSeed, fileHandlerPattern );
            return new LoggingHandle( loggingHandleSeed );
        }
        catch( IllegalAccessException ignore )
        {
        }
        catch( InvocationTargetException ignore )
        {
        }
        catch( ClassNotFoundException ignore )
        {
        }
        catch( NoSuchMethodException ignore )
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
        catch( IllegalArgumentException exc )
        {
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
            return Boolean.TRUE.equals( isLoggableMethod.invoke( handle.handle, levels.get( type ) ) );
        }
        catch( IllegalArgumentException exc )
        {
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
