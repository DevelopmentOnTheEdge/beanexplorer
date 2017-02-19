package com.developmentontheedge.beans.util;

import java.util.StringTokenizer;

public class SmartText
{
    protected String text;

    public SmartText(String s)
    {
        text = s;
    }

    public SmartText()
    {
        text = "";
    }

    @Override
    public String toString()
    {
        return text;
    }

    public void setText(String s)
    {
        text = s;
    }

    public String getText()
    {
        return text;
    }

    /**
     * Breaks string into several substrings at standard delimiter characters using iWidth
     * as top limit of substring's length; inserts sBreak strings on break positions;
     * if bArticles is true, avoids placing breaks after a, an, and the.
     */

    public String insertBreaks(String sBreak, int iWidth, boolean bArticles)
    {
        if( text.length() <= iWidth )
        {
            return text;
        }
        StringBuffer result = new StringBuffer( "" );
        StringTokenizer st = new StringTokenizer( text );
        if( !st.hasMoreTokens() )
        {
            return "";
        }
        String tok2 = st.nextToken();
        int iPos = tok2.length();
        String tok1 = null;
        while( st.hasMoreTokens() )
        {
            tok1 = tok2;
            tok2 = st.nextToken();
            iPos += tok2.length() + 1;
            if( iPos >= iWidth )
            {
                if( bArticles && ( tok1.equals( "a" ) || tok1.equals( "an" ) || tok1.equals( "the" ) ) )
                {
                    result.append( sBreak + tok1 + " " + tok2 + " " );
                }
                else
                {
                    result.append( tok1 + sBreak + tok2 + " " );
                }
                if( st.hasMoreTokens() )
                {
                    tok2 = st.nextToken();
                    iPos = tok2.length();
                }
                else
                {
                    tok2 = "";
                }
            }
            else
            {
                result.append( tok1 + " " );
            }
        }
        result.append( tok2 );
        return result.toString();
    }
}
