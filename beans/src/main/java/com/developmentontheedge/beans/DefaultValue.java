package com.developmentontheedge.beans;

/**
 * This interface is used to provide default values for null properties
 * that will be returned by clone() method. Object implementing 
 * this interface must be assigned to 
 * {@link BeanInfoConstants#DEFAULT_VALUE}
 * attribute
 */
public interface DefaultValue
{
    Object clone();
}
