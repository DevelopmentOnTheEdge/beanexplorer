package com.developmentontheedge.beans;

import java.beans.PropertyChangeListener;

public interface PropertyChangeObservable
{
    public void addPropertyChangeListener(PropertyChangeListener pcl);
    public void removePropertyChangeListener(PropertyChangeListener pcl);
}
