package com.developmentontheedge.beans.json;


public interface JsonSerializable
{
    Object getJsonValues();

    void setJsonValues(Object jsonValues);

    Object getJsonMeta();
}
