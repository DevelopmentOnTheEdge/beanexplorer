package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

import java.awt.Color;
import java.util.Arrays;

class TestBeans
{
    public static class FormPresentation
    {
        String title;
        DynamicPropertySet dps;

        FormPresentation(String title, DynamicPropertySet dps)
        {
            this.title = title;
            this.dps = dps;
        }

        public String getTitle()
        {
            return title;
        }

        public DynamicPropertySet getDps()
        {
            return dps;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public void setDps(DynamicPropertySet dps)
        {
            this.dps = dps;
        }
    }

    public static class FormPresentationBeanInfo extends BeanInfoEx
    {
        public FormPresentationBeanInfo()
        {
            super(TestBeans.FormPresentation.class);
        }

        @Override
        public void initProperties() throws Exception
        {
            add("title");
            add("dps");
        }
    }

    public static class SimpleBean
    {
        private long arr[];
        private String name;
        private int number;

        public SimpleBean() {}

        SimpleBean(String name, int number, long[] arr) {

            this.name = name;
            this.number = number;
            this.arr = arr;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        public long[] getArr() {
            return arr;
        }

        public void setArr(long[] arr)
        {
            this.arr = arr;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setNumber(int number)
        {
            this.number = number;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof SimpleBean)) return false;

            SimpleBean that = (SimpleBean) o;

            if (number != that.number) return false;
            if (!Arrays.equals(arr, that.arr)) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = arr != null ? Arrays.hashCode(arr) : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + number;
            return result;
        }
    }

    public static class SimpleBeanBeanInfo extends BeanInfoEx
    {
        public SimpleBeanBeanInfo()
        {
            super(TestBeans.SimpleBean.class);
        }

        @Override
        public void initProperties() throws Exception
        {
            add("arr");
            add("name");
            add("number");
        }
    }

    public static class BeanWithInnerClass
    {
        private InnerBeanClass arr[];
        private InnerBeanClass field1;

        BeanWithInnerClass(InnerBeanClass field1, InnerBeanClass[] arr) {
            this.field1 = field1;
            this.arr = arr;
        }

        public InnerBeanClass getField1() {return field1;}
        public InnerBeanClass[] getArr() {return arr;}
    }

    public static class InnerBeanClass
    {
        String name;

        public InnerBeanClass() {}

        InnerBeanClass(String name) {this.name = name;}
        public String getName() {return name;}

        public void setName(String name)
        {
            this.name = name;
        }
    }

}
