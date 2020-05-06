package com.developmentontheedge.beans;

import org.junit.Test;

import static com.developmentontheedge.beans.BeanInfoConstants.GROUP_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.TAB_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.TAB_NAME;
import static org.junit.Assert.assertEquals;

public class DynamicPropertyDoubleBraceBuilderTest
{
    @Test
    public void build()
    {
        DynamicPropertySetSupport params = new DynamicPropertySetSupport();
        params.add(new DPBuilder("name") {{
            readonly = true;
            tags = new String[][]{};
            multiple = true;
            value = "test";
            attr(GROUP_ID, 0);
            attr(TAB_ID, 1);
            attr(TAB_NAME, "tab 1");
        }}.build());

        assertEquals("test", params.getValue("name"));
        assertEquals(true, params.getProperty("name").isReadOnly());
        assertEquals(0, params.getProperty("name").getAttribute(GROUP_ID));
        assertEquals(1, params.getProperty("name").getAttribute(TAB_ID));
        assertEquals("tab 1", params.getProperty("name").getAttribute(TAB_NAME));

        new DPBuilder(params.getProperty("name")) {{
            value = null;
        }}.build();

        assertEquals(null, params.getValue("name"));
    }

}
