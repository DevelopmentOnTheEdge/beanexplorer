package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.awt.Color;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonBeanFactoryTest {

    @Test
    public void testEncodeColor() throws Exception
    {
        Color color = Color.CYAN;
        assertEquals("["+color.getRed()+","+color.getGreen()+","+color.getBlue()+"]",
                JsonBeanFactory.encodeColor(color).build().toString());
    }

    @Test
    public void testEncodeColorNull() throws Exception
    {
        Color color = new Color(128,128,128,0);
        assertEquals("[]", JsonBeanFactory.encodeColor(color).build().toString());

        assertEquals("[]", JsonBeanFactory.encodeColor(null).build().toString());
    }
}