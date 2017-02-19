package com.developmentontheedge.beans.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.developmentontheedge.beans.BeanInfoEx;

/**
 * Annotation to automatically set property display name for the bean using {@link BeanInfoEx}
 * @author lan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface PropertyName
{
    String value();
}
