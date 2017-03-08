package com.developmentontheedge.beans.logging;

/**
 * Standard class for classified exceptions in BeanExplorer and related applications.
 *
 * Generally EnumeratedExceptions are processed by ErrorManager.
 *
 */
public class EnumeratedException extends Exception
{
    public EnumeratedException(int code, Object[] params, Throwable exception)
    {
        super( exception );

        this.code = code;
        this.params = params;
    }

    private final int code;
    public int getCode()
    {
        return code;
    }

    private final Object[] params;
    public Object[] getParams()
    {
        return params;
    }

    /** Error description from ErrorBundle. */
    protected String description;
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }


    @Override
    public String toString()
    {
        if( description != null )
            return description;

        StringBuilder buf = new StringBuilder( "Enumerated exception: code=" + code );
        buf.append( "\r\n  cause: " + getCause() + "." );

        if( params != null && params.length > 0 )
        {
            buf.append( "\r\n  Parameters: \r\n" );

            for( int i = 0; i < params.length; i++ )
            {
                buf.append( "    " + ( i + 1 ) + " - " );
                buf.append( params[i] );
                buf.append( "\r\n" );
            }
        }

        return buf.toString();
    }

}