package cn.floatingpoint.min.utils.client;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class IOUtil {
    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static byte[] readZipEntry(ZipEntry entry, ZipFile file) {
        try (InputStream inputStream = file.getInputStream(entry)) {
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static void writeToZip(ZipEntry entry, ZipOutputStream out, JSONObject json) {
        try {
            out.putNextEntry(entry);
            out.write(json.toString().getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        } catch (IOException ignored) {
        }
    }
}
