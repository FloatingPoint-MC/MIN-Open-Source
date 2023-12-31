import cn.floatingpoint.min.MIN;

public class VersionTest {
    public static void main(String[] args) {
        if (!VersionInfo.getVersion().equals(MIN.VERSION)) {
            System.exit(-1);
        }
    }
}
