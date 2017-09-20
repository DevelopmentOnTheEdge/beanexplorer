package com.developmentontheedge.beans;

import javax.swing.Action;

/**
 * This interface allows to specify what particular actions (for example open, delete)
 * can be applied to selected bean.
 */
public interface ActionsProvider
{
  public Action[]  getActions(Object bean);
}
