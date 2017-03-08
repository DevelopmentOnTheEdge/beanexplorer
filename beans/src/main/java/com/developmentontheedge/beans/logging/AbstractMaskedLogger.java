package com.developmentontheedge.beans.logging;

abstract class AbstractMaskedLogger extends AbstractLogger
{
    int mask;

    AbstractMaskedLogger( int mask )
    {
        this.mask = mask;
    }

    @Override
    protected boolean doIsLogEnabled( LoggingHandle handle, String type )
    {
        if( DEBUG_TYPE.equals( type ) && ( mask & MASK_DEBUG ) > 0 )
            return true;
        if( INFO_TYPE.equals( type ) && ( mask & MASK_INFO ) > 0 )
            return true;
        if( WARN_TYPE.equals( type ) && ( mask & MASK_WARN ) > 0 )
            return true;
        if( ERROR_TYPE.equals( type ) && ( mask & MASK_ERROR ) > 0 )
            return true;
        if( FATAL_TYPE.equals( type ) && ( mask & MASK_FATAL ) > 0 )
            return true;
        return false;
    }
}
