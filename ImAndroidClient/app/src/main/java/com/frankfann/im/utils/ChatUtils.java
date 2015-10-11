package com.frankfann.im.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 2015/10/8.
 */
public class ChatUtils {
    public static String getJsonFromMap(HashMap<String, String> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("\"");
            sb.append(entry.getKey());
            sb.append("\"");
            sb.append(":");
            sb.append("\"");
            String value=entry.getValue();
            try {
                value= URLEncoder.encode(value, "utf-8");
            } catch (Exception e) {
            }
            sb.append(value);
            sb.append("\"");
            sb.append(",");

        }
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }
}
