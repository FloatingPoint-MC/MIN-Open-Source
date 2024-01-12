package cn.floatingpoint.min.utils.client;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class MiscUtil {
    public static String getRemoteIP() {
        int abroad = getAddressByIP(getOutIPV4());
        return abroad == 1 ? "global-irc.minclient.xyz" : "irc.minclient.xyz";
    }

    private static int getAddressByIP(String ip) {
        try {
            URL url = new URL("https://opendata.baidu.com/api.php?query=" + ip + "&co=&resource_id=6006&t=1433920989928&ie=utf8&oe=utf-8&format=json");
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            JSONObject jsStr = new JSONObject(result.toString());
            JSONArray jsData = (JSONArray) jsStr.get("data");
            JSONObject data = (JSONObject) jsData.get(0);//位置
            String[] provinces = {"北京市", "天津市", "河北省", "山西省", "内蒙古", "辽宁省", "吉林省", "黑龙江省", "上海市", "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省", "广东省", "广西", "海南省", "重庆市", "四川省", "贵州省", "云南省", "西藏", "陕西省", "甘肃省", "青海省", "宁夏", "新疆", "台湾省", "香港", "澳门"};
            String address = (String) data.get("location");
            boolean china = false;
            for (String province : provinces) {
                if (address.startsWith(province)) {
                    china = true;
                    break;
                }
            }
            return china ? 0 : 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getOutIPV4() {
        String myIp = "https://api.myip.la/";

        StringBuilder inputLine = new StringBuilder();
        String read;
        URL url;
        HttpsURLConnection urlConnection;
        BufferedReader in = null;
        try {
            url = new URL(myIp);
            urlConnection = (HttpsURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            while ((read = in.readLine()) != null) {
                inputLine.append(read);
            }
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return inputLine.toString().replace("\r", "").replace("\n", "");
    }
}
