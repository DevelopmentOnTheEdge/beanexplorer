package com.developmentontheedge.beans.logging;

import java.io.Serializable;

public class LoggingHandle implements Serializable
{
    public LoggingHandle(Object handle)
    {
        this.handle = handle;
    }

    // either String or Class, so serializable
    Object handle;

    protected Boolean isFatalEnabled;
    protected Boolean isErrorEnabled;
    protected Boolean isWarnEnabled;
    protected Boolean isInfoEnabled;
    protected Boolean isDebugEnabled;
}
