import cn.floatingpoint.min.MIN;
import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
    @Test
    public void testVersion() {
        Assert.assertEquals(VersionInfo.getVersion(), MIN.VERSION);
    }
}
