package cn.floatingpoint.min.utils.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWIDUtil {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(getHWID());
    }
    public static String getHWID() throws NoSuchAlgorithmException {
        StringBuilder s = new StringBuilder();
        String main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME");
        byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5 = messageDigest.digest(bytes);
        for (byte b : md5) {
            s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : s.toString().toCharArray()) {
            if (((Character) s.toString().charAt(2)).equals(c) || ((Character) s.toString().charAt(5)).equals(c) || ((Character) s.toString().charAt(6)).equals(c))
                continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString() + stringBuilder.toString().charAt(1) + stringBuilder.toString().charAt(2) + stringBuilder.toString().charAt(8);
    }
}
