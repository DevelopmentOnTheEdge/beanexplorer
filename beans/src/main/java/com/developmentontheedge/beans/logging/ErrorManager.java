package com.developmentontheedge.beans.logging;

import java.io.Writer;

/**
 * Standard class for management by EnumeratedExceptions.
 *
 * Error codes for emunerated exceptions are stored and described in ErrorBundle.
 * Application should initialize ErrorBundle properly.
 *
 * @todo processing of errors inside ErrorManager
 */
public class ErrorManager
{
    private static ErrorBundle errorBundle = new ErrorBundleStub();

    public static ErrorBundle getErrorBundle()
    {
        return errorBundle;
    }

    public static void setErrorBundle(ErrorBundle errorBundle)
    {
        if( errorBundle != null )
            ErrorManager.errorBundle = errorBundle;
    }


    public static String describe(EnumeratedException e)
    {
        if( e == null )
            return null;

        e.setDescription( errorBundle.describe(e) );
        return e.getDescription();
    }

    public static void write(Writer out, EnumeratedException e)
    {
        if( e == null )
            return;

        try
        {
            out.write( describe(e) );
        }
        catch(Throwable t)
        {
            // to do
        }
    }

    public static void log(LoggingHandle handle, EnumeratedException e)
    {
        Logger.error( handle, "" + e, e);        
    }

    public static void write(Writer out, EnumeratedException e, LoggingHandle handle )
    {
        write(out, e);
        Logger.error( handle, "" + e, e );        
    }
}
