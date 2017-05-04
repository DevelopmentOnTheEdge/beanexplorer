package com.developmentontheedge.beans.json;

import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonArrayBuilder;

import static org.junit.Assert.*;

public class JsonFactoryTest
{
    @Test
    public void testCreateDictionary() throws Exception
    {
        JsonArrayBuilder dictionary = JsonFactory.createDictionary(
                new String[]{"foo", "bar"}, true);
        assertEquals("[['0','foo'],['1','bar']]", oneQuotes(dictionary.build().toString()));
    }

    @Test
    @Ignore
    public void testDpsValuesMetaDictionaries() throws Exception
    {
        JsonArrayBuilder dictionary = JsonFactory.createDictionary(
                new String[]{"foo", "bar"}, true);
        assertEquals("{"+
                   "values:['p1':'a'," +
                           "'p2':{'c1':'p21','c2':'p22'}" +
                           "]," +
                   "meta:['p1':{'title':'p1','readOnly':true}," +
                         "'p2':{'title':'p2','readOnly':true}," +
                         "'/p2/c1':{title:'c1','readOnly':true}" +
                         "]," +
                   "dictionaries:{'p1':{'key1':'value1','key2':'value2'}" +
        "}", oneQuotes(dictionary.build().toString()));
    }

    private String oneQuotes(String s)
    {
        return s.replace("\"", "'");
    }

}