package com.developmentontheedge.beans.logging;

/**
 * Stub implementation of ErrorBundle.
 */
public class ErrorBundleStub implements ErrorBundle
{
    @Override
    public String getFormat(int code)           
    { 
        return null; 
    }

    @Override
    public String getDescription(int code)      
    { 
        return null; 
    }


    @Override
    public String describe(EnumeratedException e)
    {
        if( e == null )
            return null;

        return e.toString();
    }

}
