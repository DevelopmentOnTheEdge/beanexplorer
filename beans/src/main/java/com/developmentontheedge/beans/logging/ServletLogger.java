package com.developmentontheedge.beans.logging;

import javax.servlet.GenericServlet;

class ServletLogger extends AbstractMaskedLogger
{
    GenericServlet servlet;
    boolean noJCS;

    ServletLogger( GenericServlet servlet, int mask, boolean noJCS )
    {
        super( mask );
        this.servlet = servlet;
        this.noJCS = noJCS;
        servlet.log( "Servlet logging initialized with mask = " + mask );
        if( noJCS )
            servlet.log( "JCS messages are off" );
    }

    @Override
    protected final void doLog( LoggingHandle handle, String type, String message, Throwable details )
    {
        if( !doIsLogEnabled( handle, type ) )
             return;

        String handleStr = "" + handle.handle;
        if( noJCS && handleStr.startsWith( "org.apache.jcs" ) )
            return;

        String msg = type + ": " + handleStr;
        if( message != null )
             msg += ": " + message;
        if( details != null )
        {
           servlet.log( msg, details );
        }
        else
           servlet.log( msg );
    }
}
