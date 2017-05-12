package com.developmentontheedge.beans.json;

import javax.json.JsonException;
import javax.json.JsonObject;

public interface JsonSerializable
{
    /**
     * Serialize editor to JSONObject including current value and all necessary information
     * to render editor elsewhere (allowed values, etc.)
     */
    JsonObject toJson() throws JsonException;

    /**
     * Extract value from JSONObject and set it to the edited property.
     * Note that editor.fromJSON(editor.toJson()) should not harm the object.
     * If object contains unwanted fields, they should be ignored.
     * @param input - JSONObject to get value from
     */
    void fromJson(JsonObject input) throws JsonException;
}
