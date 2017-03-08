/** $Id: ErrorBundle.java,v 1.2 2006/05/03 06:36:49 zha Exp $ */
package com.developmentontheedge.beans.logging;

/**
 * ErrorBundle allows to ErrorManager get descriptions and formats for EnumeratedExceptions.
 */
public interface ErrorBundle
{
    public String getFormat(int code);

    public String getDescription(int code);

    public String describe(EnumeratedException e);
}
