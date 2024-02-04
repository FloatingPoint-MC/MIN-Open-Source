package cn.floatingpoint.min.system.irc;

import cn.floatingpoint.min.management.Managers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client {
    private static String username;
    private static String password;
    private static boolean loggedIn = false;
    private static List<String> status = Collections.singletonList("\247e" + Managers.i18NManager.getTranslation("idle"));
    private static String vexViewVersion;
    private static byte[] modList;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Client.password = password;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    public static List<String> getStatus() {
        return status;
    }

    public static void setStatus(String... status) {
        Client.status = Arrays.asList(status);
    }

    public static void setVexViewVersion(String vexViewData) {
        vexViewVersion = vexViewData;
    }

    public static String getVexViewVersion() {
        return vexViewVersion;
    }

    public static void setModList(byte[] modList) {
        Client.modList = modList;
    }

    public static byte[] getModList() {
        return modList;
    }
}
