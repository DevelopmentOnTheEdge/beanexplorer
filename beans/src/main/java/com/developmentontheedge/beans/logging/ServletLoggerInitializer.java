package com.developmentontheedge.beans.logging;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.StringTokenizer;

public class ServletLoggerInitializer extends GenericServlet
{
    private static ServletLogger logger;

    @Override
    public String getServletInfo()
    {
        return "Servlet Logger Initializer";
    }

    @Override
    public void init() throws ServletException
    {
        int mask = AbstractLogger.MASK_FATAL + AbstractLogger.MASK_ERROR;
        String noJCS = getInitParameter( "noJCS" );
        String sm = getInitParameter( "enabledMask" );
        if( sm != null )
        {
            try {
                mask = Integer.parseInt( sm );
            }
            catch (NumberFormatException e) {
                mask = 0;
                StringTokenizer st = new StringTokenizer(sm, ",");
                while (st.hasMoreTokens())
                {
                    String token = st.nextToken();
                    if (token.equalsIgnoreCase(AbstractLogger.FATAL_TYPE))
                        mask += AbstractLogger.MASK_FATAL;
                    else if (token.equalsIgnoreCase(AbstractLogger.ERROR_TYPE))
                        mask += AbstractLogger.MASK_ERROR;
                    else if (token.equalsIgnoreCase(AbstractLogger.WARN_TYPE))
                        mask += AbstractLogger.MASK_WARN;
                    else if (token.equalsIgnoreCase(AbstractLogger.INFO_TYPE))
                        mask += AbstractLogger.MASK_INFO;
                    else if (token.equalsIgnoreCase(AbstractLogger.DEBUG_TYPE))
                        mask += AbstractLogger.MASK_DEBUG;
                }
            }
        }
        setLogger( new ServletLogger( this, mask, noJCS != null ) );
        // force using servlet logger
        Logger.useServletLogging();
    }

    @Override
    public void service( ServletRequest req, ServletResponse resp )
    {
    }

    public synchronized static ServletLogger getLogger()
    {
        return logger;
    }

    private synchronized static void setLogger( ServletLogger logger )
    {
        ServletLoggerInitializer.logger = logger;
    }
}
