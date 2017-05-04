package com.developmentontheedge.beans.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FieldMapTest
{

    @Test
    public void testFieldMap()
    {
        FieldMap map = FieldMap.ALL;
        assertTrue(map.contains("blahblah"));
        map = new FieldMap(null);
        assertTrue(map.contains("blahblah"));
        map = new FieldMap("");
        assertTrue(map.contains("blahblah"));
        map = new FieldMap("test");
        assertFalse(map.contains("blahblah"));
        assertTrue(map.contains("test"));
        map = new FieldMap("test; a/b/c; test/a");
        assertEquals("{a={b={c={}}}, test={a={}}}", map.toString());
        map = map.get("a");
        assertEquals("{b={c={}}}", map.toString());
        map = map.get("b");
        assertTrue(map.contains("c"));
        assertFalse(map.contains("blahblah"));
        map = map.get("c");
        assertTrue(map.contains("blahblah"));
        map = map.get("d");
        assertTrue(map.contains("blahblah"));
    }
}