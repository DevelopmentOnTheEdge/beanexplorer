package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import static com.developmentontheedge.beans.jsontest.DpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryTest
{

    @Test
    public void testAddToJsonArrayNullValue(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class));

        JsonFactory.addValueToJsonArray(json, dps);

        //JsonFactory.addValueToJsonArray(json, new JsonFactoryTest());

        JsonArray buildJson = json.build();
        assertEquals(1, buildJson.size());

        assertEquals("[{}]", oneQuotes(buildJson.toString()));
    }

    @Test
    public void testAddToJsonArray(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class, "test"));

        JsonFactory.addValueToJsonArray(json, dps);

        //JsonFactory.addValueToJsonArray(json, new JsonFactoryTest());

        JsonArray buildJson = json.build();
        assertEquals(1, buildJson.size());

        assertEquals("[{'name':'test'}]", oneQuotes(buildJson.toString()));
    }

//    @Test
//    public void testAddToJsonObject(){
//        JsonObjectBuilder json = Json.createObjectBuilder();
//        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
//        dps.add(new DynamicProperty("name", String.class));
//
//        JsonFactory.addToJsonObject(json,"unknownClass", new JsonFactoryTest(), JsonFactoryTest.class);
//
//        JsonObject buildJson = json.build();
//        assertEquals(0, buildJson.size());
//    }
//
//    @Test
//    public void testEncodeColor() throws Exception
//    {
//        Color color = Color.CYAN;
//        assertEquals("["+color.getRed()+","+color.getGreen()+","+color.getBlue()+"]",
//                JsonFactory.encodeColor(color).build().toString());
//    }
//
//    @Test
//    public void testEncodeColorNull() throws Exception
//    {
//        Color color = new Color(128,128,128,0);
//        assertEquals("[]", JsonFactory.encodeColor(color).build().toString());
//
//        assertEquals("[]", JsonFactory.encodeColor(null).build().toString());
//    }
}