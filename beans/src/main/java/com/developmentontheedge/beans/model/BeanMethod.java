/** $Id: BeanMethod.java,v 1.4 2009/10/27 06:04:09 zha Exp $ */
package com.developmentontheedge.beans.model;

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanMethod
{
    public BeanMethod( Object owner, MethodDescriptor descriptor )
    {
        this.owner = owner;
        this.descriptor = descriptor;
        init();
    }

    protected void init()
    {
        StringBuffer result = new StringBuffer( descriptor.getDisplayName() );
        result.append( "(" );
        ParameterDescriptor []params = descriptor.getParameterDescriptors();
        if( params != null )
        {
            voidType = true;
            paramCount = params.length;
            for( int i = 0; i < params.length; i++ )
            {
                result.append( params[ i ].getDisplayName() );
                if( i < params.length - 1 )
                    result.append( ", " );
            }
        }
        else
        {
            Method meth = descriptor.getMethod();
            if( meth != null )
            {
                voidType = meth.getReturnType().equals(Void.TYPE);
                Class<?>[] pars = meth.getParameterTypes();
                paramCount = pars.length;
                for( int i = 0; i < pars.length; i++ )
                {
                    String pclass = pars[ i ].getName();
                    StringBuffer arr = new StringBuffer( "" );
                    for( int l = 0; ; l++ )
                    {
                        if( pclass.substring( l, 1 ).equals( "[" ) )
                            arr.append( "[]" );
                        else
                        {
                            pclass = pclass.substring( l );
                            break;
                        }
                    }

                    int index = pclass.lastIndexOf( "." );
                    if( index > 0 )
                    {
                        pclass = pclass.substring( index + 1 );
                    }

                    result.append( pclass + arr.toString() );

                    if( i < pars.length - 1 )
                        result.append( ", " );
                }
            }
        }
        result.append( ")" );
        displayName = result.toString();
    }

    public String getName(){ return descriptor.getName(); }

    private String displayName;
    public String getDisplayName() { return displayName; }

    private final MethodDescriptor descriptor;
    public MethodDescriptor getDescriptor(){ return descriptor; }

    private final Object owner;
    public Object getOwner(){ return owner; }

    /**
     * @pending Do it right! Now it assumes void method without parameters
     */
    public Object invoke()
    {
        try
        {
            descriptor.getMethod().invoke( owner, ( Object[] )null );
        }
        catch( IllegalAccessException ignore ) {}
        catch( InvocationTargetException ignore ) {}
        return null;
    }

    private boolean voidType = true;
    public boolean isVoidType(){ return voidType; }

    private int paramCount = 0;
    public int getParamCount(){ return paramCount; }
}
