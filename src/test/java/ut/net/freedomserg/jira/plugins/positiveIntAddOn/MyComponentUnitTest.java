package ut.net.freedomserg.jira.plugins.positiveIntAddOn;

import org.junit.Test;
import net.freedomserg.jira.plugins.positiveIntAddOn.api.MyPluginComponent;
import net.freedomserg.jira.plugins.positiveIntAddOn.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}