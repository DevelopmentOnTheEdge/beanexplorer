package com.developmentontheedge.beans.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Entity;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Various HTML-related utils
 * @author lan
 */
public class HtmlUtil
{
    private static DTD dtd;
    private static final Pattern entityPattern = Pattern.compile("&(\\#?[\\w]+);");
    private static final Pattern stripHtmlPattern = Pattern.compile("<[^>]+>");
    
    static
    {
        try
        {
            new ParserDelegator();  // to initialize DTD
            dtd = DTD.getDTD("html32");
        }
        catch( IOException e )
        {
        }
    }
    
    /**
     * Converts character entity value into character.
     * Example input: gt
     * Example output: >
     * @param entity - entity to convert
     * @return
     */
    public static String convertEntity(String entity)
    {
        if(entity.startsWith("#"))
        {
            try
            {
                int code = entity.startsWith("#x")?Integer.parseInt(entity.substring(2), 16):Integer.parseInt(entity.substring(1));
                return new String(new int[] {code}, 0, 1);
            }
            catch( NumberFormatException e )
            {
                return null;
            }
        }
        Entity entityValue = dtd.getEntity(entity);
        if(entityValue != null) return entityValue.getString();
        return null;
    }
    
    /**
     * Strips tags and converts entities into characters
     * @param html
     * @return
     */
    public static String stripHtml(String html)
    {
        if(html == null) return null;
        html = stripHtmlPattern.matcher(html).replaceAll("");
        while(true)
        {
            Matcher matcher = entityPattern.matcher(html);
            if(matcher.find())
            {
                String entity = matcher.group(1);
                String entityValue = convertEntity(entity);
                if(entityValue != null)
                    html = matcher.replaceFirst(entityValue);
                else
                    html = matcher.replaceFirst("["+entity+"]");
            } else break;
        }
        return html;
    }
}
