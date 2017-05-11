/** $Id: CompositeEditorBean.java,v 1.2 2014/02/07 07:24:51 lan Exp $ */
package com.developmentontheedge.beans;

public class CompositeEditorBean 
{
    SimpleBean bean;
    public SimpleBean getBean()                     { return bean; }
    public void  setBean(SimpleBean bean)           { this.bean = bean; }

    HiddenBean hiddenBean;
    public HiddenBean getHiddenBean()               { return hiddenBean; }
    public void  setHiddenBean(HiddenBean bean)     { this.hiddenBean = bean; }
}


