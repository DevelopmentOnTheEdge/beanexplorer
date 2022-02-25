package com.developmentontheedge.beans;

/*
 * @(#)ObjectInputStreamWithLoader.java 4.21 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// Java import
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class ObjectInputStreamWithLoader extends ObjectInputStream
{
    private final ClassLoader loader;


    public ObjectInputStreamWithLoader(InputStream in, ClassLoader theLoader) throws IOException
    {
        super(in);
        this.loader = theLoader;
    }

    @Override
    protected Class resolveClass(ObjectStreamClass aClass) throws IOException, ClassNotFoundException
    {
        if( loader == null )
        {
            return super.resolveClass(aClass);
        }
        else
        {
            String name = aClass.getName();
    	    try
    	    {
    	        return loader.loadClass(name);
    	    }
    	    catch(ClassNotFoundException e)
    	    {
    	    }
    	    return Class.forName(name, false, loader);
        }
    }
}
