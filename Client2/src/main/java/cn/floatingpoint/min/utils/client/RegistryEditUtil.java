package cn.floatingpoint.min.utils.client;

import java.util.prefs.Preferences;

public class RegistryEditUtil {
    public static void writeValue(String name, String key, String value) {
        Preferences pre = Preferences.userRoot().node(name);
        pre.put(key, value);
    }
    public static String getValue(String name, String key) {
        Preferences pre = Preferences.userRoot().node(name);
        return pre.get(key, "");
    }
}
