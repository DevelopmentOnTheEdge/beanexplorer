package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryTest
{

    @Test
    public void testAddToJsonArray(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class));

        JsonFactory.addToJsonArray(json, dps);

        JsonFactory.addToJsonArray(json, new JsonFactoryTest());

        JsonArray buildJson = json.build();
        assertEquals(1, buildJson.size());

        assertEquals("[{'name':null}]", oneQuotes(buildJson.toString()));
    }

    @Test
    public void testAddToJsonObject(){
        JsonObjectBuilder json = Json.createObjectBuilder();
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty("name", String.class));


        JsonFactory.addToJsonObject(json,"unknownClass", new JsonFactoryTest(), JsonFactoryTest.class);

        JsonObject buildJson = json.build();
        assertEquals(0, buildJson.size());
    }
}