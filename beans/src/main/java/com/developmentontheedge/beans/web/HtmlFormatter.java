package com.developmentontheedge.beans.web;

import java.io.Writer;

public interface HtmlFormatter
{
    void generateHeader(Writer out, String beanTitle) throws Exception;
    void generateLevelStart(Writer out, int level) throws Exception;
    void generateLevelEnd(Writer out, int level) throws Exception;
    void generateFooter(Writer out) throws Exception;

    void generateProperty(Writer out, int level, String name, String completeName, String value, boolean readOnly, boolean canBeNull)
            throws Exception;
}
