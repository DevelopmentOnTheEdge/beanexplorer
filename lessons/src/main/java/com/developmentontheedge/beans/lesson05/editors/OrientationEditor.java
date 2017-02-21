package com.developmentontheedge.beans.lesson05.editors;

import com.developmentontheedge.beans.editors.TagEditorSupport;


public class OrientationEditor extends TagEditorSupport
{
     
     public OrientationEditor()
     {
         super(new String[] { "Vertical","Horizontal" }  , 0);
     }
}