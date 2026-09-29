package com.developmentontheedge.beans.model;

import java.lang.ref.WeakReference;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Model cache in {@link ComponentFactory}: one model per bean <em>instance</em>
 * (issue #10), found again even if the bean's hashCode() changes, and never
 * keeping a bean alive.
 */
public class ComponentFactoryCacheTest
{
    /** Equal by id, but each instance carries its own name. */
    public static class IdBean
    {
        private final String id;
        private String name;

        public IdBean(String id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof IdBean && ( (IdBean)obj ).id.equals( id );
        }

        @Override
        public int hashCode()
        {
            return id.hashCode();
        }
    }

    /** hashCode() follows a mutable field, like java.awt.Dimension. */
    public static class MutableHashBean
    {
        private String name;

        public MutableHashBean(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof MutableHashBean && ( (MutableHashBean)obj ).name.equals( name );
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }
    }

    @Test
    public void equalButDistinctBeansGetTheirOwnModels()
    {
        IdBean bean1 = new IdBean( "same", "first" );
        IdBean bean2 = new IdBean( "same", "second" );
        assertEquals( bean1, bean2 );

        ComponentModel model1 = ComponentFactory.getModel( bean1, ComponentFactory.Policy.UI );
        ComponentModel model2 = ComponentFactory.getModel( bean2, ComponentFactory.Policy.UI );

        assertNotSame( model1, model2 );
        assertSame( bean1, model1.getBean() );
        assertSame( bean2, model2.getBean() );
        assertEquals( "first", model1.findProperty( "name" ).getValue() );
        assertEquals( "second", model2.findProperty( "name" ).getValue() );
    }

    @Test
    public void writesGoToTheirOwnBean() throws Exception
    {
        IdBean bean1 = new IdBean( "same-w", "first" );
        IdBean bean2 = new IdBean( "same-w", "second" );
        ComponentFactory.getModel( bean1, ComponentFactory.Policy.UI );
        ComponentModel model2 = ComponentFactory.getModel( bean2, ComponentFactory.Policy.UI );

        model2.findProperty( "name" ).setValue( "edited" );

        assertEquals( "edited", bean2.getName() );
        assertEquals( "first", bean1.getName() );
    }

    @Test
    public void sameBeanInstanceHitsTheCache()
    {
        IdBean bean = new IdBean( "hit", "value" );
        ComponentModel model = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );

        assertSame( model, ComponentFactory.getModel( bean, ComponentFactory.Policy.UI ) );
        assertSame( model, ComponentFactory.getModel( bean ) );
    }

    @Test
    public void ignoreCacheStillCreatesAFreshModel()
    {
        IdBean bean = new IdBean( "fresh", "value" );
        ComponentModel cached = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
        ComponentModel fresh = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI, true );

        assertNotSame( cached, fresh );
        assertSame( cached, ComponentFactory.getModel( bean, ComponentFactory.Policy.UI ) );
    }

    @Test
    public void beanIsFoundAfterItsHashCodeChanges() throws Exception
    {
        MutableHashBean bean = new MutableHashBean( "before" );
        ComponentModel model = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
        int hashBefore = bean.hashCode();

        model.findProperty( "name" ).setValue( "after" );
        assertNotEquals( hashBefore, bean.hashCode() );

        assertSame( model, ComponentFactory.getModel( bean, ComponentFactory.Policy.UI ) );
    }

    @Test
    public void cacheDoesNotKeepBeansAlive()
    {
        WeakReference<IdBean> beanRef = cacheModelForUnreachableBean();

        assertTrue( "bean was retained by the model cache", awaitCleared( beanRef ) );
    }

    private static WeakReference<IdBean> cacheModelForUnreachableBean()
    {
        IdBean bean = new IdBean( "gc", "value" );
        assertNotNull( ComponentFactory.getModel( bean, ComponentFactory.Policy.UI ) );
        return new WeakReference<>( bean );
    }

    static boolean awaitCleared(WeakReference<?> ref)
    {
        for( int i = 0; i < 50 && ref.get() != null; i++ )
        {
            System.gc();
            try
            {
                Thread.sleep( 10 );
            }
            catch( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return ref.get() == null;
    }
}
