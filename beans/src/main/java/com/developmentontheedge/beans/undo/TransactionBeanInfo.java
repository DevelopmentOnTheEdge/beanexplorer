package com.developmentontheedge.beans.undo;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class TransactionBeanInfo extends BeanInfoEx
{
    public TransactionBeanInfo()
    {
        super(Transaction.class, MessageBundle.class.getName());
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getPresentationName", null), getResourceString("PN_TRANSACTION_NAME"), getResourceString("PD_TRANSACTION_NAME"));
        add(new PropertyDescriptorEx("comment", beanClass), getResourceString("PN_TRANSACTION_COMMENT"), getResourceString("PD_TRANSACTION_COMMENT"));
    }
}
