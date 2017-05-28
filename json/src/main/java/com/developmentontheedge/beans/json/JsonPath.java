package com.developmentontheedge.beans.json;

public class JsonPath
{
    private String path = "";

    public JsonPath(){}

    public JsonPath(String path){
        this.path = path;
    }

    public JsonPath append(String path){
        return new JsonPath(this.path + "/" + path);
    }

    public String get(){
        return path;
    }

    @Override
    public String toString()
    {
        return get();
    }
}
