package com.developmentontheedge.enterprise;

import java.util.Arrays;
import java.util.List;

public class Utils {

    /**
     * Shows not very long stacktrace of exception without misleading "Exception" word in it.
     * @param exc
     * @return
     */
    public static String trimStackAsString( Throwable exc )
    {
        return trimStackAsString( exc, 7 );
    }

    public static String trimStackAsString( Throwable exc, int nLines )
    {
        StringBuilder sb = new StringBuilder();

        List<StackTraceElement> stackList = Arrays.asList( exc.getStackTrace() );
        if( stackList.size() > nLines )
        {
            stackList = stackList.subList( 0, nLines );
        }

        for( StackTraceElement stackEl : stackList )
        {
            sb.append( "     at " ).append( stackEl.toString() ).append( "\n" );
        }
        return sb.toString();
    }

}
