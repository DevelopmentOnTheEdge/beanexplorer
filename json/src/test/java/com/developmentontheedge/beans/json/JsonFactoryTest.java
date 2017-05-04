package com.developmentontheedge.beans.json;

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
        assertEquals("[[\"0\",\"foo\"],[\"1\",\"bar\"]]",dictionary.build().toString());
    }

}