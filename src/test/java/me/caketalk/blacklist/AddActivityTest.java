package me.caketalk.blacklist;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author rock created at 20:26 28/03/13
 */
@RunWith(RobolectricTestRunner.class)
public class AddActivityTest {

    @Test
    public void shouldHaveProperAppName() throws Exception {
        String appName = new AddActivity().getResources().getString(R.string.lbl_add);
        Assert.assertEquals("New Number", appName);
    }

}
