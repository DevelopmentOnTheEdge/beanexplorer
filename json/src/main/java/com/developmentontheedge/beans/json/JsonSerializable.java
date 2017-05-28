package com.developmentontheedge.beans.json;

import javax.json.JsonException;
import javax.json.JsonObject;

public interface JsonSerializable
{
    /**
     * Serialize editor to JsonObject including current value and all necessary information
     * to render editor elsewhere (allowed values, etc.)
     */
    JsonObject toJson() throws JsonException;

    /**
     * Extract value from JsonObject and set it to the edited property.
     * Note that editor.fromJson(editor.getPropertyMeta()) should not harm the object.
     * If object contains unwanted fields, they should be ignored.
     * @param input - JsonObject to get value from
     */
    void fromJson(JsonObject input) throws JsonException;
}
