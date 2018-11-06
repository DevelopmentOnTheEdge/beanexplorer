package com.developmentontheedge.beans.integration.composite;

import java.util.function.Function;
import java.util.stream.Stream;

import com.developmentontheedge.beans.BeanInfoEx;

public class BeanInfoExGeneric<T> extends BeanInfoEx
{
    protected BeanInfoExGeneric()
    {
        super();
    }
    /**
     * @param beanClass
     */
    public BeanInfoExGeneric(Class<? extends T> beanClass)
    {
        super(beanClass, true);
    }

    @Override
    public PropertyDescriptorBuilder2 property(String name)
    {
        return new PropertyDescriptorBuilder2( name );
    }

    public class PropertyDescriptorBuilder2 extends PropertyDescriptorBuilder
    {
        public PropertyDescriptorBuilder2(String name)
        {
            super(name);
        }

        public void add()
        {
            BeanInfoExGeneric.this.add(pd);
        }

        public void add(int i)
        {
            BeanInfoExGeneric.this.add(i, pd);
        }

        @SuppressWarnings ( "unchecked" )
        public PropertyDescriptorBuilder2 tags(Function<T, Stream<String>> tagsSupplier)
        {
            tagsFunction( (Function<Object, Stream<String>>)tagsSupplier );
            return this;
        }

        @Override
        public PropertyDescriptorBuilder2 tags(String... tags)
        {
            return tags( bean -> Stream.of(tags) );
        }
    }
}
