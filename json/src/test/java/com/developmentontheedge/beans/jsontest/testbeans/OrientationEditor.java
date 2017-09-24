package com.developmentontheedge.beans.jsontest.testbeans;

import com.developmentontheedge.beans.editors.TagEditorSupport;

public class OrientationEditor extends TagEditorSupport
{
    public static String Vertical = "Vertical";
    public static String Horizontal = "Horizontal";
    public static String[] orientations = new String[] { Vertical,Horizontal };

    public OrientationEditor()
    {
        super(orientations, 0);
    }
}
