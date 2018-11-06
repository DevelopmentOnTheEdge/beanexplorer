package com.developmentontheedge.beans.integration.composite;

import com.developmentontheedge.beans.Option;
import com.developmentontheedge.beans.annot.PropertyName;
import one.util.streamex.StreamEx;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Curve extends Option {

    private static final String TIME_VARIABLE = "time";
    private String path = "path1";
    private String name = TIME_VARIABLE;
    private String title = TIME_VARIABLE;

    private static final Map<String, String[]> pathToNameMap = new HashMap<String, String[]>() {{
        put("path1", new String[]{TIME_VARIABLE, "1_1", "1_2", "1_3", "1_4", "1_5"});
        put("path2", new String[]{TIME_VARIABLE, "2_1", "2_2", "2_3", "2_4", "2_5"});
        put("path3", new String[]{TIME_VARIABLE, "3_1", "3_2", "3_3", "3_4", "3_5"});
        put("path4", new String[]{TIME_VARIABLE, "4_1", "4_2", "4_3", "4_4", "4_5"});
        put("path5", new String[]{TIME_VARIABLE, "5_1", "5_2", "5_3", "5_4", "5_5"});
    }};
    private static final Map<String, String> nametoTitleMap = new HashMap<String, String>() {{
        for (String[] names : pathToNameMap.values())
            for (String name : names)
                put(name, name.replace('_', ':'));
    }};

    @PropertyName("Path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        String oldValue = this.path;
        this.path = path;
        firePropertyChange("path", oldValue, path);
        if (!Objects.equals(oldValue, path)) {
            setName(TIME_VARIABLE);
            setTitle(TIME_VARIABLE);
            firePropertyChange("*", null, null);
        }
    }

    @PropertyName("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;

        this.name = name;
        setTitle(nametoTitleMap.getOrDefault(name, name));


        firePropertyChange("name", oldValue, name);
    }

    public StreamEx<String> variables() {
        if (path == null)
            return StreamEx.empty();

        return StreamEx.of(pathToNameMap.getOrDefault(path, new String[]{""}));
    }

    public StreamEx<String> modules() {
        return StreamEx.of(pathToNameMap.keySet());
    }

    @PropertyName("Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String oldValue = this.title;
        this.title = title;
        firePropertyChange("title", oldValue, title);
    }
}
