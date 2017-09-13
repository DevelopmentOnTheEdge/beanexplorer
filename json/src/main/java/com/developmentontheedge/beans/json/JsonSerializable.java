package com.developmentontheedge.beans.json;

/**
 * Object which supports serializing to JSON
 * @author lan
 */
public interface JsonSerializable
{
    /**
     * Serialize editor to JSONObject including current value and all necessary information
     * to render editor elsewhere (allowed values, etc.)
     */
    Object toJSON();

}
