package com.developmentontheedge.beans.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JavaLogFormatter extends Formatter
{

    Date dat = new Date();
    private final static String format = "{0,date} {0,time}";
    private MessageFormat formatter;

    private final Object args[] = new Object[1];

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record)
    {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
        dat.setTime( record.getMillis() );
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if( formatter == null )
        {
            formatter = new MessageFormat( format );
        }
        formatter.format( args, text, null );
        sb.append( text );
        String message = formatMessage( record );
        sb.append( " " );
        sb.append( message );
        sb.append( '\n' );
        if( record.getThrown() != null )
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter( sw );
                record.getThrown().printStackTrace( pw );
                pw.close();
                sb.append( sw.toString() );
            }
            catch( Exception ignore )
            {
            }
        }
        return sb.toString();
    }
}
