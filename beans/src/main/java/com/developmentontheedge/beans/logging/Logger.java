package com.developmentontheedge.beans.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * First rather ugly implementation.
 */
public class Logger
{
    private static AbstractLogger loggerImpl = null;

    static
    {
        // just in case
        if ( loggerImpl == null )
            initialize();
    }

    private static void initialize()
    {
        // try to find available loggers
        if (!useServletLogging())
            if (!useLOG4J())
                if (!useCommonsLogging())
                    if (!useJavaLogging())
                        useConsole();
    }

    public static boolean isServletLoggingUsed()
    {
        // to skip dependence from servlet.jar - use reflection
        try
        {
            Class<?> c = Class.forName( "com.beanexplorer.logging.ServletLogger" );
            return loggerImpl != null && c.isInstance(loggerImpl) ;
        }
        catch(ClassNotFoundException e){}

        return false;
    }

    public static boolean isJavaLoggingUsed()
    {
        return loggerImpl != null && loggerImpl instanceof JavaLogger;
    }

    public static boolean isCommonsLoggingUsed()
    {
        return loggerImpl != null && loggerImpl instanceof CommonsLogger;
    }

    public static boolean isLOG4JUsed()
    {

        return loggerImpl != null && loggerImpl instanceof LOG4JLogger;
    }

    public static boolean isConsoleUsed()
    {
        return loggerImpl != null && loggerImpl instanceof ConsoleLogger;
    }

    public static boolean useServletLogging()
    {
        // to skip dependence from servlet.jar - use reflection
        try
        {
            Class<?> c = Class.forName( "com.beanexplorer.logging.ServletLoggerInitializer" );
            AbstractLogger servletLogger = null;
            servletLogger = ( AbstractLogger ) c.getMethod( "getLogger", ( Class[] )null ).invoke( null );
            if ( servletLogger != null )
            {
                setImpl( servletLogger );
                return true;
            }
        }
        catch ( Throwable t )
        {
        }
        return false;
    }

    public static boolean useLOG4J()
    {
        try
        {
            Class.forName( LOG4JLogger.MAIN_CLASS );
            setImpl( new LOG4JLogger() );
            return true;
        }
        catch ( ClassNotFoundException ignore )
        {
        }
        return false;
    }

    public static boolean useCommonsLogging()
    {
        try
        {
            Class.forName( CommonsLogger.MAIN_CLASS );
            if (!"com.beanexplorer.logging.BeanExplorerLoggingWrapperForCommonsLogging".equals(
                            System.getProperty( "org.apache.commons.logging.Log" ) ) )
            {
                setImpl( new CommonsLogger() );
                return true;
            }
        }
        catch (ClassNotFoundException ignore)
        {
        }
        return false;
    }

    public static boolean useJavaLogging()
    {
        return useJavaLogging( null );
    }

    public static boolean useJavaLogging(String filePattern)
    {
        // Java Logging available since JDK 1.4
        try
        {
            Class.forName( JavaLogger.MAIN_CLASS );
            setImpl( new JavaLogger(filePattern) );
            return true;
        }
        catch ( ClassNotFoundException ignore )
        {
        }
        return false;
    }

    public static boolean useConsole()
    {
        setImpl( new ConsoleLogger() );
        return true;
    }

    public static boolean useConsole( boolean redirectOut2Err )
    {
        setImpl( new ConsoleLogger( redirectOut2Err ) );
        return true;
    }

    //
    public static synchronized void setImpl( AbstractLogger impl )
    {
        loggerImpl = impl;
    }

    public static LoggingHandle getHandle( String requester )
    {
        if ( loggerImpl == null )
            initialize();
        return loggerImpl.getHandle( requester );
    }

    public static LoggingHandle getHandle( Class<?> requester )
    {
        if ( loggerImpl == null )
            initialize();
        return loggerImpl.getHandle( requester );
    }

    public static boolean isFatalEnabled( LoggingHandle handle )
    {
        if ( handle.isFatalEnabled != null )
            return handle.isFatalEnabled.booleanValue();

        boolean ret = loggerImpl.isFatalEnabled( handle );
        handle.isFatalEnabled = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static void fatal( LoggingHandle handle, String message, Throwable details )
    {
        trimStackTrace( details );
        loggerImpl.fatal( handle, message, details );
    }

    public static void fatal( LoggingHandle handle, String message )
    {
        fatal( handle, message, null );
    }

    public static boolean isErrorEnabled( LoggingHandle handle )
    {
        if ( handle.isErrorEnabled != null )
            return handle.isErrorEnabled.booleanValue();

        boolean ret = loggerImpl.isErrorEnabled( handle );
        handle.isErrorEnabled = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static void error( LoggingHandle handle, String message, Throwable details )
    {
        trimStackTrace( details );
        loggerImpl.error( handle, message, details );
    }

    public static void error( LoggingHandle handle, String message )
    {
        error( handle, message, null );
    }

    public static boolean isWarnEnabled( LoggingHandle handle )
    {
        if ( handle.isWarnEnabled != null )
            return handle.isWarnEnabled.booleanValue();

        boolean ret = loggerImpl.isWarnEnabled( handle );
        handle.isWarnEnabled = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static void warn( LoggingHandle handle, String message, Throwable details )
    {
        trimStackTrace( details );
        loggerImpl.warn( handle, message, details );
    }

    public static void warn( LoggingHandle handle, String message )
    {
        warn( handle, message, null );
    }

    public static boolean isInfoEnabled( LoggingHandle handle )
    {
        if ( handle.isInfoEnabled != null )
            return handle.isInfoEnabled.booleanValue();

        boolean ret = loggerImpl.isInfoEnabled( handle );
        handle.isInfoEnabled = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static void info( LoggingHandle handle, String message, Throwable details )
    {
        trimStackTrace( details );
        loggerImpl.info( handle, message, details );
    }

    public static void info( LoggingHandle handle, String message )
    {
        info( handle, message, null );
    }

    public static boolean isDebugEnabled( LoggingHandle handle )
    {
        if ( handle.isDebugEnabled != null )
            return handle.isDebugEnabled.booleanValue();

        boolean ret = loggerImpl.isDebugEnabled( handle );
        handle.isDebugEnabled = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static void debug( LoggingHandle handle, String message, Throwable details )
    {
        trimStackTrace( details );
        loggerImpl.debug( handle, message, details );
    }

    public static void debug( LoggingHandle handle, String message )
    {
        debug( handle, message, null );
    }

    public static void setMaskIfPossible( int mask )
    {
        if ( loggerImpl instanceof AbstractMaskedLogger )
            ( ( AbstractMaskedLogger ) loggerImpl ).mask = mask;
    }

    protected static void trimStackTrace( Throwable exc )
    { 
        if( exc == null )
        {
            return; 
        }

        List<StackTraceElement> stackList = new ArrayList<>( Arrays.asList( exc.getStackTrace() ) );
        for( int cnt = 0; cnt < stackList.size(); cnt++ )
        {
            String str = stackList.get( cnt ).toString();
            if( str.startsWith( "org.apache.catalina.core." ) )
            {
                exc.setStackTrace( stackList.subList( 0, cnt ).toArray( new StackTraceElement[ 0 ] ) );
                return;
            }
        }
    }     
}
