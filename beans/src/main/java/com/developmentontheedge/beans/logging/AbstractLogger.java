package com.developmentontheedge.beans.logging;

abstract public class AbstractLogger
{
    public final static String FATAL_TYPE = "fatal";
    public final static String ERROR_TYPE = "error";
    public final static String WARN_TYPE =  "warn";
    public final static String INFO_TYPE =  "info";
    public final static String DEBUG_TYPE = "debug";

    public final static int MASK_FATAL = 0x01;
    public final static int MASK_ERROR = 0x02;
    public final static int MASK_WARN = 0x04;
    public final static int MASK_INFO = 0x08;
    public final static int MASK_DEBUG = 0x10;

    public LoggingHandle getHandle( String requester )
    {
        return new LoggingHandle( requester );
    }

    public LoggingHandle getHandle( Class<?> requester )
    {
        return new LoggingHandle( requester );
    }

    public boolean isFatalEnabled( LoggingHandle handle )
    {
        return doIsLogEnabled( handle, FATAL_TYPE );
    }

    public void fatal( LoggingHandle handle, String message, Throwable details )
    {
        doLog( handle, FATAL_TYPE, message, details );
    }

    public boolean isErrorEnabled( LoggingHandle handle )
    {
        return doIsLogEnabled( handle, ERROR_TYPE );
    }

    public void error( LoggingHandle handle, String message, Throwable details )
    {
        doLog( handle, ERROR_TYPE, message, details );
    }

    public boolean isWarnEnabled( LoggingHandle handle )
    {
        return doIsLogEnabled( handle, WARN_TYPE );
    }

    public void warn( LoggingHandle handle, String message, Throwable details )
    {
        doLog( handle, WARN_TYPE, message, details );
    }

    public boolean isInfoEnabled( LoggingHandle handle )
    {
        return doIsLogEnabled( handle, INFO_TYPE );
    }

    public void info( LoggingHandle handle, String message, Throwable details )
    {
        doLog( handle, INFO_TYPE, message, details );
    }

    public boolean isDebugEnabled( LoggingHandle handle )
    {
        return doIsLogEnabled( handle, DEBUG_TYPE );
    }

    public void debug( LoggingHandle handle, String message, Throwable details )
    {
        doLog( handle, DEBUG_TYPE, message, details );
    }

    protected void doLog( LoggingHandle handle, String type, String message, Throwable details )
    {
    }

    protected boolean doIsLogEnabled( LoggingHandle handle, String type )
    {
        return true;
    }

}