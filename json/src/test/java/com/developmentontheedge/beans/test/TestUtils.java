package com.developmentontheedge.beans.test;

public class TestUtils
{
    public static String oneQuotes(Object s)
    {
        return s.toString().replace("\"", "'");
    }

    public static String doubleQuotes(String s)
    {
        return s.replace("'", "\"");
    }

}
