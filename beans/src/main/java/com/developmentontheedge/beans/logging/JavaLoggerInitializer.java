/** $Id: JavaLoggerInitializer.java,v 1.1 2006/12/19 15:43:00 sintetik Exp $ */
package com.developmentontheedge.beans.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;

public class JavaLoggerInitializer
{
    private JavaLoggerInitializer()
    {
    }

    public static void initHandlers(Object loggingHandleSeed, String filePattern)
    {
        try
        {
            java.util.logging.Logger realLogger = (java.util.logging.Logger)loggingHandleSeed;
            realLogger.addHandler( new ConsoleHandler() );

            if (filePattern == null)
                return;

            FileHandler fh = new FileHandler( filePattern );
            fh.setFormatter( new JavaLogFormatter() );
            realLogger.addHandler( fh );
        } catch ( Throwable t )
        {
            System.err.println("Unable to add file hanlder to Java Logger");
        }
    }
}
