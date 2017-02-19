package com.developmentontheedge.beans.log;

public class StderrLogger implements BeanLogger
{
    @Override
    public void warn(String msg)
    {
        System.err.println("[WARN] "+msg);
    }

    @Override
    public void warn(String msg, Throwable t)
    {
        System.err.println("[WARN] "+msg);
        t.printStackTrace();
    }

    @Override
    public void error(String msg)
    {
        System.err.println("[ERROR] "+msg);
    }

    @Override
    public void error(String msg, Throwable t)
    {
        System.err.println("[ERROR] "+msg);
        t.printStackTrace();
    }
}
