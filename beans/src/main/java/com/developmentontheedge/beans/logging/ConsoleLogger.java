/** $Id: ConsoleLogger.java,v 1.8 2006/12/19 15:43:00 sintetik Exp $ */

package com.developmentontheedge.beans.logging;

class ConsoleLogger extends AbstractMaskedLogger
{
    // needed for ant running (like JUnit tasks)
    private boolean redirectOut2Err = false;

    ConsoleLogger()
    {
        this(false);
    }

    ConsoleLogger(boolean redirectOut2Err)
    {
        super(MASK_DEBUG | MASK_INFO | MASK_WARN | MASK_ERROR | MASK_FATAL);
        this.redirectOut2Err = redirectOut2Err;
    }

    protected final void doLog(LoggingHandle handle, String type, String message, Throwable details)
    {
        if (!doIsLogEnabled(handle, type))
            return;

        java.io.PrintStream stream;
        if (redirectOut2Err || type.equals(ERROR_TYPE) || type.equals(FATAL_TYPE))
            stream = System.err;
        else
            stream = System.out;

        String msg = type + ": " + handle.handle;
        if (message != null)
            msg += ": " + message;

        stream.println(msg);
        if (details != null)
            details.printStackTrace(stream);
    }
}
