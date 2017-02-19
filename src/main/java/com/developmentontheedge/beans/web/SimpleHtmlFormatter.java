package com.developmentontheedge.beans.web;

import java.io.Writer;

public class SimpleHtmlFormatter implements HtmlFormatter
{
    String nl = "\n";
    public static final String LEVEL_OFFSET = "&nbsp;&nbsp;";

    @Override
    public void generateHeader(Writer out, String beanTitle) throws Exception
    {
        out.write("<h1>" + beanTitle + "</h1>" + nl);
    }

    @Override
    public void generateProperty(Writer out, int level, String name, String completeName, String value,
                                 boolean readOnly, boolean canBeNull) throws Exception
    {
        while(level-- > 0)
            out.write(LEVEL_OFFSET);


        out.write("<b>" + name + ": </b>");
        out.write(value + "<br>" + nl);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Empty functions
    //

    @Override
    public void generateLevelStart(Writer out, int level) throws Exception  {}
    @Override
    public void generateLevelEnd(Writer out, int level) throws Exception    {}
    @Override
    public void generateFooter(Writer out) throws Exception                 {}
}
