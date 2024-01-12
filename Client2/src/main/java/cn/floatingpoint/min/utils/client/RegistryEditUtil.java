package cn.floatingpoint.min.utils.client;

import java.util.prefs.Preferences;

public class RegistryEditUtil {
    public static void writeValue(String parent, String name, String key, String value) {
        Preferences pre = Preferences.userRoot().node("/" + parent + "/" + name);
        pre.put(key, value);
    }
    public static String getValue(String parent, String name, String key) {
        Preferences pre = Preferences.userRoot().node("/" + parent + "/" + name);
        return pre.get(key, "");
    }
}
